package ru.job4j.pooh;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class QueueServiceTest {

    @Test
    public void whenPostThenGetQueue() {
        QueueService queueService = new QueueService();
        String paramForPostMethod = "temperature=18";
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod)
        );
        Resp result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(result.text(), is("temperature=18"));
    }

    @Test
    public void whenPostThenGetFiveTimesQueue() throws InterruptedException {
        final CopyOnWriteArrayList<String> buffer = new CopyOnWriteArrayList<>();
        final QueueService queue = new QueueService();
        Thread producer = new Thread(
                () -> IntStream.range(0, 5).forEach(
                        value -> {
                            queue.process(
                                    new Req("POST", "queue", "weather", String.valueOf(value))
                            );
                        }
                )
        );
        producer.start();
        Thread consumer = new Thread(
                () -> {
                    String text;
                    while (!Thread.currentThread().isInterrupted()) {
                        text = queue.process(
                                new Req("GET", "queue", "weather", null)
                        ).text();
                        if (Objects.nonNull(text) && !Objects.equals(text, "")) {
                            buffer.add(text);
                        }
                    }
                }
        );
        consumer.start();
        producer.join();
        Thread.sleep(3000);
        consumer.interrupt();
        Assert.assertThat(buffer, is(Arrays.asList("0", "1", "2", "3", "4")));
    }

    @Test
    public void whenPostThenGetQueueToo() {
        QueueService queueService = new QueueService();
        String weatherSourceName = "weather";
        String trafficSourceName = "traffic";
        String paramForWeatherSourceName = "temperature=18";
        String paramForTrafficSourceName = "cars=20188";

        queueService.process(
                new Req("POST", "queue", weatherSourceName, paramForWeatherSourceName)
        );
        queueService.process(
                new Req("POST", "queue", trafficSourceName, paramForTrafficSourceName)
        );
        Resp resultFromWeatherSource1 = queueService.process(
                new Req("GET", "queue", weatherSourceName, null)
        );
        Resp resultFromWeatherSource2 = queueService.process(
                new Req("GET", "queue", weatherSourceName, null)
        );
        Resp resultFromTrafficSource1 = queueService.process(
                new Req("GET", "queue", trafficSourceName, null)
        );
        Resp resultFromTrafficSource2 = queueService.process(
                new Req("GET", "queue", trafficSourceName, null)
        );
        assertThat(resultFromWeatherSource1.text(), is("temperature=18"));
        assertThat(resultFromWeatherSource2.text(), is(""));
        assertThat(resultFromTrafficSource1.text(), is("cars=20188"));
        assertThat(resultFromTrafficSource2.text(), is(""));
    }
}