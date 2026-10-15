package com.vendo.chat_service.port.message;

import com.vendo.chat_service.domain.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageUseCase {

    void send(String conversationId, String message);
    Page<Message> findAll(String conversationId, Pageable pageable);

}
