package com.vendo.chat_service.adapter.message.in.interceptor;

import com.vendo.chat_service.domain.conversation.Conversation;
import com.vendo.chat_service.domain.conversation.exception.ConversationNotAllowedException;
import com.vendo.chat_service.domain.user.User;
import com.vendo.chat_service.port.conversation.ConversationQueryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MessageChannelInterceptorTest {

    private static final String CONVERSATION_PATH = "/topic/conversations/";

    private final ConversationQueryPort conversationQueryPort = mock(ConversationQueryPort.class);
    private final MessageChannel channel = mock(MessageChannel.class);
    private final MessageChannelInterceptor interceptor = new MessageChannelInterceptor(conversationQueryPort);

    private final User buyer = User.builder().id("buyer-id").build();
    private final User stranger = User.builder().id("stranger-id").build();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(interceptor, "conversationPath", CONVERSATION_PATH);
        when(conversationQueryPort.findById("c1")).thenReturn(Conversation.builder()
                .id("c1")
                .sellerId("seller-id")
                .buyerId("buyer-id")
                .build());
    }

    @Test
    void allowsParticipantToSubscribeToMessages() {
        Message<?> message = subscribe(CONVERSATION_PATH + "c1", buyer);

        assertThat(interceptor.preSend(message, channel)).isSameAs(message);
        verify(conversationQueryPort).findById("c1");
    }

    @Test
    void allowsParticipantToSubscribeToReadEvents() {
        Message<?> message = subscribe(CONVERSATION_PATH + "c1/read", buyer);

        assertThat(interceptor.preSend(message, channel)).isSameAs(message);
        verify(conversationQueryPort).findById("c1");
    }

    @Test
    void deniesStrangerSubscriptionToReadEvents() {
        Message<?> message = subscribe(CONVERSATION_PATH + "c1/read", stranger);

        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(ConversationNotAllowedException.class);
    }

    @Test
    void deniesUnknownNestedDestination() {
        Message<?> message = subscribe(CONVERSATION_PATH + "c1/other", buyer);

        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(ConversationNotAllowedException.class);
        verifyNoInteractions(conversationQueryPort);
    }

    private static Message<?> subscribe(String destination, User user) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setDestination(destination);
        accessor.setUser(new UsernamePasswordAuthenticationToken(user, null, List.of()));
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

}
