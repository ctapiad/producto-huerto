# ✅ RESUMEN - Tu microservicio YA tiene todo el código necesario

## 📦 Código Existente

Tu microservicio de productos **YA TIENE** implementado:

✅ **S3Config.java** - Configuración de AWS S3 con soporte para IAM Role
✅ **S3Service.java** - Servicio para generar presigned URLs
✅ **UploadUrlRequestDto.java** - DTO para las peticiones
✅ **ProductoController.java** - Endpoint POST `/api/productos/upload-url`

---

## 🎯 Lo que NECESITAS hacer ahora:

### 1. En AWS Console (Crear Bucket S3)

```
1. Ve a S3 → Create bucket
2. Nombre: image-huerto
3. Región: us-east-1
4. Desmarcar "Block all public access"
5. Create bucket

6. Configurar CORS:
   Bucket → Permissions → CORS
   Copiar: aws-policies/s3-cors-config.json

7. Configurar Bucket Policy:
   Bucket → Permissions → Bucket policy
   Copiar: aws-policies/s3-bucket-policy.json

8. Configurar IAM:
   IAM → Roles → LabRole (o tu role) → Add permissions
   Crear policy con: aws-policies/s3-image-upload-policy.json
```

### 2. En tu EC2 (Configurar el servicio)

```bash
# Conectarte a tu EC2
ssh -i tu-key.pem ec2-user@34.202.46.121

# Ir al directorio del proyecto
cd /home/ec2-user/producto-huerto

# Subir los archivos actualizados (desde tu Windows)
# O hacer git pull si usas Git

# Ejecutar el script de configuración
chmod +x configure-s3-ec2.sh
./configure-s3-ec2.sh
```

**O manualmente:**
```bash
# Editar application.properties
nano src/main/resources/application.properties

# Agregar/verificar:
aws.s3.use-iam-role=true
aws.s3.region=us-east-1
aws.s3.bucket-name=image-huerto
aws.s3.folder=productos/imagenes
aws.s3.presigned-url-duration=15

# Recompilar
./mvnw clean package -DskipTests

# Reiniciar
sudo systemctl restart producto-service

# Ver logs
sudo journalctl -u producto-service -f
```

### 3. Probar desde Windows

```powershell
cd "C:\Users\Hp\OneDrive\Escritorio\Duoc\micros\producto-huerto"
.\test-presigned-url.ps1
```

---

## 🔍 Verificaciones Rápidas

### ¿El servicio tiene IAM Role?
```bash
curl http://169.254.169.254/latest/meta-data/iam/security-credentials/
```
Debe mostrar: `LabRole` o el nombre de tu role

### ¿El bucket existe?
```bash
aws s3 ls s3://image-huerto/
```

### ¿El endpoint funciona?
```bash
curl -X POST http://34.202.46.121:8081/api/productos/upload-url \
  -H "Content-Type: application/json" \
  -d '{"fileName": "test.jpg", "contentType": "image/jpeg"}'
```

---

## 📋 Archivos que Necesitas Usar

### En AWS Console:
- `aws-policies/s3-cors-config.json` → CORS del bucket
- `aws-policies/s3-bucket-policy.json` → Bucket policy
- `aws-policies/s3-image-upload-policy.json` → IAM policy

### En EC2:
- `configure-s3-ec2.sh` → Script automático de configuración

### En Windows:
- `test-presigned-url.ps1` → Script para probar el endpoint

### Documentación:
- `README_S3_SETUP.md` → Guía rápida
- `CONFIGURACION_S3_AWS_ACADEMY.md` → Guía completa detallada

---

## 💡 Recordatorios Importantes

1. **AWS Academy** - Las instancias se reinician, necesitarás:
   - Volver a asignar el IAM Role si se pierde
   - Verificar que el bucket existe
   - Actualizar las IPs públicas si cambian

2. **IAM Role** - En AWS Academy usa `LabRole`:
   - No necesitas crear un nuevo role
   - Solo agrega la política de S3 al LabRole existente

3. **Bucket Name** - Si usas otro nombre:
   - Actualiza `application.properties`
   - Actualiza todas las políticas JSON
   - Actualiza el script de configuración

4. **App Móvil** - Ya está lista:
   - `ApiConfig.kt` ya tiene las URLs correctas
   - El código de ImageUploadService ya está implementado
   - Solo necesitas que el backend funcione

---

## 🚀 Flujo Completo de Trabajo

```
1. Crear bucket S3 en AWS Console (5 min)
2. Configurar CORS y políticas (2 min)
3. Configurar IAM Role (3 min)
4. Conectarte a EC2 y ejecutar script (2 min)
5. Probar con el script de Windows (1 min)
6. ¡Usar desde la app móvil!
```

**Tiempo total: ~15 minutos** ⏱️

---

## ❓ ¿Algo no funciona?

1. **Ver logs del servicio:**
   ```bash
   sudo journalctl -u producto-service -f
   ```

2. **Verificar configuración:**
   ```bash
   cat src/main/resources/application.properties | grep aws
   ```

3. **Probar permisos S3:**
   ```bash
   aws s3 ls s3://image-huerto/
   echo "test" > test.txt
   aws s3 cp test.txt s3://image-huerto/test/
   ```

4. **Ver el README completo:**
   Abre `CONFIGURACION_S3_AWS_ACADEMY.md` para troubleshooting detallado

---

## 🎯 Siguiente Paso

**Abre:** `README_S3_SETUP.md` y sigue los pasos rápidos 🚀
