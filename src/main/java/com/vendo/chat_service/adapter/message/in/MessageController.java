package com.vendo.chat_service.adapter.message.in;

import com.vendo.chat_service.adapter.message.in.dto.MessageResponse;
import com.vendo.chat_service.adapter.message.in.dto.SendMessageRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversations/{conversationId}/messages")
public class MessageController {

    @PostMapping
    public MessageResponse send(
            @PathVariable String conversationId,
            @Valid @RequestBody SendMessageRequest request
    ) {
        // Надіслати повідомлення в розмову.
        return null;
    }

    @GetMapping
    public Page<MessageResponse> findAll(
            @PathVariable String conversationId,
            Pageable pageable
    ) {
        // Отримати історію повідомлень.
        return Page.empty();
    }
}
