package com.vendo.chat_service.adapter.conversation.out.persistence;

import com.vendo.chat_service.adapter.message.out.persistence.JpaMessage;
import com.vendo.chat_service.adapter.message.out.persistence.embedded.JpaLastMessage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "conversations")
public class JpaConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String productId;

    private String sellerId;
    private String buyerId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "last_message_id")),
            @AttributeOverride(name = "senderId", column = @Column(name = "last_message_sender_id")),
            @AttributeOverride(name = "content", column = @Column(name = "last_message_content", length = 4000)),
            @AttributeOverride(name = "createdAt", column = @Column(name = "last_message_created_at"))
    })
    private JpaLastMessage lastMessage;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lastReadMessageId", column = @Column(name = "seller_last_read_message_id")),
            @AttributeOverride(name = "lastReadAt", column = @Column(name = "seller_last_read_at"))
    })
    private JpaReadState sellerReadState;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lastReadMessageId", column = @Column(name = "buyer_last_read_message_id")),
            @AttributeOverride(name = "lastReadAt", column = @Column(name = "buyer_last_read_at"))
    })
    private JpaReadState buyerReadState;

    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY)
    private List<JpaMessage> messages;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

}
