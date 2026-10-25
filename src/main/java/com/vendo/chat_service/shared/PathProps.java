package com.vendo.chat_service.shared;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "endpoints.unauthenticated")
public class PathProps {

    private Set<String> general;

    public String[] allPaths() {
        return Stream.of(general)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toArray(String[]::new);
    }

}