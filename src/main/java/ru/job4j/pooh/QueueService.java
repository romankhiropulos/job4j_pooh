package ru.job4j.pooh;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@ThreadSafe
public class QueueService implements Service {

    @GuardedBy("this")
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> map = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        String text;
        Resp resp;
        switch (req.httpRequestType()) {
            case "GET" -> {
                this.map.putIfAbsent(req.getSourceName(), new ConcurrentLinkedQueue<>());
                text = this.map.get(req.getSourceName()).poll();
                resp = Objects.equals(text, null) ? new Resp("", "204 No Content")
                        : new Resp(text, "200 OK");
            }
            case "POST" -> {
                this.map.putIfAbsent(req.getSourceName(), new ConcurrentLinkedQueue<>());
                this.map.get(req.getSourceName()).add(req.getParam());
                resp = new Resp("", "200 OK");
            }
            default -> {
                resp = new Resp("", "404, Not Found");
            }
        }
        return resp;
    }
}
