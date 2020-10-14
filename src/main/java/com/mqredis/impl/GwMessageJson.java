package com.mqredis.impl;

import com.mqredis.api.GwMessage;
import com.alibaba.fastjson.JSON;

public class GwMessageJson {

    public static String toJson(final GwMessage msg) {
        return JSON.toJSONString(msg);
    }

    public static GwMessage fromJson(final String str) {
        return JSON.parseObject(str, GwMessage.class);
    }

}
