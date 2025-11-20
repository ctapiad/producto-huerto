#!/bin/bash

# Script para configurar S3 en EC2 de AWS Academy
# Ejecutar este script en la instancia EC2

set -e

echo "=========================================="
echo "  Configuración de S3 para Producto-Huerto"
echo "=========================================="
echo ""

# Variables - ACTUALIZA ESTOS VALORES
BUCKET_NAME="image-huerto"
REGION="us-east-1"
PROJECT_DIR="/home/ec2-user/producto-huerto"
SERVICE_NAME="producto-service"

echo "📋 Configuración:"
echo "  Bucket: $BUCKET_NAME"
echo "  Region: $REGION"
echo "  Proyecto: $PROJECT_DIR"
echo ""

# 1. Verificar IAM Role
echo "🔍 1. Verificando IAM Role de la instancia EC2..."
ROLE_NAME=$(curl -s http://169.254.169.254/latest/meta-data/iam/security-credentials/ || echo "")

if [ -z "$ROLE_NAME" ]; then
    echo "❌ ERROR: Esta instancia EC2 no tiene un IAM Role asignado"
    echo "   Por favor, asigna un IAM Role con permisos de S3 desde la consola de AWS"
    exit 1
else
    echo "✅ IAM Role encontrado: $ROLE_NAME"
fi
echo ""

# 2. Verificar que el bucket existe
echo "🔍 2. Verificando que el bucket S3 existe..."
if aws s3 ls "s3://$BUCKET_NAME" > /dev/null 2>&1; then
    echo "✅ Bucket '$BUCKET_NAME' encontrado"
else
    echo "❌ ERROR: Bucket '$BUCKET_NAME' no encontrado"
    echo "   Por favor, crea el bucket desde la consola de AWS"
    exit 1
fi
echo ""

# 3. Verificar permisos del IAM Role
echo "🔍 3. Probando permisos de escritura en S3..."
TEST_FILE="/tmp/test-s3-upload.txt"
echo "Test upload $(date)" > $TEST_FILE

if aws s3 cp $TEST_FILE "s3://$BUCKET_NAME/test/" > /dev/null 2>&1; then
    echo "✅ Permisos de escritura: OK"
    aws s3 rm "s3://$BUCKET_NAME/test/test-s3-upload.txt" > /dev/null 2>&1
else
    echo "⚠️  ADVERTENCIA: No se pueden escribir archivos en S3"
    echo "   Verifica que el IAM Role tenga la política correcta"
fi
echo ""

# 4. Actualizar application.properties
echo "📝 4. Actualizando application.properties..."
PROPS_FILE="$PROJECT_DIR/src/main/resources/application.properties"

if [ ! -f "$PROPS_FILE" ]; then
    echo "❌ ERROR: No se encuentra $PROPS_FILE"
    exit 1
fi

# Backup del archivo original
cp "$PROPS_FILE" "$PROPS_FILE.backup.$(date +%Y%m%d_%H%M%S)"

# Actualizar o agregar configuraciones
update_property() {
    local key=$1
    local value=$2
    local file=$3
    
    if grep -q "^${key}=" "$file"; then
        sed -i "s|^${key}=.*|${key}=${value}|" "$file"
        echo "  ✓ Actualizado: $key=$value"
    else
        echo "${key}=${value}" >> "$file"
        echo "  ✓ Agregado: $key=$value"
    fi
}

update_property "aws.s3.use-iam-role" "true" "$PROPS_FILE"
update_property "aws.s3.region" "$REGION" "$PROPS_FILE"
update_property "aws.s3.bucket-name" "$BUCKET_NAME" "$PROPS_FILE"
update_property "aws.s3.folder" "productos/imagenes" "$PROPS_FILE"
update_property "aws.s3.presigned-url-duration" "15" "$PROPS_FILE"

echo "✅ application.properties actualizado"
echo ""

# 5. Recompilar el proyecto
echo "🔨 5. Recompilando el proyecto..."
cd "$PROJECT_DIR"

if [ -f "./mvnw" ]; then
    ./mvnw clean package -DskipTests
else
    mvn clean package -DskipTests
fi

if [ $? -eq 0 ]; then
    echo "✅ Compilación exitosa"
else
    echo "❌ ERROR: Falló la compilación"
    exit 1
fi
echo ""

# 6. Reiniciar el servicio
echo "🔄 6. Reiniciando el servicio..."
if sudo systemctl is-active --quiet "$SERVICE_NAME"; then
    sudo systemctl restart "$SERVICE_NAME"
    echo "✅ Servicio reiniciado"
else
    echo "⚠️  El servicio '$SERVICE_NAME' no está activo"
    echo "   Iniciándolo..."
    sudo systemctl start "$SERVICE_NAME"
fi
echo ""

# 7. Verificar que el servicio está corriendo
echo "🔍 7. Verificando estado del servicio..."
sleep 3

if sudo systemctl is-active --quiet "$SERVICE_NAME"; then
    echo "✅ Servicio activo"
    
    # Mostrar últimas líneas del log
    echo ""
    echo "📋 Últimas líneas del log:"
    echo "---"
    sudo journalctl -u "$SERVICE_NAME" -n 20 --no-pager | grep -E "(S3|IAM|AWS|Started)"
else
    echo "❌ ERROR: El servicio no está activo"
    echo ""
    echo "📋 Ver logs con:"
    echo "   sudo journalctl -u $SERVICE_NAME -f"
    exit 1
fi
echo ""

# 8. Probar el endpoint
echo "🧪 8. Probando el endpoint de presigned URL..."
sleep 2

RESPONSE=$(curl -s -X POST http://localhost:8081/api/productos/upload-url \
  -H "Content-Type: application/json" \
  -d '{"fileName": "test.jpg", "contentType": "image/jpeg"}')

if echo "$RESPONSE" | grep -q "uploadUrl"; then
    echo "✅ Endpoint funcionando correctamente"
    echo ""
    echo "📋 Respuesta del servidor:"
    echo "$RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE"
else
    echo "❌ ERROR: El endpoint no responde correctamente"
    echo "📋 Respuesta:"
    echo "$RESPONSE"
fi
echo ""

# Resumen final
echo "=========================================="
echo "  ✅ CONFIGURACIÓN COMPLETADA"
echo "=========================================="
echo ""
echo "📋 Información del servicio:"
echo "  • URL base: http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4):8081"
echo "  • Swagger: http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4):8081/swagger-ui/index.html"
echo "  • Endpoint: POST /api/productos/upload-url"
echo ""
echo "📋 Bucket S3:"
echo "  • Nombre: $BUCKET_NAME"
echo "  • Región: $REGION"
echo "  • Carpeta: productos/imagenes"
echo "  • URL: https://s3.console.aws.amazon.com/s3/buckets/$BUCKET_NAME"
echo ""
echo "📋 Comandos útiles:"
echo "  • Ver logs: sudo journalctl -u $SERVICE_NAME -f"
echo "  • Estado: sudo systemctl status $SERVICE_NAME"
echo "  • Reiniciar: sudo systemctl restart $SERVICE_NAME"
echo "  • Listar bucket: aws s3 ls s3://$BUCKET_NAME/productos/imagenes/"
echo ""
echo "🎉 ¡Todo listo! Ahora puedes probar la subida de imágenes desde la app móvil"
