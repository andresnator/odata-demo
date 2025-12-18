package com.example.odata.application.usecase;

import com.example.odata.domain.model.Brand;
import com.example.odata.domain.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Use Case - Application Layer
 * Orchestrates business logic without knowing implementation details
 */
@Service
@RequiredArgsConstructor
public class GetBrandsUseCase {

    private final BrandRepository brandRepository;

    public List<Brand> execute() {
        return brandRepository.findAll();
    }

    public Optional<Brand> executeById(int id) {
        return brandRepository.findById(id);
    }
}
