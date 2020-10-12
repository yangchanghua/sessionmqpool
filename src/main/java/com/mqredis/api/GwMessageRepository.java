package com.mqredis.api;

import java.util.List;

public interface GwMessageRepository {

    void save(GwMessage msg);

    List<GwMessage> readBySession(long session);

    int countOfMessagesForSession(long session);

}
