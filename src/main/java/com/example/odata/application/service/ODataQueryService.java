package com.example.odata.application.service;

import com.example.odata.application.usecase.GetBrandsUseCase;
import com.example.odata.application.usecase.GetProductsUseCase;
import com.example.odata.domain.model.Brand;
import com.example.odata.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Application Service - Coordinates Use Cases
 * Central orchestrator for OData operations
 */
@Service
@RequiredArgsConstructor
public class ODataQueryService {

    private final GetProductsUseCase getProductsUseCase;
    private final GetBrandsUseCase getBrandsUseCase;
    private final com.example.odata.application.usecase.CreateProductUseCase createProductUseCase;

    /**
     * Retrieves products with optional brand expansion
     * 
     * @param expand if true, enriches each product with its brand data (simulates
     *               microservice call)
     */
    public List<Map<String, Object>> getProducts(boolean expand, List<String> select) {
        List<Product> products = getProductsUseCase.execute();

        return products.stream()
                .map(product -> toODataEntity(product, expand, select))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getBrands(List<String> select) {
        List<Brand> brands = getBrandsUseCase.execute();

        return brands.stream()
                .map(brand -> toODataEntity(brand, select))
                .collect(Collectors.toList());
    }

    private Map<String, Object> toODataEntity(Product product, boolean expand, List<String> select) {
        Map<String, Object> entity = new HashMap<>();

        if (select == null || select.isEmpty() || select.contains("ID")) {
            entity.put("ID", product.getId());
        }
        if (select == null || select.isEmpty() || select.contains("Name")) {
            entity.put("Name", product.getName());
        }
        if (select == null || select.isEmpty() || select.contains("Description")) {
            entity.put("Description", product.getDescription());
        }
        if (select == null || select.isEmpty() || select.contains("Price")) {
            entity.put("Price", product.getPrice());
        }
        if (select == null || select.isEmpty() || select.contains("BrandID")) {
            entity.put("BrandID", product.getBrandId());
        }

        // ORCHESTRATION: Only call Brand microservice if $expand is requested
        if (expand) {
            getBrandsUseCase.executeById(product.getBrandId()).ifPresent(brand -> {
                Map<String, Object> brandEntity = new HashMap<>();
                brandEntity.put("ID", brand.getId());
                brandEntity.put("Name", brand.getName());
                brandEntity.put("Country", brand.getCountry());
                entity.put("Brand", brandEntity);
            });
        }

        return entity;
    }

    private Map<String, Object> toODataEntity(Brand brand, List<String> select) {
        Map<String, Object> entity = new HashMap<>();

        if (select == null || select.isEmpty() || select.contains("ID")) {
            entity.put("ID", brand.getId());
        }
        if (select == null || select.isEmpty() || select.contains("Name")) {
            entity.put("Name", brand.getName());
        }
        if (select == null || select.isEmpty() || select.contains("Country")) {
            entity.put("Country", brand.getCountry());
        }

        return entity;
    }

    /**
     * Creates a new product
     * 
     * @param productData Map with product data (Name, Description, Price, BrandID)
     * @return Created product as OData entity
     */
    public Map<String, Object> createProduct(Map<String, Object> productData) {
        // Convert Map to Product domain object
        Product product = new Product();
        product.setName((String) productData.get("Name"));
        product.setDescription((String) productData.get("Description"));
        product.setPrice(((Number) productData.get("Price")).doubleValue());
        product.setBrandId(((Number) productData.get("BrandID")).intValue());

        // Execute use case
        Product savedProduct = createProductUseCase.execute(product);

        // Convert back to OData format
        Map<String, Object> entity = new HashMap<>();
        entity.put("ID", savedProduct.getId());
        entity.put("Name", savedProduct.getName());
        entity.put("Description", savedProduct.getDescription());
        entity.put("Price", savedProduct.getPrice());
        entity.put("BrandID", savedProduct.getBrandId());

        return entity;
    }
}
