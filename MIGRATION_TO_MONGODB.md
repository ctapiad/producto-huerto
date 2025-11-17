# Migración a MongoDB

Este documento describe la migración del microservicio de productos de Oracle Cloud a MongoDB Atlas.

## 🔄 Cambios Realizados

### 1. Base de Datos
- **Antes:** Oracle Cloud Database con Wallet
- **Después:** MongoDB Atlas

### 2. Dependencias Maven
**Eliminadas:**
- `spring-boot-starter-data-jpa`
- `ojdbc8`
- `oraclepki`

**Agregadas:**
- `spring-boot-starter-data-mongodb`

### 3. Configuración
- Eliminados: archivos de wallet y configuración Oracle
- Actualizados: `application.properties`, `application-test.properties`, `application-prod.properties`
- Nueva URI de conexión: `mongodb+srv://ctapiad_db_user:***@huerto.bi4rvwk.mongodb.net/Huerto`

### 4. Modelo de Datos
- `@Entity` → `@Document`
- `@Table` → `@Document(collection = "producto")`
- `@Column` → `@Field`
- `JpaRepository` → `MongoRepository`
- Queries JPQL → Queries MongoDB (JSON)

### 5. Tipos de Datos
- `precio`: `BigDecimal` → `Integer`
- `idCategoria`: `Long` → `Integer`
- `idProducto` → `id` (campo `_id` de MongoDB)

### 6. Archivos Eliminados
- `OracleWalletConfig.java`
- `src/main/resources/wallet_extracted/` (directorio completo)
- `src/main/resources/database/` (scripts SQL)
- `setup_and_run.sh` (script de configuración Oracle)

### 7. Archivos Actualizados
- `README.md` - Actualizado con información de MongoDB
- `run_app.sh` - Simplificado para MongoDB
- Todos los archivos Java del proyecto

## 📊 Estructura de MongoDB

**Cluster:** huerto.bi4rvwk.mongodb.net  
**Base de datos:** Huerto  
**Colección:** producto

### Documento de Ejemplo:
```json
{
    "_id": "FR001",
    "nombre": "Manzanas Fuji",
    "link_imagen": null,
    "descripcion": "Manzanas crujientes y dulces del Valle del Maule.",
    "precio": 1200,
    "stock": 150,
    "origen": "Valle del Maule",
    "certificacion_organica": "N",
    "esta_activo": "S",
    "fecha_ingreso": ISODate("2024-01-20T10:30:00.000Z"),
    "id_categoria": 1
}
```

## 🚀 Ejecución

### Ejecutar la aplicación:
```bash
./run_app.sh
```

### O manualmente:
```bash
./mvnw clean compile
./mvnw spring-boot:run
```

## ✅ Verificación

1. La aplicación debe iniciar sin errores
2. Debe conectarse a MongoDB Atlas exitosamente
3. Los endpoints deben responder correctamente
4. Swagger UI disponible en: http://localhost:8082/swagger-ui.html

## 🔗 Conexión a MongoDB

La conexión está configurada en `application.properties`:
```properties
spring.data.mongodb.uri=mongodb+srv://ctapiad_db_user:MhRBXg6OTYK9AqQv@huerto.bi4rvwk.mongodb.net/Huerto
spring.data.mongodb.database=Huerto
```

**Nota:** En producción, considera usar variables de entorno para las credenciales.

## 📝 Fecha de Migración
12 de noviembre de 2025
