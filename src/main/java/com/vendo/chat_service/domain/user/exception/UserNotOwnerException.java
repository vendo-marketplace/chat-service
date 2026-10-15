package com.vendo.chat_service.domain.user.exception;

public class UserNotOwnerException extends RuntimeException {
    public UserNotOwnerException(String message) {
        super(message);
    }
}
