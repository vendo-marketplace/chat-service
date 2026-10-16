package com.vendo.chat_service.infrastructure;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.vendo.chat_service.adapter.product.out")
public class OpenFeignConfig {
}
