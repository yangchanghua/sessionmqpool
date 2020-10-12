package com.mqredis.serealized;

import com.mqredis.api.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadGwMessageConsumerPool implements GwMessageConsumerPool {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void run(final GwMessageConsumer consumer, final GwMessageQueue queue) {
        this.executorService.submit(new Runnable() {
            public void run() {
                while(true) {
                    if (SingleThreadGwMessageConsumerPool.this.executorService.isShutdown()) {
                        System.out.println("Stop consuming new messages");
                        break;
                    }
                    try {
                        GwMessage msg = queue.poll();
                        consumer.consume(msg);
                    } catch (GwQueueException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void shutdown() {
        this.executorService.shutdown();
    }
}
