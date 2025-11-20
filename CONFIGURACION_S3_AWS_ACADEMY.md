# Configuración S3 para AWS Academy - Presigned URLs

## 🎯 Objetivo
Configurar un bucket S3 nuevo en AWS Academy y conectarlo con el microservicio de productos en EC2 para generar URLs pre-firmadas para subida de imágenes.

---

## 📋 Paso 1: Crear el Bucket S3

### 1.1 Accede a la Consola S3
```
AWS Console → Servicios → S3 → Create bucket
```

### 1.2 Configuración del Bucket
- **Bucket name**: `image-huerto` (o el nombre que prefieras)
- **AWS Region**: `us-east-1` (mantén la misma región del EC2)
- **Object Ownership**: ACLs disabled (recommended)
- **Block Public Access settings**: 
  - ✅ Desmarcar "Block all public access" (para que las imágenes sean públicas)
  - ⚠️ Confirmar que entiendes los riesgos
- **Bucket Versioning**: Disabled
- **Default encryption**: Amazon S3 managed keys (SSE-S3)

### 1.3 Crear Bucket
Click en **"Create bucket"**

---

## 📋 Paso 2: Configurar CORS del Bucket

### 2.1 Acceder a CORS
```
S3 Console → Selecciona tu bucket (image-huerto) → Permissions → CORS
```

### 2.2 Agregar Configuración CORS
Click en **Edit** y pega esta configuración:

```json
[
  {
    "AllowedHeaders": ["*"],
    "AllowedMethods": ["PUT", "GET", "POST"],
    "AllowedOrigins": ["*"],
    "ExposeHeaders": ["ETag", "x-amz-server-side-encryption", "x-amz-request-id"]
  }
]
```

**Guardar cambios**

---

## 📋 Paso 3: Configurar Política del Bucket (Lectura Pública)

### 3.1 Acceder a Bucket Policy
```
S3 Console → Selecciona tu bucket → Permissions → Bucket policy
```

### 3.2 Agregar Política
Click en **Edit** y pega esta política:

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

⚠️ **Importante**: Reemplaza `image-huerto` con el nombre de tu bucket si es diferente.

**Guardar cambios**

---

## 📋 Paso 4: Configurar IAM Role para EC2

### 4.1 Crear Política IAM para S3

#### Opción A: Usando la Consola Web

```
IAM Console → Policies → Create policy
```

