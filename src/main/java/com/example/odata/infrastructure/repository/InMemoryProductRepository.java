package com.example.odata.infrastructure.repository;

import com.example.odata.domain.model.Product;
import com.example.odata.domain.repository.ProductRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Infrastructure Layer - In-Memory Implementation
 * Simulates a microservice data source
 */
@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final Map<Integer, Product> storage = new HashMap<>();

    @PostConstruct
    public void init() {
        // Brand 1: TechGiant, 2: ErgoSoft, 3: SpeedyMouse
        storage.put(1, new Product(1, "Notebook Basic", "Notebook Basic 15", 95.0, 1));
        storage.put(2, new Product(2, "Notebook Professional", "Notebook Professional 17", 420.0, 1));
        storage.put(3, new Product(3, "Monitor Ergo", "Monitor Ergo 24", 150.0, 2));
        storage.put(4, new Product(4, "Mouse Optical", "Mouse Optical USB", 15.0, 3));
        storage.put(5, new Product(5, "Mouse Wireless", "Mouse Wireless Bluetooth", 25.0, 3));
        storage.put(6, new Product(6, "Keyboard Standard", "Keyboard Standard USB", 20.0, 3));
    }

    @Override
    public List<Product> findAll() {
        System.out.println("📦 [Product Microservice] Fetching all products");
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Product> findById(int id) {
        System.out.println("📦 [Product Microservice] Fetching product ID: " + id);
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Product save(Product product) {
        // Auto-generate ID if not present
        if (product.getId() == 0) {
            int newId = storage.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
            product.setId(newId);
        }

        System.out.println("📦 [Product Microservice] Saving product ID: " + product.getId());
        storage.put(product.getId(), product);
        return product;
    }
}
