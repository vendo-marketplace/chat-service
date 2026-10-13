package com.vendo.chat_service.application.conversation.command;

public record ConversationCreatedCommand(String conversationId, boolean created) {
}
