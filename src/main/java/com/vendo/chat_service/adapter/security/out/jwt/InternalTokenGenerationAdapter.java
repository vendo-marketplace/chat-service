package com.vendo.chat_service.adapter.security.out.jwt;

import com.vendo.chat_service.adapter.security.out.jwt.props.JwtProperties;
import com.vendo.core_lib.types.ServiceName;
import com.vendo.core_lib.types.ServiceRole;
import com.vendo.security_lib.type.TokenClaim;
import com.vendo.security_starter.jwt.JwtPayload;
import com.vendo.security_starter.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class InternalTokenGenerationAdapter implements InternalTokenGenerationPort {

    private final JwtProperties props;

    @Override
    public String generate(ServiceName audience) {
        JwtProperties.Internal internal = props.getInternal();

        JwtPayload jwtPayload = JwtPayload.builder()
                // TODO
//                .subject(ServiceName.CHAT_SERVICE.getServiceName())
                .audience(Set.of(audience.getServiceName()))
                .claims(Map.of(TokenClaim.ROLES.getClaim(), ServiceRole.INTERNAL.name()))
                .expiration(internal.expirationTime())
                .build();
        return JwtService.buildToken(jwtPayload, internal.key());
    }

}
