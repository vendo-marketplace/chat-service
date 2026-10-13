package com.vendo.chat_service.domain.user.exception;

public class SameUserConversationException extends RuntimeException {
    public SameUserConversationException(String message) {
        super(message);
    }
}
