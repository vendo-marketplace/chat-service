package com.vendo.chat_service.domain.conversation.exception;

public class ConversationNotAllowedException extends RuntimeException {
    public ConversationNotAllowedException(String message) {
        super(message);
    }
}
