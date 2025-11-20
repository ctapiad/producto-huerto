# 🚀 Configuración Rápida S3 + Presigned URLs

## ⚡ Pasos Rápidos (AWS Academy)

### 1️⃣ Crear Bucket S3 (AWS Console)

```
AWS Console → S3 → Create bucket
```

- **Nombre**: `image-huerto`
- **Región**: `us-east-1`
- **Desmarcar** "Block all public access" ✓
- Click **Create bucket**

---

### 2️⃣ Configurar Bucket

#### A) CORS (para permitir uploads desde móvil)
```
Bucket → Permissions → CORS → Edit
```
Pegar el contenido de: `aws-policies/s3-cors-config.json`

#### B) Bucket Policy (para lectura pública)
```
Bucket → Permissions → Bucket policy → Edit
```
Pegar el contenido de: `aws-policies/s3-bucket-policy.json`

---

### 3️⃣ Configurar IAM (Permisos para EC2)

#### Opción A: Si usas LabRole (AWS Academy)
```
IAM Console → Roles → LabRole → Add permissions → Attach policies → Create policy
```
Pegar el contenido de: `aws-policies/s3-image-upload-policy.json`

#### Opción B: Si necesitas crear un nuevo Role
```
IAM Console → Roles → Create role → EC2 → Next
```
- Crear policy con: `aws-policies/s3-image-upload-policy.json`
- Asignar el role a tu instancia EC2

---

### 4️⃣ Configurar EC2 (Conectarse por SSH)

```bash
# Conectarse a EC2
ssh -i tu-key.pem ec2-user@34.202.46.121

# Ejecutar el script de configuración automática
cd /home/ec2-user/producto-huerto
chmod +x configure-s3-ec2.sh
./configure-s3-ec2.sh
```

**El script automáticamente:**
- ✅ Verifica IAM Role
- ✅ Actualiza `application.properties`
- ✅ Recompila el proyecto
- ✅ Reinicia el servicio
- ✅ Prueba el endpoint

---

### 5️⃣ Probar desde Windows

```powershell
cd "C:\Users\Hp\OneDrive\Escritorio\Duoc\micros\producto-huerto"
.\test-presigned-url.ps1
```

---

## 🔧 Configuración Manual (Si el script falla)

### Actualizar application.properties

```bash
cd /home/ec2-user/producto-huerto
nano src/main/resources/application.properties
```

Agregar/actualizar:
```properties
aws.s3.use-iam-role=true
aws.s3.region=us-east-1
aws.s3.bucket-name=image-huerto
aws.s3.folder=productos/imagenes
aws.s3.presigned-url-duration=15
```

### Recompilar y reiniciar

```bash
# Recompilar
./mvnw clean package -DskipTests

# Reiniciar servicio
sudo systemctl restart producto-service

# Ver logs
sudo journalctl -u producto-service -f
```

---

## 🧪 Prueba Manual con curl

```bash
# Solicitar presigned URL
curl -X POST http://34.202.46.121:8081/api/productos/upload-url \
  -H "Content-Type: application/json" \
  -d '{"fileName": "test.jpg", "contentType": "image/jpeg"}'

# Copiar la uploadUrl de la respuesta y usarla para subir:
curl -X PUT "<uploadUrl>" \
  -H "Content-Type: image/jpeg" \
  --data-binary "@imagen.jpg"
```

---

## ✅ Checklist Rápido

- [ ] Bucket S3 creado y configurado
- [ ] CORS configurado
- [ ] Bucket policy agregada
- [ ] IAM policy creada y asignada al role
- [ ] IAM role asignado a la instancia EC2
- [ ] application.properties actualizado
- [ ] Servicio recompilado y reiniciado
- [ ] Endpoint probado y funcionando
- [ ] App móvil puede subir imágenes

---

## 🆘 Troubleshooting Rápido

### Error: "Access Denied"
→ Verificar que el IAM Role tenga la política de S3

### Error: "NoSuchBucket"
→ Verificar nombre del bucket en `application.properties`

### Error: "CORS policy blocked"
→ Verificar configuración CORS del bucket

### Las imágenes no son públicas (403)
→ Verificar la bucket policy de lectura pública

---

## 📚 Archivos Útiles

- `CONFIGURACION_S3_AWS_ACADEMY.md` - Guía completa detallada
- `configure-s3-ec2.sh` - Script de configuración automática (Linux)
- `test-presigned-url.ps1` - Script de prueba (Windows)
- `aws-policies/` - Todas las políticas JSON necesarias

---

## 🌐 URLs del Proyecto

- **Microservicio**: http://34.202.46.121:8081
- **Swagger**: http://34.202.46.121:8081/swagger-ui/index.html
- **Endpoint**: POST http://34.202.46.121:8081/api/productos/upload-url
