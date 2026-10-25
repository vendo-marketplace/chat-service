package com.vendo.chat_service.adapter.message.out.websocket;

import com.vendo.chat_service.domain.user.User;
import com.vendo.chat_service.port.user.AuthUserPort;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
class AuthenticationHandshakeHandler extends DefaultHandshakeHandler {

    private final AuthUserPort authUserPort;

    @Override
    protected @Nullable Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        User user = authUserPort.getAuthUser();

        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.toRoleNames().stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList()
        );
    }

}
