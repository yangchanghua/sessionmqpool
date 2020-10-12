package com.mqredis.serealized;

import com.mqredis.api.GwMessage;
import com.mqredis.api.GwMessageQueue;
import com.mqredis.api.GwQueueException;

import java.util.concurrent.LinkedBlockingQueue;

public class NaiveGwMQ implements GwMessageQueue {

    private int capacity;

    private LinkedBlockingQueue<GwMessage> q;

    public NaiveGwMQ(int capacity) {
        this.capacity = capacity;
        this.q = new LinkedBlockingQueue<GwMessage>(capacity);
    }

    public void tryPut(GwMessage msg) throws GwQueueException {
        if (!this.q.offer(msg)) {
            throw new GwQueueException("Error: Queue Full");
        }
    }

    public GwMessage tryGet() {
        return this.q.poll();
    }

    public void blockingPut(GwMessage msg) throws GwQueueException, InterruptedException {
        this.q.put(msg);
    }

    public GwMessage blockingGet() throws GwQueueException, InterruptedException {
        return this.q.take();
    }

    public long size() throws GwQueueException {
        return this.q.size();
    }

    public long capacityLeft() throws GwQueueException {
        return this.capacity - this.q.size();
    }

    public boolean empty() throws GwQueueException {
        return this.q.isEmpty();
    }
}
