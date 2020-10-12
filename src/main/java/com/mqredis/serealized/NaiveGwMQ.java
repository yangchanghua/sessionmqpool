package com.mqredis.serealized;

import com.mqredis.api.GwMessage;
import com.mqredis.api.GwMessageQueue;
import com.mqredis.api.GwQueueException;

import java.util.concurrent.LinkedBlockingQueue;

public class NaiveGwMQ implements GwMessageQueue {

    private int capacity;

    private LinkedBlockingQueue<GwMessage> myQueue;

    public NaiveGwMQ(int capacity) {
        this.capacity = capacity;
        this.myQueue = new LinkedBlockingQueue<GwMessage>(capacity);
    }

    public void offer(GwMessage msg) throws GwQueueException {
        if (!this.myQueue.offer(msg)) {
            throw new GwQueueException("Error: Queue Full");
        }
    }

    public GwMessage poll() {
        return this.myQueue.poll();
    }

    public long size() throws GwQueueException {
        return this.myQueue.size();
    }

    public long capacityLeft() throws GwQueueException {
        return this.capacity - this.myQueue.size();
    }

    public boolean empty() throws GwQueueException {
        return this.myQueue.isEmpty();
    }
}
