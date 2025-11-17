# ✅ Configuración Completada - Microservicio de Productos

## 🎉 Resumen de Cambios

### 1. Repositorio GitHub ✅
- **Repositorio anterior**: `https://github.com/ctapiad/usuario.git`
- **Repositorio nuevo**: `https://github.com/ctapiad/producto-huerto.git`
- **Estado**: Código subido y sincronizado

### 2. Puerto de Aplicación ✅
- **Puerto anterior**: 8082 (desarrollo)
- **Puerto nuevo**: 8081 (producción)
- **Archivo modificado**: `src/main/resources/application.properties`

### 3. CI/CD con GitHub Actions ✅
- **Workflow creado**: `.github/workflows/deploy.yml`
- **Trigger**: Push a rama `main`
- **Acciones**:
  1. Build del proyecto con Maven
  2. Copia del JAR a EC2
  3. Despliegue automático
  4. Reinicio del servicio

### 4. Configuración de EC2 ✅
- **IP Pública**: `54.158.158.91`
- **Sistema Operativo**: Ubuntu
- **Puerto**: 8081
- **Usuario**: ubuntu
- **Servicio**: producto-service (systemd)

## 📁 Archivos Creados

### Configuración de Deployment
1. **`.github/workflows/deploy.yml`** - Pipeline de CI/CD
2. **`producto-service.service`** - Archivo de servicio systemd
3. **`setup-ec2.sh`** - Script de configuración inicial de EC2
4. **`check-service.sh`** - Script de verificación del servicio

### Documentación
1. **`DEPLOYMENT.md`** - Guía completa de despliegue
2. **`GITHUB_SECRETS_SETUP.md`** - Configuración de secrets
3. **`MIGRATION_TO_MONGODB.md`** - Documentación de migración (ya existía)
4. **`README.md`** - Actualizado con información de deployment

## 🔄 Próximos Pasos

### Paso 1: Configurar EC2 (Primera vez solamente)

```bash
# 1. Conectarse a EC2
ssh -i tu-clave.pem ubuntu@54.158.158.91

# 2. Copiar scripts a EC2
scp -i tu-clave.pem setup-ec2.sh ubuntu@54.158.158.91:/home/ubuntu/
scp -i tu-clave.pem producto-service.service ubuntu@54.158.158.91:/home/ubuntu/

# 3. Ejecutar configuración
ssh -i tu-clave.pem ubuntu@54.158.158.91
chmod +x setup-ec2.sh
./setup-ec2.sh
```

### Paso 2: Configurar Secrets en GitHub

Ve a: `https://github.com/ctapiad/producto-huerto/settings/secrets/actions`

Crea estos 3 secrets:

| Secret | Valor |
|--------|-------|
| `EC2_HOST` | `54.158.158.91` |
| `EC2_USER` | `ubuntu` |
| `EC2_SSH_KEY` | Contenido de tu archivo `.pem` |

Ver guía completa en: **`GITHUB_SECRETS_SETUP.md`**

### Paso 3: Activar el Primer Deployment

```bash
# Hacer cualquier cambio o solo trigger el workflow
git commit --allow-empty -m "Trigger first deployment"
git push origin main
```

### Paso 4: Monitorear el Deployment

1. Ve a: `https://github.com/ctapiad/producto-huerto/actions`
2. Observa el workflow ejecutándose
3. Espera a que complete (±3-5 minutos)

### Paso 5: Verificar el Servicio

```bash
# Desde tu máquina local
curl http://54.158.158.91:8081/api/productos/health

# Desde la EC2
ssh -i tu-clave.pem ubuntu@54.158.158.91
./check-service.sh
```

## 🌐 URLs del Servicio en Producción

Una vez desplegado:

- **Health Check**: http://54.158.158.91:8081/api/productos/health
- **Swagger UI**: http://54.158.158.91:8081/swagger-ui/index.html
- **API Productos**: http://54.158.158.91:8081/api/productos
- **API por ID**: http://54.158.158.91:8081/api/productos/{id}

## 🔐 Configuración de Security Group (AWS)

Asegúrate de que tu EC2 tenga estos puertos abiertos:

| Puerto | Protocolo | Descripción |
|--------|-----------|-------------|
| 22 | TCP | SSH |
| 8081 | TCP | Aplicación |
| 80 | TCP | HTTP (opcional) |
| 443 | TCP | HTTPS (opcional) |

## 🛠️ Comandos Útiles

### En EC2

```bash
# Ver estado del servicio
sudo systemctl status producto-service

# Ver logs en tiempo real
sudo journalctl -u producto-service -f

# Reiniciar servicio
sudo systemctl restart producto-service

# Detener servicio
sudo systemctl stop producto-service

# Iniciar servicio
sudo systemctl start producto-service

# Ver últimas 100 líneas de logs
sudo journalctl -u producto-service -n 100 --no-pager

# Verificar estado completo
./check-service.sh
```

### Localmente

```bash
# Verificar repositorio
git remote -v

# Ver estado del deployment en GitHub
# https://github.com/ctapiad/producto-huerto/actions

# Probar endpoints
curl http://54.158.158.91:8081/api/productos/health
curl http://54.158.158.91:8081/api/productos
```

## 📊 Flujo de Deployment Automático

```
1. Desarrollas localmente
   ↓
2. git push origin main
   ↓
3. GitHub Actions se activa
   ↓
4. Build con Maven (Java 17)
   ↓
5. Copia JAR a EC2 vía SCP
   ↓
6. Ejecuta deployment vía SSH
   ↓
7. Reinicia servicio systemd
   ↓
8. Servicio disponible en puerto 8081
```

## ✅ Checklist de Verificación

- [x] Código subido a GitHub
- [x] Repositorio cambiado a producto-huerto
- [x] Puerto cambiado a 8081
- [x] Pipeline de CI/CD creado
- [x] Archivo de servicio systemd creado
- [x] Scripts de setup y verificación creados
- [x] Documentación completa
- [ ] Configurar EC2 (ejecutar setup-ec2.sh)
- [ ] Configurar secrets en GitHub
- [ ] Realizar primer deployment
- [ ] Verificar servicio funcionando

## 📚 Documentación Adicional

- **Deployment completo**: Ver `DEPLOYMENT.md`
- **Configuración de secrets**: Ver `GITHUB_SECRETS_SETUP.md`
- **Migración a MongoDB**: Ver `MIGRATION_TO_MONGODB.md`
- **README principal**: Ver `README.md`

## 🎯 Testing de Endpoints

Una vez desplegado, puedes probar:

```bash
# Health check
curl http://54.158.158.91:8081/api/productos/health

# Listar todos los productos
curl http://54.158.158.91:8081/api/productos

# Obtener un producto
curl http://54.158.158.91:8081/api/productos/FR001

# Buscar por nombre
curl "http://54.158.158.91:8081/api/productos/buscar?nombre=manzana"

# Filtrar por precio
curl "http://54.158.158.91:8081/api/productos/precio?precioMin=1000&precioMax=2000"

# Productos orgánicos
curl http://54.158.158.91:8081/api/productos/organicos
```

## 🚨 Troubleshooting

### Si el deployment falla:
1. Verifica los secrets en GitHub
2. Revisa los logs del workflow en Actions
3. Verifica conectividad SSH: `ssh -i tu-clave.pem ubuntu@54.158.158.91`
4. Revisa el Security Group de AWS

### Si el servicio no responde:
1. Conéctate a EC2: `ssh -i tu-clave.pem ubuntu@54.158.158.91`
2. Ejecuta: `./check-service.sh`
3. Revisa logs: `sudo journalctl -u producto-service -f`
4. Verifica el puerto: `sudo netstat -tlnp | grep 8081`

### Si hay errores en la aplicación:
1. Revisa los logs: `sudo journalctl -u producto-service -n 200 --no-pager`
2. Verifica la conexión a MongoDB (debe estar permitida desde la IP de EC2)
3. Confirma que el JAR se copió correctamente: `ls -lh /home/ubuntu/producto-service/`

## 📞 Contacto y Soporte

Para más información, consulta:
- GitHub Repository: https://github.com/ctapiad/producto-huerto
- MongoDB Atlas: https://cloud.mongodb.com/
- AWS EC2 Console: https://console.aws.amazon.com/ec2/

---

**¡Configuración completada con éxito! 🎉**

El microservicio está listo para recibir deployments automáticos cada vez que hagas push a la rama main.