Selecciona la pestaña **JSON** y pega:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:DeleteObject",
        "s3:ListBucket"
      ],
      "Resource": [
        "arn:aws:s3:::image-huerto",
        "arn:aws:s3:::image-huerto/*"
      ]
    }
  ]
}
```

- **Policy name**: `EC2-S3-ImageUpload-Policy`
- Click en **Create policy**

#### Opción B: Usando AWS CLI (desde tu EC2)

Crea un archivo `s3-policy.json`:

```bash
cat > /tmp/s3-policy.json << 'EOF'
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:DeleteObject",
        "s3:ListBucket"
      ],
      "Resource": [
        "arn:aws:s3:::image-huerto",
        "arn:aws:s3:::image-huerto/*"
      ]
    }
  ]
}
EOF
```

---

### 4.2 Crear o Actualizar el IAM Role

#### Si tu EC2 NO tiene un Role asignado:

**Usando la Consola:**
```
IAM Console → Roles → Create role
→ AWS service → EC2 → Next
→ Buscar y seleccionar "EC2-S3-ImageUpload-Policy"
→ Role name: "EC2-ProductoHuerto-Role"
→ Create role
```

**Asignar el Role a la instancia EC2:**
```
EC2 Console → Selecciona tu instancia
→ Actions → Security → Modify IAM role
→ Selecciona "EC2-ProductoHuerto-Role"
→ Update IAM role
```

#### Si tu EC2 YA tiene un Role asignado (LabRole en AWS Academy):

**Agregar la política al Role existente:**

1. Identifica el role actual de tu EC2:
```bash
# Desde tu instancia EC2
curl http://169.254.169.254/latest/meta-data/iam/security-credentials/
```

2. En la consola IAM:
```
IAM Console → Roles → Buscar el role (ej: LabRole)
→ Permissions → Add permissions → Attach policies
→ Seleccionar "EC2-S3-ImageUpload-Policy"
→ Add permissions
```

---

## 📋 Paso 5: Configurar el Microservicio en EC2

### 5.1 Conectarse a la Instancia EC2

```bash
ssh -i tu-key.pem ec2-user@34.202.46.121
```

O desde AWS Console:
```
EC2 Console → Instancia → Connect → EC2 Instance Connect
```

---

### 5.2 Actualizar application.properties

```bash
cd /home/ec2-user/producto-huerto
nano src/main/resources/application.properties
```

Verifica o actualiza estas líneas:

```properties
# Configuración de AWS S3
aws.s3.use-iam-role=true
aws.s3.region=us-east-1
aws.s3.bucket-name=image-huerto
aws.s3.folder=productos/imagenes
aws.s3.presigned-url-duration=15
```

⚠️ **Importante**: 
- `aws.s3.use-iam-role=true` para usar el IAM Role de EC2
- NO necesitas configurar `aws.s3.access-key-id` ni `aws.s3.secret-access-key`
- Reemplaza `image-huerto` con el nombre de tu bucket

---

### 5.3 Crear Variables de Entorno (Opcional pero Recomendado)

```bash
# Editar el archivo de servicio
sudo nano /etc/systemd/system/producto-service.service
```

Agrega en la sección `[Service]`:

```ini
[Service]
Environment="AWS_USE_IAM_ROLE=true"
Environment="AWS_REGION=us-east-1"
Environment="S3_BUCKET_NAME=image-huerto"
```

O editar el archivo de entorno:

```bash
sudo nano /etc/environment
```

Agregar:

```bash
AWS_USE_IAM_ROLE=true
AWS_REGION=us-east-1
S3_BUCKET_NAME=image-huerto
```

---

### 5.4 Verificar la Configuración del IAM Role

```bash
# Verificar que el EC2 tiene un role asignado
curl http://169.254.169.254/latest/meta-data/iam/security-credentials/

# Debería mostrar algo como: LabRole o EC2-ProductoHuerto-Role
```

```bash
# Ver las credenciales temporales (opcional)
curl http://169.254.169.254/latest/meta-data/iam/security-credentials/LabRole
```

---

### 5.5 Recompilar y Reiniciar el Servicio

```bash
cd /home/ec2-user/producto-huerto

# Recompilar
./mvnw clean package -DskipTests

# Reiniciar el servicio
sudo systemctl restart producto-service

# Ver logs
sudo journalctl -u producto-service -f
```

Deberías ver en los logs:
```
✅ Usando IAM Role de EC2 para S3Client
✅ Usando IAM Role de EC2 para S3Presigner
```

---

## 📋 Paso 6: Probar la Configuración

### 6.1 Probar el Endpoint de Presigned URL

```bash
curl -X POST http://34.202.46.121:8081/api/productos/upload-url \
  -H "Content-Type: application/json" \
  -d '{
    "fileName": "test.jpg",
    "contentType": "image/jpeg"
  }'
```

**Respuesta esperada:**
```json
{
  "uploadUrl": "https://image-huerto.s3.us-east-1.amazonaws.com/productos/imagenes/uuid.jpg?X-Amz-...",
  "imageUrl": "https://image-huerto.s3.us-east-1.amazonaws.com/productos/imagenes/uuid.jpg",
  "key": "productos/imagenes/uuid.jpg",
  "expiresIn": "900"
}
```

---

### 6.2 Probar la Subida a S3

Copia la `uploadUrl` de la respuesta anterior y úsala para subir una imagen:

```bash
curl -X PUT "<uploadUrl>" \
  -H "Content-Type: image/jpeg" \
  --data-binary "@imagen-test.jpg"
