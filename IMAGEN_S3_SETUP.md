# Configuración de Subida de Imágenes a S3

## Resumen de Cambios

Se ha implementado la funcionalidad de subida de imágenes desde el dispositivo móvil a Amazon S3 usando **Presigned URLs** para mayor seguridad.

## Arquitectura

1. **Móvil** → Selecciona imagen de la galería
2. **Móvil** → Solicita URL prefirmada al backend
3. **Backend** → Genera URL firmada con permisos temporales (15 minutos)
4. **Móvil** → Sube imagen directamente a S3 usando la URL firmada
5. **Móvil** → Guarda la URL pública de S3 en el producto

## Cambios en el Backend (producto-huerto)

### 1. Dependencia Maven agregada

```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.20.26</version>
</dependency>
```

### 2. Archivos Creados

- **`S3Config.java`**: Configuración del cliente S3 y S3Presigner
- **`S3Service.java`**: Lógica para generar URLs prefirmadas
- **`UploadUrlRequestDto.java`**: DTO para solicitudes de URL

### 3. Endpoint Agregado

**POST** `/api/productos/upload-url`

**Request Body:**
```json
{
  "fileName": "mi-imagen.jpg",
  "contentType": "image/jpeg"
}
```

**Response:**
```json
{
  "uploadUrl": "https://image-huerto.s3.us-east-1.amazonaws.com/productos/imagenes/uuid.jpg?X-Amz-...",
  "imageUrl": "https://image-huerto.s3.us-east-1.amazonaws.com/productos/imagenes/uuid.jpg",
  "key": "productos/imagenes/uuid.jpg",
  "expiresIn": "900"
}
```

### 4. Configuración Requerida

#### Opción A: Usando IAM Role en EC2 (Recomendado) ⭐

Si tu microservicio está desplegado en una **instancia EC2 de AWS**:

1. **Crear IAM Role**:
   - Ve a AWS Console → IAM → Roles → Create Role
   - Tipo: AWS Service → EC2
   - Adjunta la política con permisos S3 (ver sección "Configuración del Bucket S3")
   - Nombre: `EC2-S3-ImageUpload-Role`

2. **Asignar Role a EC2**:
   - EC2 Console → Selecciona tu instancia
   - Actions → Security → Modify IAM Role
   - Selecciona el role creado

3. **Configurar application.properties**:
```properties
# Solo necesitas establecer esto en true
aws.s3.use-iam-role=true
```

O como variable de entorno:
```bash
export AWS_USE_IAM_ROLE=true
```

**✅ Ventajas**:
- No necesitas credenciales explícitas
- Más seguro (credenciales rotan automáticamente)
- Sin riesgo de exponer keys en el código
- Gestión centralizada desde IAM

---

#### Opción B: Usando Credenciales Explícitas (Desarrollo Local)

Para desarrollo local o servidores fuera de AWS:

Agregar las siguientes variables de entorno en el servidor:

```bash
export AWS_ACCESS_KEY_ID="tu_access_key_aqui"
export AWS_SECRET_ACCESS_KEY="tu_secret_key_aqui"
export AWS_USE_IAM_ROLE=false
```

O crear un archivo `.env` en la raíz del proyecto:

```properties
AWS_ACCESS_KEY_ID=tu_access_key_aqui
AWS_SECRET_ACCESS_KEY=tu_secret_key_aqui
AWS_USE_IAM_ROLE=false
```

---

**Valores ya configurados en `application.properties`:**
- Bucket: `image-huerto`
- Región: `us-east-1`
- Carpeta: `productos/imagenes`
- Duración URL: 15 minutos

## Cambios en el Móvil (Huerto-Mobile)

### 1. Archivos Creados

- **`ImageUploadService.kt`**: Servicio para manejar selección y subida de imágenes
  - `getFileInfo()`: Obtiene nombre, tipo MIME y tamaño
  - `uploadToS3()`: Sube archivo a S3 usando URL prefirmada
  - `isValidImageType()`: Valida tipos de imagen (JPEG, PNG, GIF, WEBP)
  - `isValidFileSize()`: Limita a 5MB por defecto

### 2. Archivos Modificados

#### **ProductApiService.kt**
- Agregados DTOs: `UploadUrlRequest` y `UploadUrlResponse`
- Agregado endpoint: `getPresignedUploadUrl()`

#### **ProductRepository.kt**
- Agregado método: `getPresignedUploadUrl()`

#### **ProductoViewModel.kt**
- Agregado parámetro: `ImageUploadService` (opcional)
- Nuevos StateFlows: `selectedImageUri`, `uploadingImage`
- Nuevos métodos:
  - `onImageSelected(uri: Uri)`: Guarda URI de imagen seleccionada
  - `clearImage()`: Limpia imagen seleccionada
  - `uploadSelectedImage()`: Sube imagen a S3 y actualiza `linkImagen`
- Validación actualizada: Ahora requiere que se haya seleccionado una imagen

#### **ProductManagementScreen.kt**
- Campo de texto URL reemplazado por:
  - Vista previa de imagen (si está seleccionada o ya existe)
  - Botón "Seleccionar" / "Cambiar" para abrir galería
  - Botón "Subir" para enviar imagen a S3 (solo si está pendiente)
  - Botón "X" para quitar imagen
- Agregado `rememberLauncherForActivityResult` para selección de imágenes
- El ViewModel ahora recibe `ImageUploadService` como dependencia

### 3. Flujo de Usuario

