package com.vendo.chat_service.port.conversation;

import com.vendo.chat_service.adapter.conversation.in.dto.ConversationResponse;
import com.vendo.chat_service.application.conversation.command.ConversationCreatedCommand;

import java.util.List;

public interface ConversationUseCase {

    ConversationCreatedCommand create(String firstMessage, String productId);

    List<ConversationResponse> findAll();
    ConversationResponse findById(String id);

    void markAsRead(String conversationId, String lastReadMessageId);

    void delete(String id);

}
