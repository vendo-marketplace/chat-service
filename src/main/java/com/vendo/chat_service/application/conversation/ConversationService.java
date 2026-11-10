package com.vendo.chat_service.application.conversation;

import com.vendo.chat_service.adapter.conversation.in.dto.ConversationResponse;
import com.vendo.chat_service.adapter.user.in.dto.UserProfileResponse;
import com.vendo.chat_service.application.conversation.command.ConversationCreatedCommand;
import com.vendo.chat_service.domain.conversation.Conversation;
import com.vendo.chat_service.domain.message.Message;
import com.vendo.chat_service.domain.product.Product;
import com.vendo.chat_service.domain.product.exception.ProductOwnerException;
import com.vendo.chat_service.domain.user.User;
import com.vendo.chat_service.domain.user.exception.SameUserConversationException;
import com.vendo.chat_service.port.conversation.ConversationCommandPort;
import com.vendo.chat_service.port.conversation.ConversationQueryPort;
import com.vendo.chat_service.port.conversation.ConversationUseCase;
import com.vendo.chat_service.port.message.MessageCommandPort;
import com.vendo.chat_service.port.product.ProductQueryPort;
import com.vendo.chat_service.port.user.AuthUserPort;
import com.vendo.chat_service.port.user.UserQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversationService implements ConversationUseCase {

    private final AuthUserPort authUserPort;
    private final UserQueryPort userQueryPort;

    private final ProductQueryPort productQueryPort;

    private final ConversationQueryPort conversationQueryPort;
    private final ConversationCommandPort conversationCommandPort;

    private final MessageCommandPort messageCommandPort;

    @Override
    @Transactional
    public ConversationCreatedCommand create(String firstMessage, String productId) {
        var product = productQueryPort.findById(productId);
        var buyer = authUserPort.getAuthUser();

        validateNotConversationProductOwner(buyer, product);

        Optional<Conversation> optConv = conversationQueryPort.find(product.id(), product.ownerId(), buyer.id());
        return optConv.map(conversation -> validateExistConversation(buyer, conversation))
                .orElseGet(() -> ConversationCreatedCommand.from(saveConversation(productId, product.ownerId(), buyer.id(), firstMessage), true));
    }

    @Override
    public List<ConversationResponse> findAll() {
        User authUser = authUserPort.getAuthUser();
        List<Conversation> conversations = conversationQueryPort.findAll(authUser.id());
        Map<String, UserProfileResponse> profiles = new HashMap<>();

        return conversations.stream()
                .map(conversation -> toConversationResponse(conversation, profiles))
                .toList();
    }

    @Override
    public ConversationResponse findById(String conversationId) {
        var conversation = conversationQueryPort.findById(conversationId);
        authUserPort.validateAnyOwner(conversation.buyerId(), conversation.sellerId());
        return toConversationResponse(conversation, new HashMap<>());
    }

    @Override
    @Transactional
    public void delete(String conversationId) {
        validateConversationOwner(conversationId);
        messageCommandPort.deleteAll(conversationId);
        conversationCommandPort.delete(conversationId);
    }

    private ConversationResponse toConversationResponse(Conversation conversation, Map<String, UserProfileResponse> profiles) {
        return ConversationResponse.builder()
                .id(conversation.id())
                .productId(conversation.productId())
                .seller(getUserProfile(conversation.sellerId(), profiles))
                .buyer(getUserProfile(conversation.buyerId(), profiles))
                .lastMessage(conversation.lastMessage())
                .createdAt(conversation.createdAt())
                .build();
    }

    private UserProfileResponse getUserProfile(String userId, Map<String, UserProfileResponse> profiles) {
        return profiles.computeIfAbsent(userId, id -> toUserProfile(userQueryPort.getById(id)));
    }

    private UserProfileResponse toUserProfile(User user) {
        return UserProfileResponse.builder()
                .id(user.id())
                .fullName(user.fullName())
                .build();
    }

    private void validateConversationOwner(String conversationId) {
        var conversation = conversationQueryPort.findById(conversationId);
        authUserPort.validateAnyOwner(conversation.buyerId(), conversation.sellerId());
    }

    private void validateNotConversationProductOwner(User owner, Product product) {
        if (owner.id().equals(product.ownerId())) {
            throw new ProductOwnerException("Product owner isn't able to create conversation to itself.");
        }
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
        String conversationId = conversationCommandPort.save(conversation);
        messageCommandPort.save(Message.from(conversationId, buyerId, firstMessage));
        return conversationId;
    }

}
