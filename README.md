# Ejemplo de OData con Spring Boot y Java 11 - Clean Architecture

Este proyecto implementa un servicio OData V4 usando **Spring Boot con @RestController** siguiendo los principios de **Clean Architecture** y **Clean Code**.

## 🏗️ Arquitectura

El proyecto está estructurado en **capas claramente separadas**:

- **Domain**: Entidades y contratos (sin dependencias externas)
- **Application**: Casos de uso y lógica de negocio
- **Infrastructure**: Implementaciones técnicas (repositorios, OData processors)
- **Presentation**: API REST (@RestController)

Ver documentación completa en: [CLEAN_ARCHITECTURE.md](CLEAN_ARCHITECTURE.md)

## 🚀 Cómo ejecutar

1. Ejecuta la aplicación:
   ```bash
   mvn spring-boot:run
   ```

2. El servicio estará disponible en: `http://localhost:8080/odata/`

## 📖 Ejemplos de uso

### 1. Obtener todos los productos (Select All implícito)
```
http://localhost:8080/odata/Products
```

### 2. Seleccionar campos específicos ($select)
Solo devuelve ID y Precio.
```
http://localhost:8080/odata/Products?$select=ID,Price
```

### 3. Filtro Igualdad (eq)
Productos con precio igual a 150.
```
http://localhost:8080/odata/Products?$filter=Price eq 150
```

### 4. Filtro Mayor que (gt) y Menor que (lt)
```
http://localhost:8080/odata/Products?$filter=Price gt 50
http://localhost:8080/odata/Products?$filter=Price lt 100
```

### 5. Contiene (Like aproximado)
Productos que contienen 'Mouse' en el nombre.
```
http://localhost:8080/odata/Products?$filter=contains(Name,'Mouse')
```

## 🔗 Relaciones y Microservicios (Simulado)

La entidad `Brand` (Marca) está relacionada con `Product` (N:1).
El servicio simula llamadas a microservicios **solo cuando se solicita la expansión**.

### 6. Obtener Marcas (Directo)
Llama al "microservicio" de marcas.
```
http://localhost:8080/odata/Brands
```

### 7. Expandir Marca en Productos ($expand) - ORQUESTACIÓN
Aquí ocurre la **orquestación**. El controlador:
1. Recupera todos los productos (llamada al servicio de Productos)
2. Por cada producto, hace una llamada al servicio de Marcas
3. Combina los datos y los devuelve

```
http://localhost:8080/odata/Products?$expand=Brand
```

### 8. Seleccionar y Expandir
Optimización: Traer solo nombre del producto y la marca completa.
```
http://localhost:8080/odata/Products?$select=Name&$expand=Brand
```

### 9. Consulta compleja combinada
```
http://localhost:8080/odata/Products?$expand=Brand&$select=Name,Price&$filter=Price lt 200
```

## ✍️ Crear Productos (POST)

### 10. Crear un nuevo producto
Endpoint para crear productos siguiendo el estándar OData.

**Request:**
```bash
POST http://localhost:8080/odata/Products
Content-Type: application/json

{
  "Name": "New Gaming Laptop",
  "Description": "High-performance gaming laptop with RTX 4080",
  "Price": 1899.99,
  "BrandID": 1
}
```

**Response (201 Created):**
```json
{
  "@odata.context": "/odata/$metadata#Products/$entity",
  "value": {
    "ID": 7,
    "Name": "New Gaming Laptop",
    "Description": "High-performance gaming laptop with RTX 4080",
    "Price": 1899.99,
    "BrandID": 1
  }
}
```

**Con curl:**
```bash
curl -X POST http://localhost:8080/odata/Products \
  -H "Content-Type: application/json" \
  -d '{
    "Name": "New Gaming Laptop",
    "Description": "High-performance gaming laptop with RTX 4080",
    "Price": 1899.99,
    "BrandID": 1
  }'
```

### Validación

El servicio genera automáticamente el ID y persiste el producto en el repositorio (en memoria para esta demo).

## 📊 Entidades

### Product
- ID: int
- Name: string
- Description: string  
- Price: double
- BrandID: int
- **Navigation**: Brand

### Brand
- ID: int
- Name: string
- Country: string

## 🧪 Verificar Logs de Microservicios

Al ejecutar consultas, verás en la consola logs como:
```
📦 [Product Microservice] Fetching all products
🏷️  [Brand Microservice] Fetching brand ID: 1
```

Esto demuestra la **orquestación selectiva**: el servicio de Brands solo se llama cuando usas `$expand=Brand`.

## 📚 Documentación Adicional

- [CLEAN_ARCHITECTURE.md](CLEAN_ARCHITECTURE.md) - Documentación completa de la arquitectura
- [ARCHITECTURE.md](ARCHITECTURE.md) - Documentación de la implementación anterior con Olingo
