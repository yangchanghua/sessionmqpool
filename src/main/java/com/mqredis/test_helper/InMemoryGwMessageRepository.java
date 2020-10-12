package com.mqredis.test_helper;

import com.mqredis.api.GwMessage;
import com.mqredis.api.GwMessageRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InMemoryGwMessageRepository implements GwMessageRepository {

    private final ConcurrentHashMap<Long, ConcurrentLinkedQueue<GwMessage>> db;

    private static final InMemoryGwMessageRepository instance = new InMemoryGwMessageRepository();

    private InMemoryGwMessageRepository() {
        this.db = new ConcurrentHashMap<Long, ConcurrentLinkedQueue<GwMessage>>();
    }

    public static InMemoryGwMessageRepository getInstance() {
        return InMemoryGwMessageRepository.instance;
    }

    public void save(GwMessage msg) {
        ConcurrentLinkedQueue<GwMessage> oldQ = this.db.get(msg.getSession());
        if (oldQ == null) {
            ConcurrentLinkedQueue<GwMessage> q = new ConcurrentLinkedQueue<GwMessage>(
                    Collections.singletonList(msg)
            );
            oldQ = this.db.putIfAbsent(msg.getSession(), q);
            if (oldQ != null) {
                oldQ.add(msg);
            }
        } else {
            oldQ.add(msg);
        }
        System.out.println("repo received message " + msg.getMsg());
    }

    public List<GwMessage> readBySession(long session) {
        return Arrays.asList((GwMessage[]) this.db.get(session).toArray());
    }

    public int countOfMessagesForSession(long session) {
        return this.db.get(session).size();
    }
}
