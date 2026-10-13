package com.vendo.chat_service.domain.user;

import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import lombok.Builder;

import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record User(
        String id,
        String email,
        UserStatus status,
        Set<UserRole> roles,
        boolean emailVerified
) {

    public Set<String> toRoleNames() {
        return roles.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

}