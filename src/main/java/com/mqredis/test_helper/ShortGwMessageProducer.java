package com.mqredis.test_helper;

import com.mqredis.api.GwMessage;
import com.mqredis.api.GwMessageProducer;

import java.util.concurrent.atomic.AtomicInteger;

public class ShortGwMessageProducer implements GwMessageProducer {
    private long session;
    private AtomicInteger msgNumber;
    private int cntToProduce;

    public ShortGwMessageProducer(long session) {
        this.session = session;
        this.msgNumber = new AtomicInteger(0);
        this.cntToProduce = 100;
    }

    public ShortGwMessageProducer setCntToProduce(int count) {
        this.cntToProduce = count;
        return this;
    }

    public ShortGwMessageProducer setSession(long session) {
        this.session = session;
        return this;
    }

    public GwMessage produce() {
        int n = this.msgNumber.getAndIncrement();
        if (n > this.cntToProduce) {
            System.out.println("Done producing " + n + " messages");
            return null;
        }
        return new GwMessage(this.session, "msg:" + n);
    }
}
