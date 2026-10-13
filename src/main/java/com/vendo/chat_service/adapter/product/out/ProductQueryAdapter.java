package com.vendo.chat_service.adapter.product.out;

import com.vendo.chat_service.domain.product.Product;
import com.vendo.chat_service.port.product.ProductQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductQueryAdapter implements ProductQueryPort {

    private final ProductClient client;

    @Override
    public Product findById(String id) {
        return client.findById(id);
    }
}
