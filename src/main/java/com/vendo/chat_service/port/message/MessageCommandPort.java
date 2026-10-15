package com.vendo.chat_service.port.message;

import com.vendo.chat_service.domain.message.Message;

public interface MessageCommandPort {

    void save(Message message);

    void deleteAll(String conversationId);

}
