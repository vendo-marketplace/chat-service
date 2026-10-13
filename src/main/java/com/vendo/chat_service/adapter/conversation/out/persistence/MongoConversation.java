package com.vendo.chat_service.adapter.conversation.out.persistence;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document
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
