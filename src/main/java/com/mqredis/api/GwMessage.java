package com.mqredis.api;

public class GwMessage {
    private long id;
    private long session;
    private String msg;

    public GwMessage(long session, String msg) {
        this.session = session;
        this.msg = msg;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSession() {
        return session;
    }

    public void setSession(long session) {
        this.session = session;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
