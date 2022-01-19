package ru.job4j.pooh;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@ThreadSafe
public class TopicService implements Service {

    @GuardedBy("this")
    private final Map<String, ConcurrentLinkedQueue<String>> map = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        String text;
        Resp resp;
        switch (req.httpRequestType()) {
            case "GET" -> {
                text =  this.map.putIfAbsent(req.getParam(), new ConcurrentLinkedQueue<>()) == null
                                ? ""
                                : this.map.get(req.getParam()).poll();
                resp = Objects.equals(text, "") ? new Resp(text, "204 No Content")
                        : new Resp(text, "200 OK");
            }
            case "POST" -> {
                this.map.values().forEach(queue -> queue.add(req.getParam()));
                resp = new Resp("New param inserted to queues!", "200 OK");
            }
            default -> {
                resp = new Resp("Not Found!", "404, Not Found");
            }
        }
        return resp;
    }
}
