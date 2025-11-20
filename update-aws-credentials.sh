#!/bin/bash

# Script para actualizar credenciales de AWS Academy en EC2
# Las credenciales de AWS Academy expiran cada ~4 horas

echo "=========================================="
echo "  Actualizar Credenciales AWS Academy"
echo "=========================================="
echo ""

# Solicitar credenciales
echo "Por favor ingresa las credenciales de AWS Academy:"
echo "(Ve a AWS Academy → AWS Details → Show)"
echo ""

read -p "AWS_ACCESS_KEY_ID: " ACCESS_KEY
read -p "AWS_SECRET_ACCESS_KEY: " SECRET_KEY
read -p "AWS_SESSION_TOKEN (opcional): " SESSION_TOKEN

echo ""
echo "📝 Actualizando variables de entorno..."

# Actualizar archivo de servicio
SERVICE_FILE="/etc/systemd/system/producto-service.service"

if [ -f "$SERVICE_FILE" ]; then
    sudo sed -i '/Environment="AWS_ACCESS_KEY_ID=/d' "$SERVICE_FILE"
    sudo sed -i '/Environment="AWS_SECRET_ACCESS_KEY=/d' "$SERVICE_FILE"
    sudo sed -i '/Environment="AWS_SESSION_TOKEN=/d' "$SERVICE_FILE"
    sudo sed -i '/Environment="AWS_USE_IAM_ROLE=/d' "$SERVICE_FILE"
    
    sudo sed -i '/\[Service\]/a Environment="AWS_USE_IAM_ROLE=false"' "$SERVICE_FILE"
    sudo sed -i '/\[Service\]/a Environment="AWS_ACCESS_KEY_ID='$ACCESS_KEY'"' "$SERVICE_FILE"
    sudo sed -i '/\[Service\]/a Environment="AWS_SECRET_ACCESS_KEY='$SECRET_KEY'"' "$SERVICE_FILE"
    
    if [ ! -z "$SESSION_TOKEN" ]; then
        sudo sed -i '/\[Service\]/a Environment="AWS_SESSION_TOKEN='$SESSION_TOKEN'"' "$SERVICE_FILE"
    fi
    
    echo "✅ Variables de entorno actualizadas"
else
    echo "⚠️  Archivo de servicio no encontrado: $SERVICE_FILE"
    echo "Configurando en ~/.bashrc"
    
    # Actualizar .bashrc
    grep -v "AWS_ACCESS_KEY_ID\|AWS_SECRET_ACCESS_KEY\|AWS_SESSION_TOKEN\|AWS_USE_IAM_ROLE" ~/.bashrc > ~/.bashrc.tmp
    mv ~/.bashrc.tmp ~/.bashrc
    
    echo "export AWS_USE_IAM_ROLE=false" >> ~/.bashrc
    echo "export AWS_ACCESS_KEY_ID=$ACCESS_KEY" >> ~/.bashrc
    echo "export AWS_SECRET_ACCESS_KEY=$SECRET_KEY" >> ~/.bashrc
    
    if [ ! -z "$SESSION_TOKEN" ]; then
        echo "export AWS_SESSION_TOKEN=$SESSION_TOKEN" >> ~/.bashrc
    fi
    
    source ~/.bashrc
    echo "✅ Credenciales agregadas a ~/.bashrc"
fi

echo ""
echo "🔄 Reiniciando servicio..."

if sudo systemctl is-active --quiet producto-service; then
    sudo systemctl daemon-reload
    sudo systemctl restart producto-service
    echo "✅ Servicio reiniciado"
else
    echo "⚠️  El servicio no está activo"
fi

echo ""
echo "🧪 Probando conexión a S3..."
aws s3 ls s3://image-huerto/ 2>&1

if [ $? -eq 0 ]; then
    echo "✅ Conexión a S3 exitosa"
else
    echo "❌ Error al conectar con S3"
    echo "Verifica que las credenciales sean correctas"
fi

echo ""
echo "=========================================="
echo "  ✅ ACTUALIZACIÓN COMPLETADA"
echo "=========================================="
echo ""
echo "⚠️  IMPORTANTE: Las credenciales de AWS Academy expiran"
echo "   Necesitarás ejecutar este script nuevamente cuando expiren"
echo ""
echo "📋 Ver logs: sudo journalctl -u producto-service -f"
