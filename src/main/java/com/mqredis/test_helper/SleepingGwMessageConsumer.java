package com.mqredis.test_helper;

import com.mqredis.api.GwMessage;
import com.mqredis.api.GwMessageConsumer;
import com.mqredis.api.GwMessageRepository;

public class SleepingGwMessageConsumer implements GwMessageConsumer {

    private final GwMessageRepository repository = InMemoryGwMessageRepository.getInstance();

    public String consume(GwMessage msg) {
        String data = msg.getMsg();
        String sleepKey = "{long:";
        int i = data.indexOf(sleepKey);
        if (i == -1) {
            repository.save(msg);
        } else {
            i += sleepKey.length();
            int e = data.indexOf("}");
            if (e < 0) {
                System.out.println("ERROR: no closing flag for long task");
            } else {
                String s = data.substring(i, e);
                int n = Integer.parseInt(s);
                try {
                    Thread.sleep(n);
                } catch (InterruptedException interruptedException) {
                    System.out.println("interrupted when sleeping in task");
                }
                repository.save(msg);
            }
        }
        return data;
    }
}
