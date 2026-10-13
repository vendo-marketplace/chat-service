package com.vendo.chat_service.application.conversation;

import com.vendo.chat_service.application.conversation.command.ConversationCreatedCommand;
import com.vendo.chat_service.domain.product.Product;
import com.vendo.chat_service.port.conversation.ConversationUseCase;
import com.vendo.chat_service.port.product.ProductQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationService implements ConversationUseCase {

    private final ProductQueryPort productQueryPort;

    @Override
    public ConversationCreatedCommand create(String firstMessage, String productId) {
        // TODO find product by id
        Product product = productQueryPort.findById(productId);

        // TODO check if conversation exists
        // TODO if so then return it
        // TODO else if user doesn't texts to himself
        // TODO create conversation and return it
        return null;
    }

}
