package com.example.odata.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain Entity - Pure business object with no dependencies
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Brand {
    private int id;
    private String name;
    private String country;
}
