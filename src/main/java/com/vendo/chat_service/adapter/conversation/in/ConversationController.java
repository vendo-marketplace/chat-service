package com.vendo.chat_service.adapter.conversation.in;

import com.vendo.chat_service.adapter.conversation.in.dto.*;
import com.vendo.chat_service.application.conversation.command.ConversationCreatedCommand;
import com.vendo.chat_service.port.conversation.ConversationUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationUseCase useCase;

    @PostMapping
    public ConversationCreatedCommand create(@Valid @RequestBody CreateConversationRequest request) {
        return useCase.create(request.firstMessage(), request.productId());
    }

    @GetMapping
    public ConversationsResponse findAll() {
        return new ConversationsResponse(useCase.findAll());
    }

    @GetMapping("/{conversationId}")
    public ConversationResponse findById(
            @PathVariable String conversationId
    ) {
        return useCase.findById(conversationId);
    }

    @GetMapping("/unread")
    public UnreadResponse findUnread() {
        return new UnreadResponse(useCase.findUnread());
    }

    @PatchMapping("/{conversationId}/read")
    public void markAsRead(
            @PathVariable String conversationId,
            @Valid @RequestBody MarkAsReadRequest request
    ) {
        useCase.markAsRead(conversationId, request.lastReadMessageId());
    }

    @DeleteMapping("/{conversationId}")
    public void delete(
            @PathVariable String conversationId
    ) {
        useCase.delete(conversationId);
    }

}
