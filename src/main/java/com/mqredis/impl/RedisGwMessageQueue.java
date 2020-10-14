package com.mqredis.impl;

import com.mqredis.api.GwMessage;
import com.mqredis.api.GwMessageQueue;
import com.mqredis.api.GwQueueException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

public class RedisGwMessageQueue implements GwMessageQueue {

    private static final String queueKey = "gwmessages";

    private final JedisPool jedisPool;
    private final int maxSize;

    public RedisGwMessageQueue(int maxSize) {
        this.maxSize = maxSize;
        jedisPool = new JedisPool(new JedisPoolConfig(), "localhost");
    }

    public void tryPut(GwMessage msg) throws GwQueueException {
        String v = GwMessageJson.toJson(msg);
//        System.out.println("Now putting into redis...");
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.rpush(queueKey, v);
        } finally {
            if (jedis != null) jedis.close();
        }
//        System.out.println("After put, len: " + jedis.llen(queueKey));
    }

    public GwMessage tryGet() throws GwQueueException {
        Jedis jedis = jedisPool.getResource();
        try {
            String v = jedis.lpop(queueKey);
            return GwMessageJson.fromJson(v);
        } finally {
            if (jedis != null) jedis.close();
        }
    }

    public void blockingPut(GwMessage msg) throws GwQueueException, InterruptedException {
        tryPut(msg);
    }

    public GwMessage blockingGet() throws GwQueueException, InterruptedException {
        Jedis jedis = jedisPool.getResource();
        try {
            List<String> v = jedis.blpop(3, queueKey);
            if (v.size() != 2) {
                throw new GwQueueException("No value got from redis for " + queueKey);
            }
            return GwMessageJson.fromJson(v.get(1));
        } finally {
            if (jedis != null) jedis.close();
        }
    }

    public long size() throws GwQueueException {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.llen(queueKey);
        } finally {
            if (jedis != null) jedis.close();
        }
    }

    public void clear() throws GwQueueException {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.del(queueKey);
        } finally {
            if (jedis != null) jedis.close();
        }
    }

    public long capacityLeft() throws GwQueueException {
        return 0;
    }

    public boolean empty() throws GwQueueException {
        return false;
    }
}
