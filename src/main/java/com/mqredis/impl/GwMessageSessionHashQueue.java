package com.mqredis.impl;

import com.mqredis.api.GwMessage;
import com.mqredis.api.GwMessageQueue;
import com.mqredis.api.GwQueueException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GwMessageSessionHashQueue implements GwMessageQueue {

    class SessionQueue {
        private final LinkedBlockingQueue<GwMessage> q;
        private long msgId = 0;

        SessionQueue() {
            q = new LinkedBlockingQueue<GwMessage>();
        }

        GwMessage get() throws InterruptedException {
            synchronized (this.q) {
                GwMessage msg = this.q.take();
                this.msgId = msg.getId();
                return msg;
            }
        }

        void put(GwMessage msg) throws InterruptedException {
            this.q.put(msg);
        }

        long getHeadId() {
            return this.msgId;
        }
    }

    ConcurrentHashMap<Long, SessionQueue> sessionQueueMap; //for put only

    public GwMessage tryGet(long session) throws GwQueueException {
        return null;
    }

    public void tryPut(GwMessage msg) throws GwQueueException {

    }

    public GwMessage tryGet() throws GwQueueException {
        return null;
    }

    public void blockingPut(GwMessage msg) throws GwQueueException, InterruptedException {

    }

    public GwMessage blockingGet() throws GwQueueException, InterruptedException {
        return null;
    }

    public long size() throws GwQueueException {
        return 0;
    }

    public long capacityLeft() throws GwQueueException {
        return 0;
    }

    public boolean empty() throws GwQueueException {
        return false;
    }
}
