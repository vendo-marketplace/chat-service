package com.vendo.chat_service.adapter.message.out.persistence.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Embeddable
public class JpaLastMessage {

    String id;

    String senderId;

    @Column(length = 4000)
    String content;

    Instant createdAt;

}
