# 🔐 Configuración de GitHub Secrets

## Instrucciones

Ve a tu repositorio en GitHub:
**https://github.com/ctapiad/producto-huerto**

Luego navega a:
**Settings** → **Secrets and variables** → **Actions** → **New repository secret**

---

## Secrets a Configurar

### 1. EC2_HOST
**Nombre del secret:** `EC2_HOST`

**Valor:**
```
54.158.158.91
```

---

### 2. EC2_USER
**Nombre del secret:** `EC2_USER`

**Valor:**
```
ubuntu
```

---

### 3. EC2_SSH_KEY
**Nombre del secret:** `EC2_SSH_KEY`

**Valor:** (Copia TODO el contenido del archivo huerto.pem, incluyendo las líneas BEGIN y END)

```
-----BEGIN RSA PRIVATE KEY-----
MIIEowIBAAKCAQEAsuLGohlpCmxOOMIbkAz586RxLpmb3nUwQGWiRWwq2787c6Ts
za11/FMkbVR0STAkU1UeGYAogEvkAxtbPx3JeYUeo+4Y8tv++mAuBkQgcS233RTX
XreSD86loLkqlvbJWfmKoStpuVCSZNZARZp2k1CCxDgMADQjdpSGerr6h7x/6TrS
JcshSkctqQcrinmhuTIraT3iHw4GevHxyB0KqF3y84XCBcHGcYmzQs5YReb1Niat
1/dJlggPLcVlbKvBcui2IRSAImrqk2WUo0GWVklOLYZQwB4KrASkRmQ0hhC83LjM
R4w0tpkGmUxVccQeCEShCA3b846/GT+EgqlRswIDAQABAoIBAD8ycLw5v+tHwga5
RKWKgA98S+QLro5T1l0Zys893JJPqAA+Bs7O9jzTXq4lRQPKNzsdegnGRgi1RvN2
CbFto40D44REjJnX8OehbQEXtmJlpWw8fa3fqoFdHxR99jbpLs9Too832Bz5Aw8O
xhwQ6s02h0wtBMhZoyG0bSIUxoeHD9zhyTSmEdWXa/wBsnfZw2iFFNzO3FbL3MGp
f7YXTOVlDl2Kfkau+s8QguhnzvmelXy/uH7t07hwAEmg9vrPoSZ+tQoX1bOnEmPE
/G3/lPQu1cVR+CJIGiGWQoVSAOPhnRa+kvko8pFsg46n9fC5fKIxYiSEbem2ekbV
WpMl/RkCgYEA6Y3PRPvrcH2/XQYUObAkoqR9YnVQx83cxHb5z55QO0yyk2ODMW2a
0kAkctoQftd733bMXhL42TRU7zuzfhgDggdgsOYnIYyBMzi7WYV7CMlHYSiF0g4K
BdCnoVlAIEbfhh7i2uuM+d5ARYggpTQT2wShwSAntv7Wuha1hUvzeCUCgYEAxBPz
yxlzht//tZ6say9eMWAhffSJkS60Kk3ClLHr0x7sZsatQ1OvY+7t1ms13hqI3CYD
fYEwhP0eSoecigYuBn9yRACXrk+3HpU9e/nlWM0P9/bRnqiFvtkgIYwnvJEpkuhi
5ilj+N1VGwj4xqVMP5FoSqOYJPRRA4ZdAIB27vcCgYB4HjEdv5sFTgcfpsK8MLWG
2KaL/t4/BMeoW7B7cWa+OOIXH7UXtNFNlSOp1jFpqxXQhl7xezx1KY8NQ107Wspj
E7xM/vHdNVA1sdXZYuc2S57kf1zj/ch12Sx9tVNfxlOrvTLbeQ4WbJINsMdpb/FO
UYGHvCvw1/xD1O4i0j8z6QKBgAu+OiqewpmATts8vozI0HZakIbvn1GPn4CtGYs4
QKxrPLNHENdoaqeshpgB4b8Ejc/JgwoAM7yzQB4skwZ9KkJ/XYOPGYZ6BbiSLZWA
OOkhluQ95v3+j8wHSvJXZ4XWl8vRmSK5teAQuWkeF3AmNDPWbI2T0Kfd7gAtCuLd
zsGvAoGBAONIF/Q1xFzHMp1olgMAN5wsPVy6mfG5Q9qSxef4fK1xt7tVQ9ifdcSd
kWPtFQhVjUdzkTepXhcWWR4E3LM+qjVF31t5eB86RGE4Fu5c3mNoY+iotCxlrphS
FsRZv2g7JUyZ2Znc6wk3lvVQd6lUbmGL1GlCEUCoIpCT3htYtrE0
-----END RSA PRIVATE KEY-----
```

