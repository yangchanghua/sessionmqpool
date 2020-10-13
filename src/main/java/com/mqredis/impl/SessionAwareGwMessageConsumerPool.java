package com.mqredis.impl;

import com.mqredis.api.*;

import java.util.concurrent.*;

public class SessionAwareGwMessageConsumerPool implements GwMessageConsumerPool {
    private GwMessageQueue queue;
    private GwMessageConsumer consumer;
    private SessionedTaskManager taskManager;
    private final ExecutorService dispatcherThread;
    private final ExecutorService workerPool;
    private int maxPoolSize = 100;
    private Future<?> mainTask;

    public SessionAwareGwMessageConsumerPool(int coreSize, int maxSize) {
        this.dispatcherThread = Executors.newSingleThreadExecutor();
        this.workerPool = new ThreadPoolExecutor(coreSize, maxSize, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        this.maxPoolSize = maxSize;
    }

    class MainTask implements Runnable {
        public void run() {
            while (true) {
                if (dispatcherThread.isShutdown()) {
                    System.out.println("main pool is shutdown");
                    break;
                }
                try {
                    GwMessage msg = queue.blockingGet();
                    taskManager.handleMessage(msg);
                } catch (GwQueueException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    System.out.println("interrupted when getting msg from queue");
                    if (dispatcherThread.isShutdown()) {
                        System.out.println("main task exit");
                        break;
                    }
                }
            }
        }
    }

    public void start(GwMessageConsumer consumer, GwMessageQueue queue) {
        this.consumer = consumer;
        this.queue = queue;
        this.taskManager = new SessionedTaskManager(this.consumer, this.workerPool, maxPoolSize);
        this.mainTask = this.dispatcherThread.submit(new MainTask());
    }

    public void shutdown() {
        this.workerPool.shutdown();
        this.dispatcherThread.shutdown();
        this.mainTask.cancel(true);
    }

    public boolean waitTerminatedForMillis(long timeout) throws InterruptedException {
        if (this.dispatcherThread.awaitTermination(timeout, TimeUnit.SECONDS) &&
                this.workerPool.awaitTermination(timeout, TimeUnit.SECONDS)) {
            System.out.println("All thread pool shutdown");
            return true;
        } else {
            System.out.println("Time out when wait for termination, there might be threads still running");
            return false;
        }
    }
}
