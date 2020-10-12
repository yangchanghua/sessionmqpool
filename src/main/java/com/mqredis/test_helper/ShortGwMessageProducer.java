package com.mqredis.test_helper;

import com.mqredis.api.GwMessage;
import com.mqredis.api.GwMessageQueue;
import com.mqredis.api.GwQueueException;

import java.util.concurrent.atomic.AtomicInteger;

public class ShortGwMessageProducer {
    private long session;
    private AtomicInteger msgNumber;
    private int cntToProduce;

    public ShortGwMessageProducer(long session) {
        this.session = session;
        this.msgNumber = new AtomicInteger(0);
    }

    public void produce(int count, GwMessageQueue queue) throws GwQueueException {
        for (int i = 0; i<count; i++) {
            queue.tryPut(this.getMessage());
        }
    }

    public ShortGwMessageProducer setSession(long session) {
        this.session = session;
        return this;
    }

    public GwMessage getMessage() {
        int n = this.msgNumber.getAndIncrement();
        return new GwMessage(this.session, "msg:" + n);
    }
}
