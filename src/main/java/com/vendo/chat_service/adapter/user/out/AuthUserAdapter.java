package com.vendo.chat_service.adapter.user.out;

import com.vendo.chat_service.domain.user.User;
import com.vendo.chat_service.port.user.AuthUserPort;
import com.vendo.security_starter.context.SecurityContextHelper;
import org.springframework.stereotype.Component;

@Component
public class AuthUserAdapter implements AuthUserPort {

    @Override
    public User getAuthUser() {
        return SecurityContextHelper.getAuthFromContext(User.class);
    }
}
