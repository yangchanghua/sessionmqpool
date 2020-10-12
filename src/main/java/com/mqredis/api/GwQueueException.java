package com.mqredis.api;

public class GwQueueException extends Exception {
    public GwQueueException(String message) {
        super(message);
    }

    public GwQueueException(String message, Throwable cause) {
        super(message, cause);
    }
}
