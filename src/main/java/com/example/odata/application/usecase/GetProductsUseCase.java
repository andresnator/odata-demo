package com.example.odata.application.usecase;

import com.example.odata.domain.model.Product;
import com.example.odata.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Use Case - Application Layer
 * Orchestrates business logic without knowing implementation details
 */
@Service
@RequiredArgsConstructor
public class GetProductsUseCase {

    private final ProductRepository productRepository;

    public List<Product> execute() {
        return productRepository.findAll();
    }
}
