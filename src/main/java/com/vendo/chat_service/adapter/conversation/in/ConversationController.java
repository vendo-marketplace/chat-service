package com.vendo.chat_service.adapter.conversation.in;

import com.vendo.chat_service.adapter.conversation.in.dto.ConversationsResponse;
import com.vendo.chat_service.adapter.conversation.in.dto.CreateConversationRequest;
import com.vendo.chat_service.application.conversation.command.ConversationCreatedCommand;
import com.vendo.chat_service.domain.conversation.Conversation;
import com.vendo.chat_service.port.conversation.ConversationUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        List<Conversation> conversations = useCase.findAll();
        return new ConversationsResponse(conversations);
    }

    @GetMapping("/{conversationId}")
    public Conversation findById(
            @PathVariable String conversationId
    ) {
        return useCase.findById(conversationId);
    }

    @DeleteMapping("/{conversationId}")
    public void delete(
            @PathVariable String conversationId
    ) {
        useCase.delete(conversationId);
    }

}
