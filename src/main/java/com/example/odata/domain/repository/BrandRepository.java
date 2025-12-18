package com.example.odata.domain.repository;

import com.example.odata.domain.model.Brand;
import java.util.List;
import java.util.Optional;

/**
 * Repository Interface - Domain Layer
 * Defines the contract without implementation details
 */
public interface BrandRepository {
    List<Brand> findAll();

    Optional<Brand> findById(int id);
}
