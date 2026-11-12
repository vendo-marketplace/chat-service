package com.vendo.chat_service.application.conversation;

import com.vendo.chat_service.adapter.conversation.in.dto.ConversationResponse;
import com.vendo.chat_service.adapter.user.in.dto.UserProfileResponse;
import com.vendo.chat_service.domain.conversation.Conversation;
import com.vendo.chat_service.domain.message.LastMessage;
import com.vendo.chat_service.domain.conversation.exception.ConversationNotFoundException;
import com.vendo.chat_service.domain.user.User;
import com.vendo.chat_service.domain.user.exception.UserNotOwnerException;
import com.vendo.chat_service.port.conversation.ConversationCommandPort;
import com.vendo.chat_service.port.conversation.ConversationQueryPort;
import com.vendo.chat_service.port.message.MessageCommandPort;
import com.vendo.chat_service.port.product.ProductQueryPort;
import com.vendo.chat_service.port.user.AuthUserPort;
import com.vendo.chat_service.port.user.UserQueryPort;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    private static final User BUYER = user("buyer-id", "Buyer Name");
    private static final User SELLER = user("seller-id", "Seller Name");
    private static final User OTHER_SELLER = user("other-seller-id", "Other Seller");
    private static final User OTHER_BUYER = user("other-buyer-id", "Other Buyer");

    @Mock
    private AuthUserPort authUserPort;
    @Mock
    private UserQueryPort userQueryPort;
    @Mock
    private ProductQueryPort productQueryPort;
    @Mock
    private ConversationQueryPort conversationQueryPort;
    @Mock
    private ConversationCommandPort conversationCommandPort;
    @Mock
    private MessageCommandPort messageCommandPort;

    @InjectMocks
    private ConversationService service;

    @Nested
    class FindAll {

        @Test
        void mapsSellerAndBuyerToProfiles() {
            Conversation conversation = conversation("c1", SELLER.id(), BUYER.id());
            when(authUserPort.getAuthUser()).thenReturn(BUYER);
            when(conversationQueryPort.findAll(BUYER.id())).thenReturn(List.of(conversation));
            when(userQueryPort.getById(SELLER.id())).thenReturn(SELLER);
            when(userQueryPort.getById(BUYER.id())).thenReturn(BUYER);

            List<ConversationResponse> result = service.findAll();

            assertThat(result).singleElement().satisfies(response -> {
                assertThat(response.id()).isEqualTo("c1");
                assertThat(response.productId()).isEqualTo(conversation.productId());
                assertThat(response.lastMessage()).isEqualTo(conversation.lastMessage());
                assertThat(response.createdAt()).isEqualTo(conversation.createdAt());
                assertThat(response.seller()).isEqualTo(profile(SELLER));
                assertThat(response.buyer()).isEqualTo(profile(BUYER));
            });
        }

        @Test
        void fetchesEachUserOnlyOnce() {
            when(authUserPort.getAuthUser()).thenReturn(BUYER);
            when(conversationQueryPort.findAll(BUYER.id())).thenReturn(List.of(
                    conversation("c1", SELLER.id(), BUYER.id()),
                    conversation("c2", SELLER.id(), BUYER.id()),
                    conversation("c3", OTHER_SELLER.id(), BUYER.id())
            ));
            when(userQueryPort.getById(SELLER.id())).thenReturn(SELLER);
            when(userQueryPort.getById(BUYER.id())).thenReturn(BUYER);
            when(userQueryPort.getById(OTHER_SELLER.id())).thenReturn(OTHER_SELLER);

            List<ConversationResponse> result = service.findAll();

            assertThat(result).extracting(ConversationResponse::seller)
                    .containsExactly(profile(SELLER), profile(SELLER), profile(OTHER_SELLER));
            verify(userQueryPort, times(1)).getById(SELLER.id());
            verify(userQueryPort, times(1)).getById(BUYER.id());
            verify(userQueryPort, times(1)).getById(OTHER_SELLER.id());
        }

        @Test
        void fetchesBuyerWhenAuthUserIsSeller() {
            when(authUserPort.getAuthUser()).thenReturn(SELLER);
            when(conversationQueryPort.findAll(SELLER.id())).thenReturn(List.of(
                    conversation("c1", SELLER.id(), BUYER.id()),
                    conversation("c2", SELLER.id(), OTHER_BUYER.id())
            ));
            when(userQueryPort.getById(SELLER.id())).thenReturn(SELLER);
            when(userQueryPort.getById(BUYER.id())).thenReturn(BUYER);
            when(userQueryPort.getById(OTHER_BUYER.id())).thenReturn(OTHER_BUYER);

            List<ConversationResponse> result = service.findAll();

            assertThat(result).extracting(ConversationResponse::seller)
                    .containsOnly(profile(SELLER));
            assertThat(result).extracting(ConversationResponse::buyer)
                    .containsExactly(profile(BUYER), profile(OTHER_BUYER));
            verify(userQueryPort, times(1)).getById(SELLER.id());
        }

        @Test
        void returnsEmptyListWithoutRemoteCalls() {
            when(authUserPort.getAuthUser()).thenReturn(BUYER);
            when(conversationQueryPort.findAll(BUYER.id())).thenReturn(List.of());

            assertThat(service.findAll()).isEmpty();
            verifyNoInteractions(userQueryPort);
        }

    }

    @Nested
    class FindById {

        @Test
        void mapsSellerAndBuyerToProfiles() {
            Conversation conversation = conversation("c1", SELLER.id(), BUYER.id());
            when(conversationQueryPort.findById("c1")).thenReturn(conversation);
            when(authUserPort.validateAnyOwner(BUYER.id(), SELLER.id())).thenReturn(BUYER);
            when(userQueryPort.getById(SELLER.id())).thenReturn(SELLER);
            when(userQueryPort.getById(BUYER.id())).thenReturn(BUYER);

            ConversationResponse response = service.findById("c1");

            assertThat(response.id()).isEqualTo("c1");
            assertThat(response.productId()).isEqualTo(conversation.productId());
            assertThat(response.lastMessage()).isEqualTo(conversation.lastMessage());
            assertThat(response.createdAt()).isEqualTo(conversation.createdAt());
            assertThat(response.seller()).isEqualTo(profile(SELLER));
            assertThat(response.buyer()).isEqualTo(profile(BUYER));
        }

        @Test
        void throwsWhenAuthUserIsNotParticipant() {
            when(conversationQueryPort.findById("c1")).thenReturn(conversation("c1", SELLER.id(), BUYER.id()));
            when(authUserPort.validateAnyOwner(BUYER.id(), SELLER.id()))
                    .thenThrow(new UserNotOwnerException("You're not owner."));

            assertThatThrownBy(() -> service.findById("c1")).isInstanceOf(UserNotOwnerException.class);
            verifyNoInteractions(userQueryPort);
        }

        @Test
        void throwsWhenConversationNotFound() {
            when(conversationQueryPort.findById("missing"))
                    .thenThrow(new ConversationNotFoundException("Conversation not found."));

            assertThatThrownBy(() -> service.findById("missing")).isInstanceOf(ConversationNotFoundException.class);
            verify(authUserPort, never()).validateAnyOwner(any());
            verifyNoInteractions(userQueryPort);
        }

    }

    @Nested
    class FindUnread {

        @Test
        void returnsUnreadCountForAuthUser() {
            when(authUserPort.getAuthUser()).thenReturn(BUYER);
            when(conversationQueryPort.countUnreadMessages(BUYER.id())).thenReturn(3);

            int result = service.findUnread();

            assertThat(result).isEqualTo(3);
            verify(conversationQueryPort).countUnreadMessages(BUYER.id());
        }

        @Test
        void returnsZeroWhenNothingIsUnread() {
            when(authUserPort.getAuthUser()).thenReturn(SELLER);
            when(conversationQueryPort.countUnreadMessages(SELLER.id())).thenReturn(0);

            assertThat(service.findUnread()).isZero();
        }

        @Test
        void doesNotTouchUsersOrWrites() {
            when(authUserPort.getAuthUser()).thenReturn(BUYER);
            when(conversationQueryPort.countUnreadMessages(BUYER.id())).thenReturn(1);

            service.findUnread();

            verifyNoInteractions(userQueryPort, conversationCommandPort, messageCommandPort, productQueryPort);
        }

    }

    private static User user(String id, String fullName) {
        return User.builder().id(id).fullName(fullName).build();
    }

    private static UserProfileResponse profile(User user) {
        return new UserProfileResponse(user.id(), user.fullName());
    }

    private static Conversation conversation(String id, String sellerId, String buyerId) {
        return Conversation.builder()
                .id(id)
                .productId("product-" + id)
                .sellerId(sellerId)
                .buyerId(buyerId)
                .lastMessage(new LastMessage("m-" + id, sellerId, "last message " + id, Instant.parse("2026-11-10T10:00:00Z")))
                .createdAt(Instant.parse("2026-11-10T10:00:00Z"))
                .build();
    }

}
