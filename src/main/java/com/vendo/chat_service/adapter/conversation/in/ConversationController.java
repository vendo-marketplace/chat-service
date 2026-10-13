package com.vendo.chat_service.adapter.conversation.in;

import com.vendo.chat_service.adapter.conversation.in.dto.ConversationResponse;
import com.vendo.chat_service.adapter.conversation.in.dto.ConversationsResponse;
import com.vendo.chat_service.adapter.conversation.in.dto.CreateConversationRequest;
import com.vendo.chat_service.adapter.conversation.out.mapper.ConversationDtoMapper;
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
    private final ConversationDtoMapper mapper;

    @PostMapping
    public ConversationResponse create(@Valid @RequestBody CreateConversationRequest request) {
        ConversationCreatedCommand command = useCase.create(request.firstMessage(), request.productId());
        return mapper.toResponse(command);
    }

    @GetMapping
    public ConversationsResponse findAll() {
        // Отримати розмови поточного користувача.
        return new ConversationsResponse();
    }

    @GetMapping("/{conversationId}")
    public ConversationResponse findById(
            @PathVariable String conversationId
    ) {
        // Отримати розмову.
        return null;
    }

    @DeleteMapping("/{conversationId}")
    public void delete(
            @PathVariable String conversationId
    ) {
        // Видалити розмову та її повідомлення.
    }

}
