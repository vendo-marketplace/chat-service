package com.vendo.chat_service.adapter.user.in.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserProfileResponse(

        String id,
        String fullName

) {
}
