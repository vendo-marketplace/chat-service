package com.vendo.chat_service.adapter.conversation.out.persistence;

import com.vendo.chat_service.adapter.message.out.persistence.JpaMessage;
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

    @Column(length = 4000)
    private String lastMessage;

    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY)
    private List<JpaMessage> messages;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

}
