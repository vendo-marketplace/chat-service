package com.vendo.chat_service.adapter.message.in.interceptor;

import com.vendo.chat_service.domain.conversation.Conversation;
import com.vendo.chat_service.domain.conversation.exception.ConversationNotAllowedException;
import com.vendo.chat_service.domain.user.User;
import com.vendo.chat_service.adapter.message.out.websocket.MessageWebSocketEventListener;
import com.vendo.chat_service.port.conversation.ConversationQueryPort;
import com.vendo.core_lib.constants.Delimiters;
import com.vendo.core_lib.constants.Separators;
import com.vendo.core_lib.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageChannelInterceptor implements ChannelInterceptor {

    @Value("${websocket.conversation.path}")
    private String conversationPath;

    private final ConversationQueryPort conversationQueryPort;

    @Override
    public @Nullable Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (ObjectUtils.isNotNull(command) && command.getMessageType() == SimpMessageType.SUBSCRIBE) {
            String destination = validateDestination(accessor);
            String conversationId = parseConversationId(destination);

            Conversation conversation = conversationQueryPort.findById(conversationId);

            User user = parseAuthUser(accessor);
            if (!isParticipant(user, conversation)) {
                throw new ConversationNotAllowedException("Subscription denied");
            }
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }

    private String validateDestination(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null || !destination.startsWith(conversationPath)) {
            throw new ConversationNotAllowedException("Subscription denied.");
        }

        return destination;
    }

    private String parseConversationId(String destination) {
        String conversationId = destination.substring(conversationPath.length());
        if (conversationId.endsWith(MessageWebSocketEventListener.READ_DESTINATION_SUFFIX)) {
            conversationId = conversationId.substring(0, conversationId.length() - MessageWebSocketEventListener.READ_DESTINATION_SUFFIX.length());
        }

        if (conversationId.isBlank() || conversationId.contains(Separators.SLASH_SEPARATOR)) {
            throw new ConversationNotAllowedException("Subscription denied.");
        }

        return conversationId;
    }

    private User parseAuthUser(StompHeaderAccessor accessor) {
        Authentication authentication = (Authentication) accessor.getUser();
        if (ObjectUtils.isNull(authentication)) {
            throw new ConversationNotAllowedException("Subscription denied.");
        }

        User user = (User) authentication.getPrincipal();
        if (ObjectUtils.isNull(user)) {
            throw new ConversationNotAllowedException("Subscription denied.");
        }

        return user;
    }

    private boolean isParticipant(User user, Conversation conversation) {
        return user.id().equals(conversation.buyerId())
                || user.id().equals(conversation.sellerId());
    }

}
