package com.example.odata.service;

import com.example.odata.model.Product;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class Storage {

    private List<Product> productList;

    @PostConstruct
    public void init() {
        productList = new ArrayList<>();
        // Brand 1: TechGiant, 2: ErgoSoft, 3: SpeedyMouse
        productList.add(new Product(1, "Notebook Basic", "Notebook Basic 15", 95.0, 1));
        productList.add(new Product(2, "Notebook Professional", "Notebook Professional 17", 420.0, 1));
        productList.add(new Product(3, "Monitor Ergo", "Monitor Ergo 24", 150.0, 2));
        productList.add(new Product(4, "Mouse Optical", "Mouse Optical USB", 15.0, 3));
        productList.add(new Product(5, "Mouse Wireless", "Mouse Wireless Bluetooth", 25.0, 3));
        productList.add(new Product(6, "Keyboard Standard", "Keyboard Standard USB", 20.0, 3));
    }

    public List<Product> getProducts() {
        return productList;
    }
}
