#!/bin/bash

# Script para configurar IAM Role para EC2 con acceso a S3
# Uso: ./setup-ec2-iam-role.sh <instance-id> <aws-account-id>

set -e

INSTANCE_ID=$1
AWS_ACCOUNT_ID=$2
ROLE_NAME="EC2-S3-ImageUpload-Role"
POLICY_NAME="S3-ImageUpload-Policy"

if [ -z "$INSTANCE_ID" ] || [ -z "$AWS_ACCOUNT_ID" ]; then
    echo "❌ Error: Debes proporcionar el instance-id y aws-account-id"
    echo "Uso: $0 <instance-id> <aws-account-id>"
    echo "Ejemplo: $0 i-1234567890abcdef0 123456789012"
    exit 1
fi

echo "🚀 Configurando IAM Role para EC2..."
echo "   Instance ID: $INSTANCE_ID"
echo "   AWS Account: $AWS_ACCOUNT_ID"
echo ""

# Paso 1: Crear la política de permisos S3
echo "📝 Paso 1: Creando política S3..."
POLICY_ARN=$(aws iam create-policy \
  --policy-name $POLICY_NAME \
  --policy-document file://aws-policies/s3-policy.json \
  --query 'Policy.Arn' \
  --output text 2>/dev/null || echo "arn:aws:iam::${AWS_ACCOUNT_ID}:policy/${POLICY_NAME}")

echo "   ✅ Política creada: $POLICY_ARN"

# Paso 2: Crear el role
echo "📝 Paso 2: Creando IAM Role..."
aws iam create-role \
  --role-name $ROLE_NAME \
  --assume-role-policy-document file://aws-policies/trust-policy.json \
  2>/dev/null || echo "   ⚠️  Role ya existe"

echo "   ✅ Role creado: $ROLE_NAME"

# Paso 3: Adjuntar la política al role
echo "📝 Paso 3: Adjuntando política al role..."
aws iam attach-role-policy \
  --role-name $ROLE_NAME \
  --policy-arn $POLICY_ARN \
  2>/dev/null || echo "   ⚠️  Política ya adjunta"

echo "   ✅ Política adjunta al role"

# Paso 4: Crear instance profile si no existe
echo "📝 Paso 4: Creando instance profile..."
aws iam create-instance-profile \
  --instance-profile-name $ROLE_NAME \
  2>/dev/null || echo "   ⚠️  Instance profile ya existe"

# Paso 5: Agregar role al instance profile
echo "📝 Paso 5: Agregando role al instance profile..."
aws iam add-role-to-instance-profile \
  --instance-profile-name $ROLE_NAME \
  --role-name $ROLE_NAME \
  2>/dev/null || echo "   ⚠️  Role ya agregado al profile"

echo "   ✅ Role agregado al instance profile"

# Paso 6: Asociar instance profile a EC2
echo "📝 Paso 6: Asociando instance profile a EC2..."
aws ec2 associate-iam-instance-profile \
  --instance-id $INSTANCE_ID \
  --iam-instance-profile Name=$ROLE_NAME \
  2>/dev/null || echo "   ⚠️  Instance profile ya asociado o instancia no encontrada"

echo "   ✅ Instance profile asociado a EC2"

echo ""
echo "✅ ¡Configuración completada!"
echo ""
echo "📋 Próximos pasos:"
echo "   1. Conéctate a tu instancia EC2: ssh ec2-user@<ip-publica>"
echo "   2. Verifica el role: curl http://169.254.169.254/latest/meta-data/iam/security-credentials/"
echo "   3. Configura la variable: export AWS_USE_IAM_ROLE=true"
echo "   4. Reinicia tu aplicación Spring Boot"
echo ""
