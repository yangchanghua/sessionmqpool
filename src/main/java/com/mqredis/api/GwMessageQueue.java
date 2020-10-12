package com.mqredis.api;

public interface GwMessageQueue {

    void offer(GwMessage msg) throws GwQueueException;

    GwMessage poll() throws GwQueueException;

    long size() throws GwQueueException;

    long capacityLeft() throws GwQueueException;

    boolean empty() throws GwQueueException;

}
