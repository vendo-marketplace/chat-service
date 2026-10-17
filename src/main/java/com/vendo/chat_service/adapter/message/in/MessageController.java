package com.vendo.chat_service.adapter.message.in;

import com.vendo.chat_service.adapter.message.in.dto.SendMessageRequest;
import com.vendo.chat_service.domain.message.Message;
import com.vendo.chat_service.port.message.MessageUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversations/{conversationId}/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageUseCase useCase;

    @PostMapping
    public void send(
            @PathVariable String conversationId,
            @Valid @RequestBody SendMessageRequest request
    ) {
        useCase.send(conversationId, request.message());
    }

    @GetMapping
    public Page<Message> findAll(
            @PathVariable String conversationId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 30) Pageable pageable
    ) {
        return useCase.findAll(conversationId, pageable);
    }
}
