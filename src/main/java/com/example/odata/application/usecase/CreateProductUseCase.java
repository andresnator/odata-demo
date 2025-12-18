package com.example.odata.application.usecase;

import com.example.odata.domain.model.Product;
import com.example.odata.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Use Case - Application Layer
 * Creates a new product
 */
@Service
@RequiredArgsConstructor
public class CreateProductUseCase {

    private final ProductRepository productRepository;

    public Product execute(Product product) {
        return productRepository.save(product);
    }
}