```

**Respuesta esperada:**
- Status 200 OK (sin contenido)

---

### 6.3 Verificar la Imagen en S3

```
S3 Console → Bucket (image-huerto) → productos/imagenes/
```

Deberías ver tu imagen subida con un nombre UUID.

---

### 6.4 Probar la URL Pública

Copia la `imageUrl` de la respuesta del paso 6.1 y ábrela en un navegador:

```
https://image-huerto.s3.us-east-1.amazonaws.com/productos/imagenes/uuid.jpg
```

Deberías ver la imagen.

---

## 🔧 Troubleshooting

### Error: "Access Denied" al generar presigned URL

**Causa**: El IAM Role no tiene permisos suficientes.

**Solución**:
```bash
# 1. Verificar que el role está asignado
curl http://169.254.169.254/latest/meta-data/iam/security-credentials/

# 2. Verificar permisos del role en IAM Console
IAM → Roles → [Tu Role] → Permissions

# 3. Asegúrate de que la política incluye:
# - s3:PutObject
# - s3:GetObject
# - Resource: arn:aws:s3:::image-huerto/*
```

---

### Error: "NoSuchBucket" o "Bucket does not exist"

**Causa**: El nombre del bucket en `application.properties` no coincide con el bucket real.

**Solución**:
```bash
# Verificar el nombre exacto del bucket
aws s3 ls

# Actualizar application.properties
aws.s3.bucket-name=nombre-correcto-del-bucket
```

---

### Error: "CORS policy blocked" en la app móvil

**Causa**: Configuración CORS incorrecta o faltante.

**Solución**:
```
S3 Console → Bucket → Permissions → CORS
Verificar que la configuración CORS incluye:
- AllowedMethods: PUT, GET
- AllowedOrigins: *
```

---

### Las imágenes no son públicas (403 Forbidden)

**Causa**: Falta la política de lectura pública.

**Solución**:
```
S3 Console → Bucket → Permissions → Bucket policy
Agregar la política del Paso 3.2
```

---

### El servicio no encuentra las credenciales

**Logs**:
```
Unable to load credentials from service endpoint
```

**Solución**:
```bash
# 1. Verificar que el IAM Role está asignado a la instancia EC2
# 2. Reiniciar el servicio
sudo systemctl restart producto-service

# 3. Si el problema persiste, verificar la configuración
grep -r "aws.s3" src/main/resources/application.properties
```

---

## 📝 Actualizar la App Móvil

### Verificar que ApiConfig.kt tenga la URL correcta

```kotlin
object ApiConfig {
    const val PRODUCT_SERVICE_BASE_URL = "http://34.202.46.121:8081/"
}
```

### Compilar y probar la app

```bash
cd "C:\Users\Hp\OneDrive\Escritorio\Duoc\Desarrollo mobile\api-version\Huerto-Mobile"
.\gradlew.bat assembleDebug
```

---

## ✅ Checklist de Configuración

- [ ] Bucket S3 creado (`image-huerto`)
- [ ] CORS configurado en el bucket
- [ ] Política de lectura pública agregada
- [ ] Política IAM creada (`EC2-S3-ImageUpload-Policy`)
- [ ] IAM Role asignado a la instancia EC2
- [ ] `application.properties` actualizado con `aws.s3.use-iam-role=true`
- [ ] Microservicio recompilado y reiniciado
- [ ] Endpoint `/api/productos/upload-url` probado y funcionando
- [ ] Subida de imagen a S3 probada exitosamente
- [ ] URL pública de imagen verificada en navegador
- [ ] App móvil actualizada con la IP correcta

---

## 🎯 URLs Importantes

| Servicio | URL |
|----------|-----|
| **Microservicio Producto** | `http://34.202.46.121:8081` |
| **Swagger Productos** | `http://34.202.46.121:8081/swagger-ui/index.html` |
| **Endpoint Presigned URL** | `http://34.202.46.121:8081/api/productos/upload-url` |
| **Bucket S3** | `https://s3.console.aws.amazon.com/s3/buckets/image-huerto` |
| **Imágenes Públicas** | `https://image-huerto.s3.us-east-1.amazonaws.com/productos/imagenes/` |

---

## 📚 Documentación Adicional

- [AWS S3 Presigned URLs](https://docs.aws.amazon.com/AmazonS3/latest/userguide/PresignedUrlUploadObject.html)
- [IAM Roles for EC2](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/iam-roles-for-amazon-ec2.html)
- [S3 CORS Configuration](https://docs.aws.amazon.com/AmazonS3/latest/userguide/cors.html)
