package com.example.odata.presentation.controller;

import com.example.odata.application.service.ODataQueryService;
import com.example.odata.infrastructure.odata.ODataFilterProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST Controller - Presentation Layer
 * Exposes OData endpoints following REST principles
 * Clean separation from business logic
 */
@Slf4j
@RestController
@RequestMapping("/odata")
@RequiredArgsConstructor
public class ODataController {

    private final ODataQueryService queryService;
    private final ODataFilterProcessor filterProcessor;

    /**
     * GET /odata/Products
     * OData Query Options:
     * - $expand=Brand (triggers microservice call)
     * - $select=Name,Price (projection)
     * - $filter=Price gt 100 (filtering)
     */
    @GetMapping("/Products")
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(value = "$expand", required = false) String expand,
            @RequestParam(value = "$select", required = false) String select,
            @RequestParam(value = "$filter", required = false) String filter) {

        log.info("📥 GET /odata/Products | expand={}, select={}, filter={}", expand, select, filter);

        boolean shouldExpand = expand != null && expand.contains("Brand");
        List<String> selectedFields = parseSelect(select);

        List<Map<String, Object>> entities = queryService.getProducts(shouldExpand, selectedFields);

        // Apply filters
        if (filter != null) {
            entities = filterProcessor.applyFilter(entities, filter);
        }

        Map<String, Object> response = buildODataResponse(entities, "Products");
        return ResponseEntity.ok(response);
    }

    /**
     * GET /odata/Brands
     */
    @GetMapping("/Brands")
    public ResponseEntity<Map<String, Object>> getBrands(
            @RequestParam(value = "$select", required = false) String select,
            @RequestParam(value = "$filter", required = false) String filter) {

        log.info("📥 GET /odata/Brands | select={}, filter={}", select, filter);

        List<String> selectedFields = parseSelect(select);
        List<Map<String, Object>> entities = queryService.getBrands(selectedFields);

        // Apply filters
        if (filter != null) {
            entities = filterProcessor.applyFilter(entities, filter);
        }

        Map<String, Object> response = buildODataResponse(entities, "Brands");
        return ResponseEntity.ok(response);
    }

    /**
     * GET /odata/$metadata
     * Returns service metadata (simplified version)
     */
    @GetMapping("/$metadata")
    public ResponseEntity<String> getMetadata() {
        String metadata = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<edmx:Edmx Version=\"4.0\" xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\">\n" +
                "  <edmx:DataServices>\n" +
                "    <Schema Namespace=\"OData.Demo\" xmlns=\"http://docs.oasis-open.org/odata/ns/edm\">\n" +
                "      <EntityType Name=\"Product\">\n" +
                "        <Key><PropertyRef Name=\"ID\"/></Key>\n" +
                "        <Property Name=\"ID\" Type=\"Edm.Int32\" Nullable=\"false\"/>\n" +
                "        <Property Name=\"Name\" Type=\"Edm.String\"/>\n" +
                "        <Property Name=\"Description\" Type=\"Edm.String\"/>\n" +
                "        <Property Name=\"Price\" Type=\"Edm.Double\"/>\n" +
                "        <Property Name=\"BrandID\" Type=\"Edm.Int32\"/>\n" +
                "        <NavigationProperty Name=\"Brand\" Type=\"OData.Demo.Brand\"/>\n" +
                "      </EntityType>\n" +
                "      <EntityType Name=\"Brand\">\n" +
                "        <Key><PropertyRef Name=\"ID\"/></Key>\n" +
                "        <Property Name=\"ID\" Type=\"Edm.Int32\" Nullable=\"false\"/>\n" +
                "        <Property Name=\"Name\" Type=\"Edm.String\"/>\n" +
                "        <Property Name=\"Country\" Type=\"Edm.String\"/>\n" +
                "      </EntityType>\n" +
                "      <EntityContainer Name=\"Container\">\n" +
                "        <EntitySet Name=\"Products\" EntityType=\"OData.Demo.Product\"/>\n" +
                "        <EntitySet Name=\"Brands\" EntityType=\"OData.Demo.Brand\"/>\n" +
                "      </EntityContainer>\n" +
                "    </Schema>\n" +
                "  </edmx:DataServices>\n" +
                "</edmx:Edmx>\n";

        return ResponseEntity.ok()
                .header("Content-Type", "application/xml")
                .body(metadata);
    }

    /**
     * GET /odata
     * Service document
     */
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getServiceDocument() {
        Map<String, Object> serviceDoc = new HashMap<>();
        serviceDoc.put("@odata.context", "/odata/$metadata");

        List<Map<String, String>> value = new ArrayList<>();
        value.add(Map.of("name", "Products", "url", "Products"));
        value.add(Map.of("name", "Brands", "url", "Brands"));

        serviceDoc.put("value", value);

        return ResponseEntity.ok(serviceDoc);
    }

    /**
     * POST /odata/Products
     * Creates a new product
     * 
     * Example request body:
     * {
     * "Name": "New Laptop",
     * "Description": "High-end gaming laptop",
     * "Price": 1500.00,
     * "BrandID": 1
     * }
     */
    @PostMapping("/Products")
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Map<String, Object> productData) {

        log.info("📥 POST /odata/Products | Creating new product: {}", productData.get("Name"));

        Map<String, Object> createdEntity = queryService.createProduct(productData);

        Map<String, Object> response = new HashMap<>();
        response.put("@odata.context", "/odata/$metadata#Products/$entity");
        response.put("value", createdEntity);

        return ResponseEntity
                .status(201) // HTTP 201 Created
                .body(response);
    }

    // Helper methods

    private List<String> parseSelect(String select) {
        if (select == null || select.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(select.split(","));
    }

    private Map<String, Object> buildODataResponse(List<Map<String, Object>> entities, String entitySetName) {
        Map<String, Object> response = new HashMap<>();
        response.put("@odata.context", "/odata/$metadata#" + entitySetName);
        response.put("value", entities);
        return response;
    }
}
