# 🔄 RECONFIGURACIÓN COMPLETA - NUEVA EC2

**Nueva IP Elástica:** `34.202.46.121`  
**Par de claves:** `~/Downloads/huerto.pem`  
**MongoDB:** Sin cambios (misma URI)

---

## 📋 PASO 1: ACTUALIZAR GITHUB SECRETS

Ve a: https://github.com/ctapiad/producto-huerto/settings/secrets/actions

**Actualiza estos 3 secrets:**

### 1. AWS_HOST
```
Valor: 34.202.46.121
```

### 2. AWS_USER
```
Valor: ubuntu
```

### 3. SSH_PRIVATE_KEY
```
Obtener el contenido:
cat ~/Downloads/huerto.pem

Copiar TODO el contenido (incluyendo las líneas BEGIN y END)
```

**IMPORTANTE:** GitHub Secrets se SOBREESCRIBEN, no se editan.
Elimina el secret viejo y crea uno nuevo con el mismo nombre.

---

## 🖥️ PASO 2: CONFIGURAR LA NUEVA EC2

### 2.1 Copiar archivos de configuración

```bash
# Copiar scripts a la EC2
scp -i ~/Downloads/huerto.pem setup-ec2.sh ubuntu@34.202.46.121:/home/ubuntu/
scp -i ~/Downloads/huerto.pem producto-service.service ubuntu@34.202.46.121:/home/ubuntu/
```

### 2.2 Conectarse a la EC2

```bash
ssh -i ~/Downloads/huerto.pem ubuntu@34.202.46.121
```

### 2.3 Ejecutar setup dentro de la EC2

```bash
chmod +x setup-ec2.sh
./setup-ec2.sh
```

Esto instalará:
- ✅ Java 17
- ✅ Configuración de firewall (UFW)
- ✅ Directorio `/home/ubuntu/producto-service`
- ✅ Servicio systemd

### 2.4 Configurar MongoDB URI

**IMPORTANTE:** El servicio necesita la conexión a MongoDB.

Editar el archivo de servicio:
```bash
sudo nano /etc/systemd/system/producto-service.service
```

Buscar la línea que dice:
```ini
# Environment="MONGODB_URI=mongodb+srv://ctapiad_db_user:PASSWORD@huerto.bi4rvwk.mongodb.net/Huerto"
```

**Descomentar** (quitar el `#`) y poner tu password real:
```ini
Environment="MONGODB_URI=mongodb+srv://ctapiad_db_user:TU_PASSWORD_REAL@huerto.bi4rvwk.mongodb.net/Huerto"
```

Guardar: `Ctrl+O`, `Enter`, `Ctrl+X`

Recargar systemd:
```bash
sudo systemctl daemon-reload
```

### 2.5 Salir de la EC2

```bash
exit
```

---

## 🔐 PASO 3: CONFIGURAR AWS SECURITY GROUP

**CRÍTICO:** Debes abrir los puertos en el nuevo Security Group

En AWS Console → EC2 → Security Groups:

**Inbound Rules:**
```
Type            Port    Source          Description
SSH             22      0.0.0.0/0       SSH Access
HTTP            80      0.0.0.0/0       HTTP
HTTPS           443     0.0.0.0/0       HTTPS
Custom TCP      8081    0.0.0.0/0       Producto API
```

---

## 🗄️ PASO 4: CONFIGURAR MONGODB ATLAS

MongoDB Atlas → Network Access

**Agregar la nueva IP de EC2:**
```
IP Address: 34.202.46.121/32
Description: Nueva EC2 Producto Service
```

O permitir desde cualquier lugar (menos seguro):
```
IP Address: 0.0.0.0/0
Description: Allow from anywhere
```

---

## 🚀 PASO 5: PROBAR DEPLOYMENT

### Opción A: Deployment automático
```bash
git commit --allow-empty -m "Test deployment nueva EC2"
git push origin main
```

### Opción B: Monitorear en GitHub Actions
Ve a: https://github.com/ctapiad/producto-huerto/actions

---

## ✅ PASO 6: VERIFICACIÓN

### Conectarse a EC2 y verificar:
```bash
ssh -i ~/Downloads/huerto.pem ubuntu@34.202.46.121

# Ver estado del servicio
sudo systemctl status producto-service

# Ver logs en tiempo real
sudo journalctl -u producto-service -f
```

### Probar desde tu navegador:

**Health Check:**
```
http://34.202.46.121:8081/api/productos/health
```

**Swagger UI:**
```
http://34.202.46.121:8081/swagger-ui/index.html
```

**API Docs:**
```
http://34.202.46.121:8081/v3/api-docs
```

---

## 📝 RESUMEN DE CAMBIOS

| Item | Valor Anterior | Valor Nuevo |
|------|----------------|-------------|
| IP EC2 | 54.158.158.91 | **34.202.46.121** |
| Par de claves | huerto.pem (viejo) | **huerto.pem (nuevo)** |
| Security Group | eliminado | **nuevo (configurar)** |
| MongoDB URI | sin cambios | sin cambios |

---

## 🆘 TROUBLESHOOTING

### Si el deployment falla en GitHub Actions:

1. **Error: "Permission denied"**
   - Verifica que SSH_PRIVATE_KEY esté correctamente configurado
   - Asegúrate de copiar TODO el contenido de huerto.pem

2. **Error: "Connection refused"**
   - Verifica que el Security Group tenga puerto 22 abierto
   - Verifica la IP en AWS_HOST (34.202.46.121)

3. **Error: "Unit producto-service.service not found"**
   - No ejecutaste setup-ec2.sh
   - Vuelve al PASO 2

### Si el servicio no inicia:

```bash
# Ver logs detallados
sudo journalctl -u producto-service -n 50 --no-pager

# Errores comunes:
# - "Unable to access jarfile" → falta el JAR, ejecuta deployment
# - "Invalid connection string" → falta configurar MONGODB_URI
# - "Connection refused to MongoDB" → falta configurar Network Access en Atlas
```

---

## 📞 COMANDOS RÁPIDOS

```bash
# Conectarse a EC2
ssh -i ~/Downloads/huerto.pem ubuntu@34.202.46.121

# Ver estado
sudo systemctl status producto-service

# Reiniciar servicio
sudo systemctl restart producto-service

# Ver logs
sudo journalctl -u producto-service -f

# Ver archivos
ls -lh /home/ubuntu/producto-service/

# Probar desde EC2
curl http://localhost:8081/api/productos/health
```

---

✅ Una vez completados todos los pasos, tu sistema estará funcionando nuevamente.
