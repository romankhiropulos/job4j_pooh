package ru.job4j.pooh;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@ThreadSafe
public class QueueService implements Service {

    @GuardedBy("this")
    private final Queue<String> queue = new ConcurrentLinkedQueue<>();

    @Override
    public Resp process(Req req) {
        String text;
        Resp resp;
        switch (req.httpRequestType()) {
            case "GET" -> {
                text = this.queue.poll();
                resp = Objects.equals(text, "") ? new Resp(text, "204 No Content")
                        : new Resp(text, "200 OK");
            }
            case "POST" -> {
                this.queue.add(req.getParam());
                resp = new Resp("New param inserted to queue!", "200 OK");
            }
            default -> {
                resp = new Resp("Not Found!", "404, Not Found");
            }
        }
        return resp;
    }
}
