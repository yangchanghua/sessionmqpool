package com.mqredis.api;

public interface GwMessageConsumerPool {

    void start(GwMessageConsumer consumer, GwMessageQueue queue);

    void shutdown();

    boolean waitTerminatedForMillis(long timeout) throws InterruptedException;

}
