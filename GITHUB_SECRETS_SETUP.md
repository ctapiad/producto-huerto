# 🔐 Configuración de Secrets para GitHub Actions

Para que el CI/CD funcione, necesitas configurar los siguientes secrets en tu repositorio de GitHub.

## 📍 Ubicación
Ve a: `https://github.com/ctapiad/producto-huerto/settings/secrets/actions`

O sigue estos pasos:
1. Abre tu repositorio: `https://github.com/ctapiad/producto-huerto`
2. Clic en **Settings** (Configuración)
3. En el menú lateral, clic en **Secrets and variables** → **Actions**
4. Clic en **New repository secret**

## 🔑 Secrets Requeridos

### 1. EC2_HOST
- **Nombre**: `EC2_HOST`
- **Valor**: `54.158.158.91`
- **Descripción**: IP pública elástica de tu EC2

### 2. EC2_USER
- **Nombre**: `EC2_USER`
- **Valor**: `ubuntu`
- **Descripción**: Usuario SSH para conectarse a la EC2

### 3. EC2_SSH_KEY
- **Nombre**: `EC2_SSH_KEY`
- **Valor**: Contenido completo de tu archivo `.pem`
- **Descripción**: Clave privada SSH para autenticación

#### ⚠️ Cómo obtener EC2_SSH_KEY:

En tu terminal local (Mac/Linux):
```bash
cat /ruta/a/tu/clave.pem
```

O en Windows (PowerShell):
```powershell
Get-Content C:\ruta\a\tu\clave.pem
```

**Copia TODO el contenido**, incluyendo las líneas:
```
-----BEGIN RSA PRIVATE KEY-----
... (contenido de la clave)
-----END RSA PRIVATE KEY-----
```

## ✅ Verificación

Después de configurar los 3 secrets, deberías ver algo así:

```
EC2_HOST         Updated X minutes ago
EC2_USER         Updated X minutes ago  
EC2_SSH_KEY      Updated X minutes ago
```

## 🚀 Siguiente Paso

Una vez configurados los secrets:

1. **Configura tu EC2** ejecutando el script `setup-ec2.sh`
2. Haz cualquier cambio en el código
3. Haz commit y push:
   ```bash
   git add .
   git commit -m "Trigger CI/CD"
   git push origin main
   ```
4. Ve a la pestaña **Actions** en GitHub para ver el deployment en progreso

## 📊 Monitoreo del Deployment

1. Ve a: `https://github.com/ctapiad/producto-huerto/actions`
2. Verás el workflow "CI/CD Pipeline - Deploy to AWS EC2" ejecutándose
3. Haz clic en el workflow para ver los detalles y logs

## 🎯 Verificación Final

Una vez que el deployment termine exitosamente:

```bash
# Verificar que el servicio está corriendo
curl http://54.158.158.91:8081/api/productos/health

# Deberías recibir:
# Servicio de productos funcionando correctamente en puerto 8081
```

## ⚠️ Notas Importantes

- **NUNCA** compartas tus claves privadas SSH públicamente
- Los secrets en GitHub están encriptados y solo se exponen durante la ejecución del workflow
- Si cambias la clave SSH de tu EC2, debes actualizar el secret `EC2_SSH_KEY`
- Asegúrate de que el Security Group de tu EC2 permite:
  - Puerto 22 (SSH)
  - Puerto 8081 (Aplicación)

## 🔧 Troubleshooting

Si el deployment falla:

1. **Verifica los secrets**: Asegúrate de que están correctamente configurados
2. **Revisa los logs del workflow**: Ve a la pestaña Actions y revisa los errores
3. **Verifica la conectividad SSH**: 
   ```bash
   ssh -i tu-clave.pem ubuntu@54.158.158.91
   ```
4. **Revisa el Security Group**: Confirma que permite tráfico en los puertos necesarios
5. **Verifica logs en EC2**:
   ```bash
   ssh -i tu-clave.pem ubuntu@54.158.158.91
   sudo journalctl -u producto-service -f
   ```
