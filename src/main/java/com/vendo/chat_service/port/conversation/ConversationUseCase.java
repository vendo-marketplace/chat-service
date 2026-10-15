package com.vendo.chat_service.port.conversation;

import com.vendo.chat_service.application.conversation.command.ConversationCreatedCommand;
import com.vendo.chat_service.domain.conversation.Conversation;

import java.util.List;

public interface ConversationUseCase {

    ConversationCreatedCommand create(String firstMessage, String productId);

    List<Conversation> findAll();
    Conversation findById(String id);

    void delete(String id);

}
