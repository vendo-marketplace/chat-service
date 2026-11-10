package com.vendo.chat_service.test_utils;

import com.vendo.chat_service.adapter.product.out.ProductClient;
import com.vendo.chat_service.adapter.user.out.UserClient;
import com.vendo.chat_service.domain.conversation.Conversation;
import com.vendo.chat_service.domain.conversation.exception.ConversationNotFoundException;
import com.vendo.chat_service.domain.user.User;
import com.vendo.chat_service.port.conversation.ConversationCommandPort;
import com.vendo.chat_service.port.conversation.ConversationQueryPort;
import com.vendo.chat_service.port.message.MessageCommandPort;
import com.vendo.chat_service.port.message.MessageQueryPort;
import com.vendo.security_lib.type.AuthHeader;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Instant;
import java.util.Set;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    protected static final User BUYER = user("buyer-id", "Buyer Name");
    protected static final User SELLER = user("seller-id", "Seller Name");
    protected static final User STRANGER = user("stranger-id", "Stranger Name");

    protected static final Instant CREATED_AT = Instant.parse("2026-11-10T10:00:00Z");

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected ConversationQueryPort conversationQueryPort;
    @MockitoBean
    protected ConversationCommandPort conversationCommandPort;
    @MockitoBean
    protected MessageQueryPort messageQueryPort;
    @MockitoBean
    protected MessageCommandPort messageCommandPort;

    @MockitoBean
    protected PlatformTransactionManager transactionManager;

    @MockitoBean
    protected ProductClient productClient;
    @MockitoBean
    protected UserClient userClient;

    protected static HttpHeaders authHeaders(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AuthHeader.ID.getHeader(), user.id());
        headers.add(AuthHeader.EMAIL.getHeader(), user.email());
        headers.add(AuthHeader.STATUS.getHeader(), user.status().name());
        headers.add(AuthHeader.ROLES.getHeader(), String.join(",", user.toRoleNames()));
        headers.add(AuthHeader.EMAIL_VERIFIED.getHeader(), String.valueOf(user.emailVerified()));
        return headers;
    }

    protected static Conversation conversation(String id, User seller, User buyer, String lastMessage) {
        return Conversation.builder()
                .id(id)
                .productId("product-" + id)
                .sellerId(seller.id())
                .buyerId(buyer.id())
                .lastMessage(lastMessage)
                .createdAt(CREATED_AT)
                .build();
    }

    protected Conversation stubConversation(String id, User seller, User buyer, String lastMessage) {
        Conversation conversation = conversation(id, seller, buyer, lastMessage);
        when(conversationQueryPort.findById(id)).thenReturn(conversation);
        return conversation;
    }

    protected void stubMissingConversation(String id) {
        when(conversationQueryPort.findById(id)).thenThrow(new ConversationNotFoundException("Conversation not found."));
    }

    protected void stubUsers(User... users) {
        for (User user : users) {
            when(userClient.getById(user.id())).thenReturn(user);
        }
    }

    private static User user(String id, String fullName) {
        return User.builder()
                .id(id)
                .email(id + "@vendo.test")
                .fullName(fullName)
                .status(UserStatus.ACTIVE)
                .roles(Set.of(UserRole.USER))
                .emailVerified(true)
                .build();
    }

}