⚠️ **IMPORTANTE:** Asegúrate de copiar TODA la clave, incluyendo las líneas `-----BEGIN RSA PRIVATE KEY-----` y `-----END RSA PRIVATE KEY-----`

---

### 4. MONGODB_PASSWORD (Opcional - Recomendado)
**Nombre del secret:** `MONGODB_PASSWORD`

**Valor:**
```
MhRBXg6OTYK9AqQv
```

**Nota:** Actualmente la contraseña está en el código, pero es mejor práctica usar secrets.

---

## ✅ Verificación

Después de agregar los secrets, deberías ver en GitHub:
- ✅ EC2_HOST
- ✅ EC2_USER
- ✅ EC2_SSH_KEY
- ✅ MONGODB_PASSWORD (opcional)

---

## 🚀 Próximos Pasos

Una vez configurados los secrets:

1. **Configurar EC2** (primera vez):
   ```bash
   # Conectarse a EC2
   ssh -i ~/Downloads/huerto.pem ubuntu@54.158.158.91
   
   # Copiar setup-ec2.sh a EC2
   scp -i ~/Downloads/huerto.pem setup-ec2.sh ubuntu@54.158.158.91:/home/ubuntu/
   
   # Copiar producto-service.service a EC2
   scp -i ~/Downloads/huerto.pem producto-service.service ubuntu@54.158.158.91:/home/ubuntu/
   
   # Ejecutar setup
   ssh -i ~/Downloads/huerto.pem ubuntu@54.158.158.91
   chmod +x setup-ec2.sh
   ./setup-ec2.sh
   ```

2. **Configurar Security Group en AWS:**
   - Puerto 22 (SSH)
   - Puerto 8081 (Aplicación)
   - Puerto 80 (HTTP - opcional)
   - Puerto 443 (HTTPS - opcional)

3. **Configurar MongoDB Atlas:**
   - Agregar la IP de EC2 (54.158.158.91) a la lista blanca
   - O permitir acceso desde cualquier IP (0.0.0.0/0)

4. **Hacer push a GitHub:**
   ```bash
   git push origin main
   ```

5. **Verificar deployment:**
   - Ve a **Actions** en GitHub
   - Espera a que el workflow termine
   - Accede a: http://54.158.158.91:8081/api/productos/health

---

## 🔍 Debugging

Si algo falla:

**Ver logs en GitHub Actions:**
- Ve a la pestaña "Actions" en tu repositorio
- Haz clic en el workflow que falló
- Revisa los logs de cada step

**Ver logs en EC2:**
```bash
ssh -i ~/Downloads/huerto.pem ubuntu@54.158.158.91
sudo journalctl -u producto-service -f
```

**Ver estado del servicio:**
```bash
ssh -i ~/Downloads/huerto.pem ubuntu@54.158.158.91
sudo systemctl status producto-service
```

---

## 📝 Notas de Seguridad

⚠️ **NUNCA** compartas estos secrets públicamente
⚠️ Este archivo (GITHUB_SECRETS.md) está en .gitignore
⚠️ La clave SSH debe permanecer privada
⚠️ Considera rotar la contraseña de MongoDB periódicamente
