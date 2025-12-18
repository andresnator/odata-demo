# OData Demo - Spring Boot & Clean Architecture

This project implements an **OData V4 Gateway** using **Spring Boot (@RestController)** following **Clean Architecture** principles. It orchestrates calls to multiple simulated data sources (microservices).

## 🏗️ Architecture

The project is structured into clearly separated layers:

-   **Domain**: Pure entities and repository contracts (no external dependencies).
-   **Application**: Use cases (e.g., `GetProductsUseCase`) and orchestration logic (`ODataQueryService`).
-   **Infrastructure**: Technical implementations such as In-Memory repositories and OData filter processing.
-   **Presentation**: REST API via `@RestController` (`ODataController`).

### Dependency Rule
Dependencies point inwards: **Presentation → Application → Domain** and **Infrastructure → Domain**.

---

## 📋 Prerequisites

-   **Java 11** or higher
-   **Maven 3.6+**

---

## 🚀 Getting Started

1.  **Run the application**:
    ```bash
    mvn spring-boot:run
    ```
2.  **Service available at**: `http://localhost:8080/odata/`

---

## 📖 API Usage Examples (OData V4)

### 1. Read Operations (GET)

| Feature | Endpoint |
| :--- | :--- |
| **All Products** | `/odata/Products` |
| **Selection** | `/odata/Products?$select=Name,Price` |
| **Filtering** | `/odata/Products?$filter=Price gt 100` |
| **Expansion** | `/odata/Products?$expand=Brand` (Orchestrates calls to Brand Service) |
| **Metadata** | `/odata/$metadata` |

**Example with curl**:
```bash
curl "http://localhost:8080/odata/Products?\$expand=Brand&\$select=Name,Price&\$filter=Price%20lt%20200"
```

### 2. Create Operations (POST)

**Endpoint**: `POST /odata/Products`

**Request Body**:
```json
{
  "Name": "Gaming Laptop",
  "Description": "High-performance laptop",
  "Price": 1899.99,
  "BrandID": 1
}
```

**curl Command**:
```bash
curl -X POST http://localhost:8080/odata/Products \
  -H "Content-Type: application/json" \
  -d '{"Name":"New Item","Price":99.99,"BrandID":1}'
```

---

## 🧪 Testing

-   **Unit Tests**: Located in `src/test/java`, testing use cases without Spring dependency.
-   **Integration Tests**: Testing the full OData flow via `MockMvc` in `ODataControllerIntegrationTest`.

---

## 🛠️ Key Technical Details

-   **Selective Orchestration**: The `ODataQueryService` only calls the Brand microservice if `$expand=Brand` is requested.
-   **Clean Implementation**: Unlike standard Olingo setups that use Servlets, this uses `@RestController` for better testability and Spring ecosystem integration.
-   **Repository Pattern**: Simulates microservices via `InMemoryProductRepository` and `InMemoryBrandRepository`.
