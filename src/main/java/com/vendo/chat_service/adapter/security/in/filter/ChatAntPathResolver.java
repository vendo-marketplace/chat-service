package com.vendo.chat_service.adapter.security.in.filter;

import com.vendo.chat_service.shared.PathProps;
import com.vendo.security_lib.resolver.AntPathResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class ChatAntPathResolver implements AntPathResolver {

    private final PathProps props;
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public boolean isPermittedPath(String path) {
        System.out.println("path " + path);
        System.out.println("matches" + Arrays.stream(props.allPaths())
                .anyMatch(pr -> antPathMatcher.match(pr, path)));
        System.out.println("---------------");
        return Arrays.stream(props.allPaths())
                .anyMatch(pr -> antPathMatcher.match(pr, path));
    }

}
