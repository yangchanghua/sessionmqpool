package com.mqredis.api;

public interface GwMessageConsumer {

    String consume(GwMessage msg);

}
