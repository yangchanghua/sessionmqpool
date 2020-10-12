package com.mqredis;

import com.mqredis.api.*;
import com.mqredis.serealized.NaiveGwMQ;
import com.mqredis.serealized.SingleThreadGwMessageConsumerPool;
import com.mqredis.test_helper.*;

public class NaiveGwMQTester {

    public void testOnePOneC() {
        ShortGwMessageProducer producer = new ShortGwMessageProducer(0);
        GwMessageConsumer consumer = new ShortGwMessageConsumer();
        GwMessageQueue queue = new NaiveGwMQ(100);
        final GwMessageRepository repository = InMemoryGwMessageRepository.getInstance();
        final GwMessageConsumerPool consumerPool = new SingleThreadGwMessageConsumerPool();
        consumerPool.start(consumer, queue);
        try {
            producer.produce(100, queue);
        } catch (GwQueueException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(1000);
            consumerPool.shutdown();
            consumerPool.waitTerminatedForMillis(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("msg in repo: " + repository.countOfMessagesForSession(0));
    }

    public static void main(String[] args) {
        NaiveGwMQTester tester = new NaiveGwMQTester();
        tester.testOnePOneC();
    }
}
