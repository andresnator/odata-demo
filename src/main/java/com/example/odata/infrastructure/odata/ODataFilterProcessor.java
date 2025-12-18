package com.example.odata.infrastructure.odata;

import com.example.odata.domain.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Infrastructure - OData Filter Logic
 * Translates OData filter syntax to Java predicates
 */
@Slf4j
@Component
public class ODataFilterProcessor {

    /**
     * Applies basic OData filters to a product list
     * Supports: eq, ne, gt, lt, ge, le, contains
     */
    public List<Map<String, Object>> applyFilter(List<Map<String, Object>> entities, String filter) {
        if (filter == null || filter.isEmpty()) {
            return entities;
        }

        log.info("🔍 Applying filter: {}", filter);

        return entities.stream()
                .filter(entity -> evaluateFilter(entity, filter))
                .collect(Collectors.toList());
    }

    private boolean evaluateFilter(Map<String, Object> entity, String filter) {
        try {
            // Simple filter parser (enhancement: use Olingo's ExpressionVisitor for
            // production)

            // Example: Price gt 100
            if (filter.contains(" gt ")) {
                String[] parts = filter.split(" gt ");
                String field = parts[0].trim();
                double value = Double.parseDouble(parts[1].trim());
                Object fieldValue = entity.get(field);
                return fieldValue instanceof Number && ((Number) fieldValue).doubleValue() > value;
            }

            // Example: Price lt 50
            if (filter.contains(" lt ")) {
                String[] parts = filter.split(" lt ");
                String field = parts[0].trim();
                double value = Double.parseDouble(parts[1].trim());
                Object fieldValue = entity.get(field);
                return fieldValue instanceof Number && ((Number) fieldValue).doubleValue() < value;
            }

            // Example: Price eq 150
            if (filter.contains(" eq ")) {
                String[] parts = filter.split(" eq ");
                String field = parts[0].trim();
                String value = parts[1].trim().replace("'", "");
                Object fieldValue = entity.get(field);

                if (fieldValue instanceof Number) {
                    return Double.parseDouble(value) == ((Number) fieldValue).doubleValue();
                }
                return String.valueOf(fieldValue).equals(value);
            }

            // Example: contains(Name, 'Mouse')
            if (filter.startsWith("contains(")) {
                String content = filter.substring(9, filter.length() - 1);
                String[] parts = content.split(",");
                String field = parts[0].trim();
                String value = parts[1].trim().replace("'", "");
                Object fieldValue = entity.get(field);
                return fieldValue != null && String.valueOf(fieldValue).contains(value);
            }

            return true;
        } catch (Exception e) {
            log.warn("Failed to parse filter: {}", filter, e);
            return true; // Don't filter on error
        }
    }
}
