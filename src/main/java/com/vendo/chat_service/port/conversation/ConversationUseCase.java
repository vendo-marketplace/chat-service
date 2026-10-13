package com.vendo.chat_service.port.conversation;

import com.vendo.chat_service.application.conversation.command.ConversationCreatedCommand;

public interface ConversationUseCase {

    ConversationCreatedCommand create(String firstMessage, String productId);

}
