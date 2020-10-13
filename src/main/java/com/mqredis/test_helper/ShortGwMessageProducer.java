package com.mqredis.test_helper;

import com.mqredis.api.GwMessage;
import com.mqredis.api.GwMessageQueue;
import com.mqredis.api.GwQueueException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ShortGwMessageProducer implements GwMessageProducer {
    private final GwMessageQueue queue;

    public ShortGwMessageProducer(GwMessageQueue queue) {
        this.queue = queue;
    }

    class MessageProduceTask implements Runnable {
        private final long session;
        private final int count;

        public MessageProduceTask(long session, int count) {
            this.session = session;
            this.count = count;
        }
        public void run() {
            for (int i = 0; i < count; i++) {
                GwMessage msg = new GwMessage(this.session, this.session + ":" + i);
                try {
                    queue.tryPut(msg);
                } catch (GwQueueException e) {
                    System.out.println("Failed to produce message, " + e.getMessage());
                    break;
                }
            }
        }
    }

    public void produce(int sessionCount, int countPerSession) throws GwQueueException {
        final ExecutorService executorService = Executors.newFixedThreadPool(sessionCount);
        for (int i = 0; i<sessionCount; i++) {
            executorService.submit(new MessageProduceTask(i, countPerSession));
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
