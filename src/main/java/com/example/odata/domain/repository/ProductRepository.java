package com.example.odata.domain.repository;

import com.example.odata.domain.model.Product;
import java.util.List;
import java.util.Optional;

/**
 * Repository Interface - Domain Layer
 * Defines the contract without implementation details
 */
public interface ProductRepository {
    List<Product> findAll();

    Optional<Product> findById(int id);

    Product save(Product product);
}
