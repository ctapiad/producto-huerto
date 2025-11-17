#!/bin/bash

# Script de verificación del estado del servicio en EC2
# Ejecutar en la EC2: ./check-service.sh

echo "=========================================="
echo "  Verificación del Servicio de Productos"
echo "=========================================="
echo ""

# 1. Verificar Java
echo "1️⃣  Verificando Java..."
if command -v java &> /dev/null; then
    java -version
    echo "✅ Java instalado"
else
    echo "❌ Java NO instalado"
    echo "   Instalar con: sudo apt-get install -y openjdk-17-jdk"
fi
echo ""

# 2. Verificar directorio de la aplicación
echo "2️⃣  Verificando directorio de aplicación..."
if [ -d "/home/ubuntu/producto-service" ]; then
    echo "✅ Directorio existe: /home/ubuntu/producto-service"
    ls -lh /home/ubuntu/producto-service/
else
    echo "❌ Directorio NO existe"
    echo "   Crear con: mkdir -p /home/ubuntu/producto-service"
fi
echo ""

# 3. Verificar archivo JAR
echo "3️⃣  Verificando archivo JAR..."
if ls /home/ubuntu/producto-service/*.jar 1> /dev/null 2>&1; then
    echo "✅ Archivo JAR encontrado:"
    ls -lh /home/ubuntu/producto-service/*.jar
else
    echo "⚠️  No se encontró archivo JAR"
    echo "   Se creará en el primer deployment"
fi
echo ""

# 4. Verificar servicio systemd
echo "4️⃣  Verificando servicio systemd..."
if [ -f "/etc/systemd/system/producto-service.service" ]; then
    echo "✅ Archivo de servicio existe"
    sudo systemctl status producto-service --no-pager -l
else
    echo "❌ Archivo de servicio NO existe"
    echo "   Copiar con: sudo cp producto-service.service /etc/systemd/system/"
fi
echo ""

# 5. Verificar puerto 8081
echo "5️⃣  Verificando puerto 8081..."
if sudo netstat -tlnp | grep :8081 > /dev/null; then
    echo "✅ Servicio escuchando en puerto 8081"
    sudo netstat -tlnp | grep :8081
else
    echo "⚠️  Puerto 8081 no está en uso"
    echo "   El servicio se iniciará después del deployment"
fi
echo ""

# 6. Verificar firewall
echo "6️⃣  Verificando configuración de firewall (UFW)..."
if command -v ufw &> /dev/null; then
    sudo ufw status | grep 8081
    if [ $? -eq 0 ]; then
        echo "✅ Puerto 8081 permitido en firewall"
    else
        echo "⚠️  Puerto 8081 NO está configurado en firewall"
        echo "   Configurar con: sudo ufw allow 8081/tcp"
    fi
else
    echo "ℹ️  UFW no instalado (firewall puede estar gestionado por Security Group)"
fi
echo ""

# 7. Verificar logs recientes
echo "7️⃣  Últimas 10 líneas de logs..."
if [ -f "/etc/systemd/system/producto-service.service" ]; then
    sudo journalctl -u producto-service -n 10 --no-pager
else
    echo "⚠️  No hay logs disponibles aún"
fi
echo ""

# 8. Test de conectividad
echo "8️⃣  Probando conectividad al servicio..."
if curl -s http://localhost:8081/api/productos/health > /dev/null; then
    echo "✅ Servicio respondiendo correctamente:"
    curl -s http://localhost:8081/api/productos/health
else
    echo "⚠️  Servicio no está respondiendo"
    echo "   Se iniciará después del deployment"
fi
echo ""

echo "=========================================="
echo "  Resumen"
echo "=========================================="
echo ""

# Resumen final
READY=true

if ! command -v java &> /dev/null; then
    echo "❌ Falta: Java 17"
    READY=false
fi

if [ ! -d "/home/ubuntu/producto-service" ]; then
    echo "❌ Falta: Directorio de aplicación"
    READY=false
fi

if [ ! -f "/etc/systemd/system/producto-service.service" ]; then
    echo "❌ Falta: Archivo de servicio systemd"
    READY=false
fi

if $READY; then
    echo "✅ EC2 está lista para recibir deployments"
    echo ""
    echo "Siguiente paso:"
    echo "  1. Configurar secrets en GitHub"
    echo "  2. Hacer push a main para activar el deployment"
else
    echo "⚠️  EC2 requiere configuración adicional"
    echo ""
    echo "Ejecuta: ./setup-ec2.sh"
fi

echo ""
echo "=========================================="
