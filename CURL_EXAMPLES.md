# Ejemplos de Prueba OData - curl

## 🚀 Iniciar el Servidor

```bash
mvn spring-boot:run
```

Espera a ver el mensaje: "Started ODataApplication in X seconds"

---

## 📖 GET - Consultas (READ)

### 1. Obtener todos los productos
```bash
curl http://localhost:8080/odata/Products
```

### 2. Seleccionar campos específicos ($select)
```bash
curl "http://localhost:8080/odata/Products?\$select=Name,Price"
```

### 3. Filtrar por precio mayor a 100 ($filter)
```bash
curl "http://localhost:8080/odata/Products?\$filter=Price%20gt%20100"
```

### 4. Filtrar productos que contienen "Mouse"
```bash
curl "http://localhost:8080/odata/Products?\$filter=contains(Name,'Mouse')"
```

### 5. Expandir marca (llama a 2 microservicios)
```bash
curl "http://localhost:8080/odata/Products?\$expand=Brand"
```

### 6. Consulta compleja: select + filter + expand
```bash
curl "http://localhost:8080/odata/Products?\$expand=Brand&\$select=Name,Price&\$filter=Price%20lt%20200"
```

### 7. Obtener todas las marcas
```bash
curl http://localhost:8080/odata/Brands
```

### 8. Metadata del servicio
```bash
curl http://localhost:8080/odata/\$metadata
```

---

## ✍️ POST - Crear Producto (CREATE)

### Ejemplo 1: Crear un laptop gaming
```bash
curl -X POST http://localhost:8080/odata/Products \
  -H "Content-Type: application/json" \
  -d '{
    "Name": "Gaming Laptop Pro",
    "Description": "High-performance gaming laptop with RTX 4080",
    "Price": 1899.99,
    "BrandID": 1
  }'
```

**Respuesta esperada (201 Created):**
```json
{
  "@odata.context": "/odata/$metadata#Products/$entity",
  "value": {
    "ID": 7,
    "Name": "Gaming Laptop Pro",
    "Description": "High-performance gaming laptop with RTX 4080",
    "Price": 1899.99,
    "BrandID": 1
  }
}
```

### Ejemplo 2: Crear un mouse inalámbrico
```bash
curl -X POST http://localhost:8080/odata/Products \
  -H "Content-Type: application/json" \
  -d '{
    "Name": "Wireless Mouse Pro",
    "Description": "Ergonomic wireless mouse with precision sensor",
    "Price": 49.99,
    "BrandID": 3
  }'
```

### Ejemplo 3: Crear un monitor 4K
```bash
curl -X POST http://localhost:8080/odata/Products \
  -H "Content-Type: application/json" \
  -d '{
    "Name": "UltraWide Monitor 4K",
    "Description": "34-inch curved 4K display",
    "Price": 799.99,
    "BrandID": 2
  }'
```

---

## 🧪 Verificar que el producto fue creado

Después de crear un producto, verifica que aparece en la lista:

```bash
curl http://localhost:8080/odata/Products
```

O busca específicamente el nuevo producto (si conoces el ID):

```bash
# Filtra por nombre
curl "http://localhost:8080/odata/Products?\$filter=contains(Name,'Gaming%20Laptop')"
```

---

## 📊 Verificar logs del servidor

Al ejecutar estos comandos, verás en la consola del servidor mensajes como:

```
📦 [Product Microservice] Fetching all products
🏷️  [Brand Microservice] Fetching brand ID: 1
📦 [Product Microservice] Saving product ID: 7
```

Esto demuestra la **orquestación de microservicios** y el **patrón Repository**.

---

## 💡 Tips

1. **Escapar caracteres especiales**: En bash, usa `\$` en lugar de `$` para los query params de OData
2. **URL encoding**: Espacios se codifican como `%20`
3. **Pretty print JSON**: Usa `| jq` al final del comando curl si tienes jq instalado
   ```bash
   curl http://localhost:8080/odata/Products | jq
   ```

---

## 🔍 Arquitectura en Acción

Cuando haces un POST:
1. **Controller** (Presentation) recibe el JSON
2. **QueryService** (Application) convierte a entidad de dominio
3. **CreateProductUseCase** (Application) ejecuta la lógica
4. **InMemoryProductRepository** (Infrastructure) persiste
5. ID se genera automáticamente
6. Respuesta viaja de vuelta transformándose en cada capa

Esta es **Clean Architecture** en acción! 🎯
