package com.mqredis.api;

public interface GwMessageProducerPool {

    void start(GwMessageProducer producer, GwMessageQueue queue);

    void shutdown();

}
