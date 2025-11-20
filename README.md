# Microservicio de Productos - HuertoHogar

Este microservicio maneja la gestión de productos para el sistema HuertoHogar. Ha sido adaptado del microservicio de usuarios original para manejar específicamente productos orgánicos y del campo.

## 🚀 Características

- **Puerto:** 8081 (producción) / 8082 (desarrollo)
- **Base de datos:** MongoDB Atlas
- **Framework:** Spring Boot 3.4.5 con Java 17
- **API REST:** Endpoints completos para CRUD de productos
- **Documentación:** Swagger/OpenAPI integrado
- **Validación:** Validaciones robustas con Bean Validation
- **Tests:** Tests unitarios y de integración incluidos
- **CI/CD:** GitHub Actions con despliegue automático a AWS EC2
- **Deployment:** Servicio systemd en Ubuntu

## 📦 Estructura del Proyecto

```
producto/
├── src/main/java/com/fullstack/producto/
│   ├── ProductoApplication.java          # Clase principal de la aplicación
│   ├── config/
│   │   └── SwaggerConfig.java           # Configuración de Swagger
│   ├── controller/
│   │   ├── ProductoController.java      # Controlador REST de productos
│   │   └── ReportesController.java      # Controlador de reportes
│   ├── model/
│   │   ├── dto/
│   │   │   ├── ProductoDto.java         # DTO de respuesta
│   │   │   ├── CrearProductoDto.java    # DTO para crear productos
│   │   │   ├── ActualizarProductoDto.java # DTO para actualizar productos
│   │   │   └── CategoriaDto.java        # DTO de categoría
│   │   └── entity/
│   │       └── ProductoEntity.java      # Documento MongoDB de producto
│   ├── repository/
│   │   └── ProductoRepository.java      # Repositorio MongoDB de productos
│   └── service/
│       └── ProductoService.java         # Lógica de negocio de productos
└── src/test/java/
    └── com/fullstack/producto/
        ├── ProductoApplicationTests.java # Tests de contexto de Spring
        └── ProductoTest.java           # Tests unitarios de productos
```

## 🛠️ Configuración

### Requisitos Previos
- Java 17 o superior
- Maven 3.6+
- Conexión a MongoDB Atlas

### Variables de Entorno
```bash
# No se requieren variables de entorno adicionales
# La conexión a MongoDB está configurada en application.properties
```

### Configuración de Base de Datos
El microservicio se conecta a MongoDB Atlas usando la URI configurada en `src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=${MONGODB_URI}
spring.data.mongodb.database=Huerto
```

**Nota de Seguridad:** Las credenciales deben configurarse mediante variables de entorno.

## 🚀 Ejecución

### Desarrollo
```bash
# Compilar el proyecto
./mvnw clean compile

# Ejecutar tests
./mvnw test

# Iniciar la aplicación
./mvnw spring-boot:run
```

La aplicación se ejecutará en `http://localhost:8082` (desarrollo) o `http://localhost:8081` (producción)

### Producción
```bash
# Crear el JAR ejecutable
./mvnw clean package -DskipTests

# Ejecutar el JAR
java -jar target/producto-0.0.1-SNAPSHOT.jar
```

## 🌐 Despliegue en AWS EC2

Este proyecto incluye CI/CD automático con GitHub Actions. Ver **[DEPLOYMENT.md](DEPLOYMENT.md)** para instrucciones detalladas.

### URLs de Producción
- **API Base**: `http://34.202.46.121:8081`
- **Health Check**: `http://34.202.46.121:8081/api/productos/health`
- **Swagger UI**: `http://34.202.46.121:8081/swagger-ui/index.html`

### Configuración Rápida
1. Ejecutar `setup-ec2.sh` en la EC2
2. Configurar secrets en GitHub (AWS_HOST, AWS_USER, SSH_PRIVATE_KEY)
3. Push a main → Despliegue automático

## 📚 API Endpoints

