package com.vendo.chat_service.adapter.conversation.out.persistence;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class JpaReadState {

    private String lastReadMessageId;
    private Instant lastReadAt;

}
