package com.mqredis.api;

public interface GwMessageConsumerPool {

    void run(GwMessageConsumer consumer, GwMessageQueue queue);

    void shutdown();

}
