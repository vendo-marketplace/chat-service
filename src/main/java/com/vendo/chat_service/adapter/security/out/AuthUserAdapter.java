package com.vendo.chat_service.adapter.security.out;

import com.vendo.auto_search_service.domain.user.User;
import com.vendo.auto_search_service.domain.user.exception.UserNotOwnerException;
import com.vendo.auto_search_service.port.auth.AuthUserPort;
import com.vendo.security_starter.context.SecurityContextHelper;
import org.springframework.stereotype.Component;

@Component
public class AuthUserAdapter implements AuthUserPort {

    @Override
    public User getAuthUser() {
        return SecurityContextHelper.getAuthFromContext(User.class);
    }

    @Override
    public void validateAuthOwner(String otherUserId) {
        User authUser = getAuthUser();
        if (!authUser.id().equals(otherUserId)) {
            throw new UserNotOwnerException("You're not owner.");
        }
    }
}
