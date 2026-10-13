package com.vendo.chat_service.adapter.conversation.out.persistence;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document
@CompoundIndex(name = "unique_conversation_idx", def = "{'productId': 1, 'sellerId': 1, 'buyerId': 1}")
public class MongoConversation {

    @Id
    private String id;

    private String productId;
    private String sellerId;
    private String buyerId;
    private String lastMessage;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

}