1. Usuario abre formulario de producto
2. Click en "Seleccionar" imagen
3. Elige imagen de la galería
4. Se muestra vista previa
5. Click en "Subir" (solo si es nueva selección)
   - Se valida tipo (imagen) y tamaño (máx 5MB)
   - Se solicita URL prefirmada al backend
   - Se sube imagen directamente a S3
   - Se actualiza `linkImagen` con URL pública
6. Al guardar producto, se usa la URL de S3

### 4. Validaciones Implementadas

- **Tipo de archivo**: Solo imágenes (JPEG, JPG, PNG, GIF, WEBP)
- **Tamaño máximo**: 5MB
- **Imagen requerida**: No se puede crear producto sin imagen
- **URL válida**: Debe comenzar con `http://` o `https://`

## Configuración del Bucket S3

### Permisos IAM Role para EC2

Si usas IAM Role (Opción A), crea una política con estos permisos:

**Nombre de la política**: `S3-ImageUpload-Policy`

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:DeleteObject"
      ],
      "Resource": "arn:aws:s3:::image-huerto/productos/imagenes/*"
    }
  ]
}
```

### Permisos Usuario IAM (si no usas EC2)

Si usas credenciales explícitas (Opción B), el usuario IAM debe tener los siguientes permisos:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject"
      ],
      "Resource": "arn:aws:s3:::image-huerto/productos/imagenes/*"
    }
  ]
}
```

### Configuración CORS del Bucket

Para permitir subidas desde la app móvil:

```json
[
  {
    "AllowedHeaders": ["*"],
    "AllowedMethods": ["PUT", "GET"],
    "AllowedOrigins": ["*"],
    "ExposeHeaders": ["ETag"]
  }
]
```

### Política del Bucket (Opcional)

Si deseas que las imágenes sean públicas:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::image-huerto/productos/imagenes/*"
    }
  ]
}
```

## Despliegue en EC2

### Configuración Completa para EC2

#### 1. Crear y Asignar IAM Role

```bash
# 1. Crear la política de permisos
aws iam create-policy \
  --policy-name S3-ImageUpload-Policy \
  --policy-document file://s3-policy.json

# 2. Crear el role
aws iam create-role \
  --role-name EC2-S3-ImageUpload-Role \
  --assume-role-policy-document file://trust-policy.json

# 3. Adjuntar la política al role
aws iam attach-role-policy \
  --role-name EC2-S3-ImageUpload-Role \
  --policy-arn arn:aws:iam::YOUR_ACCOUNT_ID:policy/S3-ImageUpload-Policy

# 4. Asignar role a la instancia EC2
aws ec2 associate-iam-instance-profile \
  --instance-id i-1234567890abcdef0 \
  --iam-instance-profile Name=EC2-S3-ImageUpload-Role
```

#### 2. Variables de Entorno en EC2

Edita tu archivo de servicio o `.bashrc`:

```bash
# En /etc/environment o ~/.bashrc
export AWS_USE_IAM_ROLE=true
export MONGODB_URI="mongodb+srv://..."
export MONGODB_PASSWORD="..."
```

#### 3. Verificar Configuración

```bash
# Verificar que el role está asignado
curl http://169.254.169.254/latest/meta-data/iam/security-credentials/

# Debe mostrar el nombre del role: EC2-S3-ImageUpload-Role
```

### Diferencias Clave EC2 vs Local

| Aspecto | En EC2 con IAM Role | Local o Servidor Externo |
|---------|---------------------|--------------------------|
| **Credenciales** | No necesarias | Access Key + Secret Key |
| **Seguridad** | ⭐ Alta (rotan automáticamente) | ⚠️ Media (estáticas) |
| **Configuración** | `AWS_USE_IAM_ROLE=true` | Variables de entorno con keys |
| **Gestión** | Centralizada en IAM | Manual |
| **Riesgo** | Bajo | Medio (si se exponen) |

## Pruebas

### Probar el Endpoint del Backend

```bash
curl -X POST http://localhost:8081/api/productos/upload-url \
  -H "Content-Type: application/json" \
  -d '{
    "fileName": "test.jpg",
    "contentType": "image/jpeg"
  }'
```

### Probar Subida a S3

```bash
# Usando la uploadUrl obtenida del endpoint anterior
curl -X PUT "<uploadUrl>" \
  -H "Content-Type: image/jpeg" \
  --data-binary "@imagen.jpg"
```

## Ventajas de esta Implementación

✅ **Seguridad**: Las credenciales AWS nunca están en el móvil  
✅ **Escalabilidad**: Las subidas van directo a S3, no pasan por el backend  
✅ **Performance**: Reduce carga del servidor  
✅ **Control**: El backend valida permisos antes de generar URLs  
✅ **Temporal**: URLs expiran en 15 minutos  
✅ **Validación**: Se valida tipo y tamaño antes de subir  

## Solución de Problemas

### Error: "Error al generar URL de subida"
- Verificar que las credenciales AWS estén configuradas
- Verificar que el bucket existe y está en us-east-1
- Verificar permisos IAM

### Error: "Error al subir la imagen a S3"
- Verificar configuración CORS del bucket
- Verificar que la URL no haya expirado (15 min)
- Verificar conectividad de red

### La imagen no se muestra después de subir
- Verificar que el bucket tenga permisos de lectura pública
- Verificar que la URL generada sea correcta
- Verificar en la consola de S3 que el archivo se subió

## Próximos Pasos Sugeridos

- [ ] Agregar compresión de imágenes antes de subir
- [ ] Implementar caché de imágenes en el móvil
- [ ] Agregar opción de tomar foto con la cámara
- [ ] Implementar eliminación de imágenes antiguas de S3
- [ ] Agregar múltiples imágenes por producto
- [ ] Implementar CDN (CloudFront) para mejor performance
