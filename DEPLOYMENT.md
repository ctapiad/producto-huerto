# Despliegue Automático con GitHub Actions

Este proyecto está configurado con CI/CD usando GitHub Actions para despliegue automático en AWS EC2.

## 🚀 Configuración Inicial de EC2

### 1. Conectarse a la EC2
```bash
ssh -i tu-clave.pem ubuntu@54.158.158.91
```

### 2. Ejecutar el script de configuración inicial
```bash
# Copiar el archivo setup-ec2.sh a la EC2
scp -i tu-clave.pem setup-ec2.sh ubuntu@54.158.158.91:/home/ubuntu/

# Conectarse a la EC2 y ejecutar
ssh -i tu-clave.pem ubuntu@54.158.158.91
chmod +x setup-ec2.sh
./setup-ec2.sh
```

### 3. Copiar el archivo de servicio systemd
```bash
# En tu máquina local
scp -i tu-clave.pem producto-service.service ubuntu@54.158.158.91:/home/ubuntu/

# En la EC2
ssh -i tu-clave.pem ubuntu@54.158.158.91
sudo cp producto-service.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable producto-service
```

## 🔐 Configuración de Secrets en GitHub

Ve a tu repositorio en GitHub: `https://github.com/ctapiad/producto-huerto`

1. Ve a **Settings** → **Secrets and variables** → **Actions**
2. Crea los siguientes secrets:

| Secret Name | Value |
|-------------|-------|
| `EC2_HOST` | `54.158.158.91` |
| `EC2_USER` | `ubuntu` |
| `EC2_SSH_KEY` | Contenido completo de tu clave privada SSH (archivo .pem) |

### Cómo obtener EC2_SSH_KEY:
```bash
cat tu-clave.pem
```
Copia TODO el contenido (incluyendo `-----BEGIN RSA PRIVATE KEY-----` y `-----END RSA PRIVATE KEY-----`)

## 📦 Pipeline de CI/CD

El workflow de GitHub Actions se ejecuta automáticamente cuando haces push a la rama `main`.

### Pasos del Pipeline:
1. ✅ Checkout del código
2. ✅ Configuración de Java 17
3. ✅ Build con Maven
4. ✅ Copia del JAR a EC2
5. ✅ Despliegue y reinicio del servicio

## 🌐 Acceso al Servicio

Después del despliegue, el servicio estará disponible en:

- **URL Base**: `http://54.158.158.91:8081`
- **Health Check**: `http://54.158.158.91:8081/api/productos/health`
- **Swagger UI**: `http://54.158.158.91:8081/swagger-ui/index.html`
- **Todos los productos**: `http://54.158.158.91:8081/api/productos`

## 🔧 Comandos Útiles en EC2

### Ver logs del servicio
```bash
sudo journalctl -u producto-service -f
```

### Ver estado del servicio
```bash
sudo systemctl status producto-service
```

### Reiniciar servicio manualmente
```bash
sudo systemctl restart producto-service
```

### Detener servicio
```bash
sudo systemctl stop producto-service
```

### Ver logs recientes
```bash
sudo journalctl -u producto-service -n 100 --no-pager
```

## 📝 Primer Despliegue

1. **Configurar EC2**: Ejecutar `setup-ec2.sh` en la EC2
2. **Configurar Secrets**: Agregar los 3 secrets en GitHub
3. **Push a main**: Hacer commit y push para activar el pipeline
4. **Verificar**: Acceder a `http://54.158.158.91:8081/api/productos/health`

## 🔄 Despliegues Posteriores

Cada vez que hagas `git push origin main`, el pipeline se ejecutará automáticamente:
```bash
git add .
git commit -m "tu mensaje"
git push origin main
```

## ⚠️ Notas Importantes

- El servicio corre en el puerto **8081**
- Asegúrate de que el Security Group de EC2 permita tráfico en el puerto 8081
- La aplicación se reinicia automáticamente si falla
- Los logs se almacenan en systemd journal

## 🛡️ Configuración de Security Group en AWS

Asegúrate de que tu EC2 tenga estos puertos abiertos:
- **22** (SSH)
- **8081** (Aplicación)
- **80** (HTTP - opcional)
- **443** (HTTPS - opcional)

## 📊 Monitoreo

Para verificar que el deployment fue exitoso:

1. Ve a la pestaña **Actions** en GitHub
2. Verifica que el workflow se completó exitosamente
3. Accede a `http://54.158.158.91:8081/api/productos/health`
4. Revisa los logs en EC2: `sudo journalctl -u producto-service -f`
