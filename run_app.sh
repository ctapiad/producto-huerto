#!/bin/bash

# Script para ejecutar el microservicio de productos con MongoDB

echo "🚀 Iniciando Microservicio de Productos - HuertoHogar"
echo "📊 Base de datos: MongoDB Atlas"
echo "🌐 Puerto: 8082"
echo ""

# Limpiar y compilar el proyecto
echo "🔨 Compilando el proyecto..."
./mvnw clean compile

if [ $? -ne 0 ]; then
    echo "❌ Error en la compilación"
    exit 1
fi

echo ""
echo "✅ Compilación exitosa"
echo "🏃 Ejecutando la aplicación..."
echo ""

# Ejecutar la aplicación
./mvnw spring-boot:run
