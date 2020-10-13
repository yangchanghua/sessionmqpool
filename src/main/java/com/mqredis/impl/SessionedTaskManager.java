package com.mqredis.impl;

import com.mqredis.api.GwMessage;
import com.mqredis.api.GwMessageConsumer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class SessionedTaskManager {

    private static final int MAX_MESSAGES_IN_RUNNING = 10000;
    private final ExecutorService executorService;

    private final LinkedBlockingQueue<SessionMessageQueue> queuePool;

    private final ConcurrentHashMap<Long, SessionMessageQueue> runningQueueMap;

    private final GwMessageConsumer consumer;

    private class SessionMessageQueue {
        private long id;
        private LinkedBlockingQueue<GwMessage> q;
        public SessionMessageQueue() {
            this.id = 0;
            this.q = new LinkedBlockingQueue<GwMessage>(MAX_MESSAGES_IN_RUNNING);
        }
    }

    public SessionedTaskManager(GwMessageConsumer consumer, ExecutorService executorService, int maxPoolSize) {
        this.consumer = consumer;
        this.executorService = executorService;
        this.queuePool = new LinkedBlockingQueue<SessionMessageQueue>(maxPoolSize);
        this.runningQueueMap = new ConcurrentHashMap<Long, SessionMessageQueue>(32);
        for (int i = 0; i < maxPoolSize; i++) {
            this.queuePool.add(new SessionMessageQueue());
        }
    }

    public void handleMessage(GwMessage msg) throws InterruptedException {

        Long session = msg.getSession();
        SessionMessageQueue queue = this.appendToRunningQueue(msg);
        if (queue == null) {
            queue = this.queuePool.take();
            if (!queue.q.offer(msg)) {
                System.out.println("Error: Queue taken from pool cannot accept the first message");
            } else {
                this.runningQueueMap.put(session, queue);
                this.submit(session, queue);
            }
        }
    }

    public boolean releaseSession(final Long session, final SessionMessageQueue queue) {
        synchronized (queue) {
            if (queue.q.isEmpty()) {
                this.runningQueueMap.remove(session);
                if (!this.queuePool.offer(queue)) {
                    System.out.println("ERROR: cannot return back the queue to pool");
                }
                return true;
            } else {
                System.out.println("Queue has new incoming messages when trying to release session " + session);
                return false;
            }
        }
    }

    private SessionMessageQueue appendToRunningQueue(GwMessage msg) {
        Long session = msg.getSession();
        SessionMessageQueue queue = this.runningQueueMap.get(session);
        if (queue != null) {
            synchronized (queue) {
                queue = this.runningQueueMap.get(session);
                if (queue != null) {
                    if (!queue.q.offer(msg)) {
                        System.out.println("Warning: Too many messages in running queue for session " + session);
                    }
                }
            }
        }
        return queue;
    }

    private void submit(Long session, SessionMessageQueue queue) {
        Future<?> task = this.executorService.submit(new MessageConsumerTask(session, this.consumer, queue));
    }

    class MessageConsumerTask implements Runnable {

        private Long session;

        private GwMessageConsumer consumer;

        private SessionMessageQueue queue;

        MessageConsumerTask(long session, GwMessageConsumer consumer, SessionMessageQueue queue) {
            this.session = session;
            this.consumer = consumer;
            this.queue = queue;
        }

        public void run() {
            while(true) {
                GwMessage msg = this.queue.q.poll();
                if (msg == null) {
                    if (releaseSession(session, queue)) {
                        break;
                    }
                } else {
                    this.consumer.consume(msg);
                }
            }
        }
    }
}
