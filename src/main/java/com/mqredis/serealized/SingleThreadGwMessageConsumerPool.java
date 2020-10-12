package com.mqredis.serealized;

import com.mqredis.api.*;

import java.util.concurrent.*;

public class SingleThreadGwMessageConsumerPool implements GwMessageConsumerPool {

    private GwMessageConsumer consumer;
    private GwMessageQueue queue;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Future<Integer> task;

    class ConsumerTask implements Runnable {
        public void run() {
            while(true) {
                if (executorService.isShutdown()) {
                    System.out.println("Stop consuming new messages");
                    break;
                }
                try {
                    GwMessage msg = queue.blockingGet();
                    consumer.consume(msg);
                } catch (GwQueueException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    System.out.println("Interrupted when wait for new message");
                    break;
                }
            }
        }
    }

    public void start(final GwMessageConsumer consumer, final GwMessageQueue queue) {
        this.consumer = consumer;
        this.queue = queue;
        this.task = this.executorService.submit(new ConsumerTask(), null);
    }

    public void shutdown() {
        this.task.cancel(true);
        this.executorService.shutdown();
    }

    public boolean waitTerminatedForMillis(long timeout) throws InterruptedException {
        return this.executorService.awaitTermination(timeout, TimeUnit.MILLISECONDS);
    }
}
