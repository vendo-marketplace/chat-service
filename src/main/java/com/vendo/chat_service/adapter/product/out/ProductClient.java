package com.vendo.chat_service.adapter.product.out;

import com.vendo.chat_service.adapter.product.out.config.ProductFeignConfig;
import com.vendo.chat_service.domain.product.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(
        name = "product-service",
        path = "/internal/products",
        configuration = ProductFeignConfig.class)
public interface ProductClient {

    @GetMapping("/{id}")
    Product findById(@PathVariable String id);

}
