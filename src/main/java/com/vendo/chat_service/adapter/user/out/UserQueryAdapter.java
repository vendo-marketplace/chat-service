package com.vendo.chat_service.adapter.user.out;

import com.vendo.chat_service.domain.user.User;
import com.vendo.chat_service.port.user.UserQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserQueryAdapter implements UserQueryPort {

    private final UserClient userClient;

    @Override
    public User getById(String id) {
        return userClient.getById(id);
    }

}
