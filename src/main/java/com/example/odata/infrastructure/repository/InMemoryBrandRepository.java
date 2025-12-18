package com.example.odata.infrastructure.repository;

import com.example.odata.domain.model.Brand;
import com.example.odata.domain.repository.BrandRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Infrastructure Layer - In-Memory Implementation
 * Simulates a microservice data source
 */
@Repository
public class InMemoryBrandRepository implements BrandRepository {

    private final Map<Integer, Brand> storage = new HashMap<>();

    @PostConstruct
    public void init() {
        storage.put(1, new Brand(1, "TechGiant", "USA"));
        storage.put(2, new Brand(2, "ErgoSoft", "Germany"));
        storage.put(3, new Brand(3, "SpeedyMouse", "China"));
    }

    @Override
    public List<Brand> findAll() {
        System.out.println("🏷️  [Brand Microservice] Fetching all brands");
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Brand> findById(int id) {
        System.out.println("🏷️  [Brand Microservice] Fetching brand ID: " + id);
        return Optional.ofNullable(storage.get(id));
    }
}
