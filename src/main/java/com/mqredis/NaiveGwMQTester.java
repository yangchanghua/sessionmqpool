package com.mqredis;

import com.mqredis.api.*;
import com.mqredis.impl.GwMessageSingleQueue;
import com.mqredis.impl.SessionAwareGwMessageConsumerPool;
import com.mqredis.impl.SessionLessGwMessageConsumerPool;
import com.mqredis.test_helper.*;

public class NaiveGwMQTester {

    public void testOnePOneC() {
        GwMessageConsumer consumer = new ShortGwMessageConsumer();
        GwMessageQueue queue = new GwMessageSingleQueue(100000);
        ShortGwMessageProducer producer = new ShortGwMessageProducer(queue);
        final GwMessageRepository repository = InMemoryGwMessageRepository.getInstance();
//        final GwMessageConsumerPool consumerPool = new SessionLessGwMessageConsumerPool();
        final GwMessageConsumerPool consumerPool = new SessionAwareGwMessageConsumerPool(8, 20);
        consumerPool.start(consumer, queue);

        int countOfSessions = 100;
        int countPerSession = 1000;
        try {
            producer.produce(countOfSessions, countPerSession);
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
        for (int i = 0; i<countOfSessions; i++) {
            int cnt = repository.countOfMessagesForSession(i);
            assert (cnt == countPerSession);
        }
//        System.out.println("msg in repo: " + repository.countOfMessagesForSession(0));
    }

    public static void main(String[] args) {
        NaiveGwMQTester tester = new NaiveGwMQTester();
        tester.testOnePOneC();
    }
}
