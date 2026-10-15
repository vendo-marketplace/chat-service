package com.vendo.chat_service.port.message;

import com.vendo.chat_service.domain.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageQueryPort {

    Page<Message> findAll(String conversationId, Pageable pageable);

}
