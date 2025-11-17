# Configuración de Secrets - GUÍA

## ⚠️ IMPORTANTE - NO SUBIR CREDENCIALES AL REPOSITORIO

Este archivo explica cómo configurar las credenciales de manera segura.

---

## 📋 SECRETS DE GITHUB

Ve a: https://github.com/ctapiad/producto-huerto/settings/secrets/actions

### Secrets Requeridos:

1. **EC2_HOST**
   - Valor: `54.158.158.91`

2. **EC2_USER**
   - Valor: `ubuntu`

3. **EC2_SSH_KEY**
   - Valor: Contenido completo del archivo `huerto.pem`
   - Para obtenerlo: `cat ~/Downloads/huerto.pem`
   - Incluye TODO desde `-----BEGIN RSA PRIVATE KEY-----` hasta `-----END RSA PRIVATE KEY-----`

4. **MONGODB_PASSWORD** (Recomendado)
   - Valor: La contraseña de tu usuario de MongoDB Atlas
   - Se usará en la variable de entorno `MONGODB_URI`

---

## 🔐 Variables de Entorno en EC2

Después de configurar la EC2, agrega estas variables al servicio:

Edita el archivo `/etc/systemd/system/producto-service.service` y agrega:

```ini
Environment="MONGODB_URI=mongodb+srv://ctapiad_db_user:<PASSWORD>@huerto.bi4rvwk.mongodb.net/Huerto"
Environment="SPRING_PROFILES_ACTIVE=prod"
```

Reemplaza `<PASSWORD>` con la contraseña real.

---

## 📝 Archivo .env Local (NO SUBIR A GIT)

Crea un archivo `.env` en la raíz del proyecto (ya está en .gitignore):

```bash
MONGODB_URI=mongodb+srv://ctapiad_db_user:<PASSWORD>@huerto.bi4rvwk.mongodb.net/Huerto
MONGODB_PASSWORD=<PASSWORD>
```

---

## ✅ Checklist de Seguridad

- [ ] Secrets configurados en GitHub
- [ ] Variables de entorno configuradas en EC2
- [ ] Archivo `.env` en .gitignore
- [ ] NO hay credenciales en archivos de código
- [ ] NO hay claves privadas en el repositorio
- [ ] MongoDB Atlas tiene Network Access configurado

---

## 🚀 Pasos de Configuración

### 1. Configurar Secrets en GitHub
Ver sección "SECRETS DE GITHUB" arriba

### 2. Configurar EC2
```bash
# Copiar archivos a EC2
scp -i ~/Downloads/huerto.pem setup-ec2.sh ubuntu@54.158.158.91:/home/ubuntu/
scp -i ~/Downloads/huerto.pem producto-service.service ubuntu@54.158.158.91:/home/ubuntu/

# Conectarse y ejecutar
ssh -i ~/Downloads/huerto.pem ubuntu@54.158.158.91
chmod +x setup-ec2.sh
./setup-ec2.sh
```

### 3. Configurar Variables de Entorno en EC2
```bash
# Editar el servicio
sudo nano /etc/systemd/system/producto-service.service

# Agregar la línea con la URI completa:
# Environment="MONGODB_URI=mongodb+srv://ctapiad_db_user:TU_PASSWORD@huerto.bi4rvwk.mongodb.net/Huerto"

# Recargar y reiniciar
sudo systemctl daemon-reload
sudo systemctl restart producto-service
```

### 4. Configurar MongoDB Atlas
- Network Access → Add IP Address → `54.158.158.91/32`
- O permitir desde cualquier IP: `0.0.0.0/0`

### 5. Configurar Security Group AWS
- Puerto 22 (SSH)
- Puerto 8081 (Aplicación)

---

## 🔍 Verificación

```bash
# Probar health check
curl http://54.158.158.91:8081/api/productos/health

# Ver logs
ssh -i ~/Downloads/huerto.pem ubuntu@54.158.158.91
sudo journalctl -u producto-service -f
```

---

## ⚠️ NUNCA HACER

❌ Subir archivos .pem al repositorio
❌ Incluir contraseñas en el código
❌ Compartir secrets públicamente
❌ Commitear archivos .env
❌ Dejar credenciales en archivos de documentación

---

## 📚 Más Información

- DEPLOYMENT.md - Guía de deployment
- README.md - Documentación del proyecto
- .gitignore - Archivos excluidos del repositorio
