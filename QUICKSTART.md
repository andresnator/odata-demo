# Guía Rápida - OData Clean Architecture

## ✅ Proyecto Refactorizado Exitosamente

Se ha refactorizado completamente el proyecto de OData para usar **@RestController** siguiendo **Clean Architecture**.

## 🏗️ Nueva Estructura

```
src/main/java/com/example/odata/
├── domain/                  # ❤️  CORE - Sin dependencias externas
│   ├── model/
│   │   ├── Product.java
│   │   └── Brand.java
│   └── repository/          # Contratos/Interfaces
│       ├── ProductRepository.java
│       └── BrandRepository.java
│
├── application/             # 🎯 CASOS DE USO
│   ├── usecase/
│   │   ├── GetProductsUseCase.java
│   │   └── GetBrandsUseCase.java
│   └── service/
│       └── ODataQueryService.java  # Orquestador principal
│
├── infrastructure/          # 🔧 IMPLEMENTACIONES
│   ├── repository/
│   │   ├── InMemoryProductRepository.java  # Simula microservicio
│   │   └── InMemoryBrandRepository.java    # Simula microservicio
│   └── odata/
│       └── ODataFilterProcessor.java
│
└── presentation/            # 🌐 API REST
    └── controller/
        └── ODataController.java  # @RestController
```

## 🚀 Cambios Principales vs Versión Anterior

| Aspecto | Antes (Servlet) | Ahora (@RestController) |
|---------|----------------|-------------------------|
| **Entrada** | Servlet Olingo | Spring @RestController |
| **Separación** | Todo mezclado | Capas claras (Domain/App/Infra) |
| **Testeo** | Difícil | Fácil (inyección de dependencias) |
| **Filtros** | ExpressionVisitor complejo | Lógica simplificada en ODataFilterProcessor |
| **Expand** | OData nativo | Lógica manual en ODataQueryService |

## 📍 Endpoints Disponibles

### Consultas (GET)
```bash
# Service Document
GET http://localhost:8080/odata/

# Metadata
GET http://localhost:8080/odata/$metadata

# Productos
GET http://localhost:8080/odata/Products
GET http://localhost:8080/odata/Products?$select=Name,Price
GET http://localhost:8080/odata/Products?$filter=Price gt 100
GET http://localhost:8080/odata/Products?$expand=Brand

# Marcas
GET http://localhost:8080/odata/Brands

# Complejo
GET http://localhost:8080/odata/Products?$expand=Brand&$select=Name&$filter=Price lt 200
```

### Creación (POST)
```bash
POST http://localhost:8080/odata/Products
Content-Type: application/json

{
  "Name": "New Gaming Laptop",
  "Description": "High-performance laptop",
  "Price": 1899.99,
  "BrandID": 1
}
```

Ver ejemplos completos con curl en: **CURL_EXAMPLES.md**

## 🔍 Dónde se Implementa el $expand

**Archivo**: `ODataQueryService.java` (línea ~31)

```java
public List<Map<String, Object>> getProducts(boolean expand, List<String> select) {
    List<Product> products = getProductsUseCase.execute();
    
    return products.stream()
        .map(product -> toODataEntity(product, expand, select))
        .collect(Collectors.toList());
}

// ...

// ORQUESTACIÓN: Solo llama a Brand si expand=true
if (expand) {
    getBrandsUseCase.executeById(product.getBrandId()).ifPresent(brand -> {
        // Crea el objeto Brand e incrusta en Product
        entity.put("Brand", brandEntity);
    });
}
```

## 🧪 Ejecutar y Probar

```bash
# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run

# En otra terminal:
curl http://localhost:8080/odata/Products?$expand=Brand
```

Verás en los logs:
```
📦 [Product Microservice] Fetching all products
🏷️  [Brand Microservice] Fetching brand ID: 1
🏷️  [Brand Microservice] Fetching brand ID: 2
...
```

## 📚 Documentación Completa

- **CLEAN_ARCHITECTURE.md**: Explicación detallada de la arquitectura
- **README.md**: Guía de uso
- **ARCHITECTURE.md**: Documentación de la versión anterior (Olingo nativo)

## 🎯 Ventajas de Clean Architecture

1. **Testeable**: Cada componente aislado
2. **Mantenible**: Cambios en UI no afectan lógica
3. **Flexible**: Fácil cambiar de in-memory a PostgreSQL
4. **Escalable**: Agregar entidades sin romper código existente
