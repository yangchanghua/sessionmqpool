package com.mqredis.api;

public interface GwMessageQueue {

    void tryPut(GwMessage msg) throws GwQueueException;

    GwMessage tryGet() throws GwQueueException;

    void blockingPut(GwMessage msg) throws GwQueueException, InterruptedException;

    GwMessage blockingGet() throws GwQueueException, InterruptedException;

    long size() throws GwQueueException;

    long capacityLeft() throws GwQueueException;

    boolean empty() throws GwQueueException;

}
