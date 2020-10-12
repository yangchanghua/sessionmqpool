package com.mqredis.serealized;

import com.mqredis.api.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadGwMessageProducerPool implements GwMessageProducerPool {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void start(final GwMessageProducer producer, final GwMessageQueue queue) {
        while(true) {
            if (this.executorService.isShutdown()) {
                System.out.println("Producer pool was shutdown, stop producing new messages");
                break;
            }

            executorService.submit(new Runnable() {
                public void run() {
                    GwMessage msg = producer.produce();
                    if (msg == null) {
                        System.out.println("No more messages");
                        SingleThreadGwMessageProducerPool.this.executorService.shutdown();
                    }
                    try {
                        queue.offer(msg);
                    } catch (GwQueueException e) {
                        System.out.println(e.getMessage());
                    }
                }
            });
        }
    }

    public void shutdown() {
        this.executorService.shutdown();
    }
}
