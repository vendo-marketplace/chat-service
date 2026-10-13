package com.vendo.chat_service.application.conversation.command;

public record ConversationCreatedCommand(String conversationId, boolean created) {

    public static ConversationCreatedCommand from(String conversationId, boolean created) {
        return new ConversationCreatedCommand(conversationId, created);
    }

}
