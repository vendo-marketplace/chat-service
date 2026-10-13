package com.vendo.chat_service.application.conversation;

import com.vendo.chat_service.application.conversation.command.ConversationCreatedCommand;
import com.vendo.chat_service.domain.conversation.Conversation;
import com.vendo.chat_service.domain.user.User;
import com.vendo.chat_service.domain.user.exception.SameUserConversationException;
import com.vendo.chat_service.port.conversation.ConversationCommandPort;
import com.vendo.chat_service.port.conversation.ConversationQueryPort;
import com.vendo.chat_service.port.conversation.ConversationUseCase;
import com.vendo.chat_service.port.product.ProductQueryPort;
import com.vendo.chat_service.port.user.AuthUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversationService implements ConversationUseCase {

    private final AuthUserPort authUserPort;

    private final ProductQueryPort productQueryPort;

    private final ConversationQueryPort conversationQueryPort;
    private final ConversationCommandPort conversationCommandPort;

    @Override
    public ConversationCreatedCommand create(String firstMessage, String productId) {
        var product = productQueryPort.findById(productId);
        var buyer = authUserPort.getAuthUser();

        Optional<Conversation> optConv = conversationQueryPort.find(product.id(), product.ownerId(), buyer.id());
        return optConv.map(conversation -> validateExistConversation(buyer, conversation))
                .orElseGet(() -> ConversationCreatedCommand.from(saveConversation(productId, product.ownerId(), buyer.id(), firstMessage), true));
    }

    private void validateNotSameUser(User buyer, Conversation conversation) {
        if (buyer.id().equals(conversation.sellerId())) {
            throw new SameUserConversationException("You're not able to send message to yourself.");
        }
    }

    private ConversationCreatedCommand validateExistConversation(User buyer, Conversation conversation) {
        validateNotSameUser(buyer, conversation);
        return ConversationCreatedCommand.from(conversation.id(), false);
    }

    private String saveConversation(String productId, String sellerId, String buyerId, String firstMessage) {
        Conversation conversation = Conversation.from(productId, sellerId, buyerId, firstMessage);
        return conversationCommandPort.save(conversation);
    }

}
