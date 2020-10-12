package com.mqredis;

import com.mqredis.api.*;
import com.mqredis.serealized.NaiveGwMQ;
import com.mqredis.serealized.SingleThreadGwMessageConsumerPool;
import com.mqredis.serealized.SingleThreadGwMessageProducerPool;
import com.mqredis.test_helper.InMemoryGwMessageRepository;
import com.mqredis.test_helper.ShortGwMessageConsumer;
import com.mqredis.test_helper.ShortGwMessageProducer;

public class NaiveGwMQTester {

    public void testOnePOneC() {
        GwMessageProducer producer = new ShortGwMessageProducer(0);
        GwMessageConsumer consumer = new ShortGwMessageConsumer();
        GwMessageQueue queue = new NaiveGwMQ(100);
        final GwMessageRepository repository = InMemoryGwMessageRepository.getInstance();
        final GwMessageProducerPool producerPool = new SingleThreadGwMessageProducerPool();
        final GwMessageConsumerPool consumerPool = new SingleThreadGwMessageConsumerPool();
        producerPool.start(producer, queue);
        consumerPool.run(consumer, queue);
    }

    public static void main(String[] args) {
        NaiveGwMQTester tester = new NaiveGwMQTester();
        tester.testOnePOneC();
    }
}