### Productos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/productos` | Obtener todos los productos |
| GET | `/api/productos/activos` | Obtener productos activos |
| GET | `/api/productos/disponibles` | Obtener productos con stock |
| GET | `/api/productos/{id}` | Obtener producto por ID |
| GET | `/api/productos/categoria/{idCategoria}` | Obtener productos por categoría |
| GET | `/api/productos/organicos` | Obtener productos orgánicos |
| GET | `/api/productos/buscar?nombre={nombre}` | Buscar productos por nombre |
| GET | `/api/productos/precio?precioMin={min}&precioMax={max}` | Filtrar por rango de precio |
| GET | `/api/productos/stock-bajo?stockMinimo={stock}` | Productos con stock bajo |
| POST | `/api/productos` | Crear nuevo producto |
| PUT | `/api/productos/{id}` | Actualizar producto |
| PATCH | `/api/productos/{id}/desactivar` | Desactivar producto |
| PATCH | `/api/productos/{id}/stock?stock={cantidad}` | Actualizar stock |
| DELETE | `/api/productos/{id}` | Eliminar producto |

### Health Check

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/productos/health` | Verificar estado del servicio |

## 🧪 Documentación de la API

Una vez que la aplicación esté ejecutándose, puedes acceder a la documentación interactiva de Swagger en:

```
http://localhost:8082/swagger-ui.html
```

## 📊 Modelo de Datos

### ProductoEntity (Documento MongoDB)
```java
- _id: String (PK) - ID único del producto (formato: XX000)
- nombre: String - Nombre del producto
- link_imagen: String - URL de la imagen del producto
- descripcion: String - Descripción del producto
- precio: Integer - Precio del producto
- stock: Integer - Cantidad en stock
- origen: String - Lugar de origen del producto
- certificacion_organica: String - Indicador de certificación orgánica (S/N)
- esta_activo: String - Estado activo/inactivo (S/N)
- fecha_ingreso: Date - Fecha de ingreso al inventario
- id_categoria: Integer - ID de la categoría
```

## 🏗️ Estructura de Base de Datos

El microservicio utiliza la colección `producto` en MongoDB con la siguiente estructura:

```javascript
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

## 🧪 Testing

### Ejecutar Tests Unitarios
```bash
./mvnw test -Dtest=ProductoTest
```

### Ejecutar Tests de Integración
```bash
./mvnw test -Dtest=ProductoApplicationTests
```

### Cobertura de Tests
Los tests cubren:
- ✅ CRUD completo de productos
- ✅ Validaciones de negocio
- ✅ Manejo de errores
- ✅ Consultas especializadas (por categoría, orgánicos, etc.)

## 🔧 Configuración Adicional

### Desactivar Validación de Templates
Si no usas Thymeleaf, agrega a `application.properties`:
```properties
spring.thymeleaf.check-template-location=false
```

### Configuración de CORS
El controlador ya incluye `@CrossOrigin(origins = "*")` para desarrollo. Para producción, configura orígenes específicos.

## 🐛 Troubleshooting

### Problemas Comunes

1. **Error de conexión a MongoDB**
   - Verifica que la URI de conexión sea correcta
   - Confirma que tu IP esté en la lista blanca de MongoDB Atlas
   - Revisa las credenciales de acceso

2. **Error de puerto en uso**
   - Asegúrate de que el puerto 8081/8082 esté disponible
   - Cambia el puerto en `application.properties` si es necesario

3. **Timeout de conexión**
   - Verifica tu conexión a internet
   - Confirma que el cluster de MongoDB Atlas esté activo

4. **Error de despliegue en EC2**
   - Verifica los secrets de GitHub Actions
   - Revisa los logs: `sudo journalctl -u producto-service -f`
   - Confirma que el Security Group permite tráfico en puerto 8081

## 📝 Logs

El microservicio incluye logging detallado:
- Nivel DEBUG para el paquete `com.fullstack.producto`
- Logs de operaciones de base de datos MongoDB
- Logs de errores y excepciones

## 🤝 Contribución

Para contribuir al proyecto:

1. Crea una rama nueva para tu feature
2. Implementa los cambios con tests
3. Asegúrate de que todos los tests pasen
4. Crea un Pull Request con descripción detallada

## 📄 Licencia

Este proyecto es parte del sistema HuertoHogar desarrollado para fines educativos.

---

**Microservicio de Productos v0.0.1-SNAPSHOT**  
Puerto: 8082 | Framework: Spring Boot 3.4.5 | Java: 17