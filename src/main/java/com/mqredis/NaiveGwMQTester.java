package com.mqredis;

import com.mqredis.api.*;
import com.mqredis.impl.GwMessageSingleQueue;
import com.mqredis.impl.SessionAwareGwMessageConsumerPool;
import com.mqredis.test_helper.*;

public class NaiveGwMQTester {

    public void testOnePOneC() {
        GwMessageConsumer consumer = new SleepingGwMessageConsumer();
        GwMessageQueue queue = new GwMessageSingleQueue(100000);
        final GwMessageRepository repository = InMemoryGwMessageRepository.getInstance();
//        final GwMessageConsumerPool consumerPool = new SessionLessGwMessageConsumerPool();
        final GwMessageConsumerPool consumerPool =
                new SessionAwareGwMessageConsumerPool(3, 3);
        consumerPool.start(consumer, queue);

        int countOfSessions = 4;
        int countPerSession = 4;
        GwMessageWithTimeProducer producer = new GwMessageWithTimeProducer(queue, 0);
        GwMessageWithTimeProducer longTaskProducer = new GwMessageWithTimeProducer(queue, 1000);

        try {
            longTaskProducer.produce(2, 1);
            producer.produce(countOfSessions, countPerSession);
        } catch (GwQueueException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(4000);
            consumerPool.shutdown();
            consumerPool.waitTerminatedForMillis(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i<countOfSessions; i++) {
            int cnt = repository.countOfMessagesForSession(i);
            System.out.println("msg in for session  " + i + ": " + cnt);
        }
    }

    public static void main(String[] args) {
        NaiveGwMQTester tester = new NaiveGwMQTester();
        tester.testOnePOneC();
    }
}
