package com.mqredis.test_helper;

import com.mqredis.api.GwMessageQueue;

public interface GwMessageProducerPool {

    void start(GwMessageProducer producer, GwMessageQueue queue);

    void shutdown();

}
