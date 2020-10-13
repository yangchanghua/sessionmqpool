package com.mqredis.test_helper;

import com.mqredis.api.GwMessage;
import com.mqredis.api.GwQueueException;

public interface GwMessageProducer {

    void produce(int sessionCount, int countPerSession) throws GwQueueException;

}
