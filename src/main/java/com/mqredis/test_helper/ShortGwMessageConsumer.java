package com.mqredis.test_helper;

import com.mqredis.api.GwMessage;
import com.mqredis.api.GwMessageConsumer;
import com.mqredis.api.GwMessageRepository;

public class ShortGwMessageConsumer implements GwMessageConsumer {

    private final GwMessageRepository repository = InMemoryGwMessageRepository.getInstance();

    public String consume(GwMessage msg) {
        String data = msg.getMsg();
        repository.save(msg);
        return data;
    }
}
