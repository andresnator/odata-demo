package com.example.odata.service;

import com.example.odata.model.Brand;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BrandStorage {

    // Simulates a remote microservice repository
    private List<Brand> brandList;
    private Map<Integer, Brand> brandMap;

    @PostConstruct
    public void init() {
        brandList = new ArrayList<>();
        brandList.add(new Brand(1, "TechGiant", "USA"));
        brandList.add(new Brand(2, "ErgoSoft", "Germany"));
        brandList.add(new Brand(3, "SpeedyMouse", "China"));

        brandMap = brandList.stream().collect(Collectors.toMap(Brand::getId, Function.identity()));
    }

    public List<Brand> getBrands() {
        // Simulates a call to get all brands
        System.out.println("Microservice Call: Fetching ALL Brands");
        return brandList;
    }

    public Brand getBrandById(int id) {
        // Simulates a specific call to get a brand
        System.out.println("Microservice Call: Fetching Brand ID: " + id);
        return brandMap.get(id);
    }
}
