package com.vendo.chat_service.domain.message;

import com.vendo.core_lib.utils.StringUtils;

import java.time.Instant;

public record Message(

        String id,

        String conversationId,

        String senderId,
        String content,

        Instant createdAt

) {

    public static Message from(String conversationId, String senderId, String content) {
        return new Message(null, conversationId, senderId, content, null);
    }

    public LastMessage toLast() {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalStateException("Message id cannot be empty.");
        }

        return LastMessage.from(id, senderId, content);
    }

}
