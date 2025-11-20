# Script de PowerShell para probar el endpoint de S3 Presigned URLs
# Ejecutar desde Windows

$PRODUCTO_SERVICE_URL = "http://34.202.46.121:8081"

Write-Host "=========================================="
Write-Host "  Test de Presigned URLs - Producto Service"
Write-Host "=========================================="
Write-Host ""

# 1. Probar que el servicio está activo
Write-Host "🔍 1. Verificando que el servicio está activo..."
try {
    $response = Invoke-WebRequest -Uri "$PRODUCTO_SERVICE_URL/actuator/health" -Method GET -TimeoutSec 5 -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Servicio activo" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠️  Endpoint /actuator/health no disponible, intentando con raíz..." -ForegroundColor Yellow
    try {
        $response = Invoke-WebRequest -Uri $PRODUCTO_SERVICE_URL -Method GET -TimeoutSec 5 -ErrorAction Stop
        Write-Host "✅ Servicio responde" -ForegroundColor Green
    } catch {
        Write-Host "❌ ERROR: El servicio no responde" -ForegroundColor Red
        Write-Host "   Verifica que el microservicio esté corriendo en EC2" -ForegroundColor Yellow
        exit 1
    }
}
Write-Host ""

# 2. Solicitar Presigned URL
Write-Host "🔗 2. Solicitando Presigned URL para subir imagen..."
$requestBody = @{
    fileName = "test-$(Get-Date -Format 'yyyyMMdd-HHmmss').jpg"
    contentType = "image/jpeg"
} | ConvertTo-Json

Write-Host "📤 Request Body:" -ForegroundColor Cyan
Write-Host $requestBody
Write-Host ""

try {
    $response = Invoke-RestMethod -Uri "$PRODUCTO_SERVICE_URL/api/productos/upload-url" `
        -Method POST `
        -Headers @{"Content-Type"="application/json"} `
        -Body $requestBody `
        -TimeoutSec 10

    Write-Host "✅ Presigned URL generada exitosamente" -ForegroundColor Green
    Write-Host ""
    Write-Host "📋 Respuesta del servidor:" -ForegroundColor Cyan
    Write-Host "---"
    Write-Host "Upload URL:" -ForegroundColor Yellow
    Write-Host $response.uploadUrl
    Write-Host ""
    Write-Host "Image URL (pública):" -ForegroundColor Yellow
    Write-Host $response.imageUrl
    Write-Host ""
    Write-Host "Key:" -ForegroundColor Yellow
    Write-Host $response.key
    Write-Host ""
    Write-Host "Expira en:" -ForegroundColor Yellow
    Write-Host "$($response.expiresIn) segundos ($([Math]::Round($response.expiresIn/60)) minutos)"
    Write-Host ""

    # Guardar URLs para prueba de subida
    $global:uploadUrl = $response.uploadUrl
    $global:imageUrl = $response.imageUrl

    # 3. Probar subida de imagen (si existe una imagen de prueba)
    $testImagePath = ".\test-image.jpg"
    
    if (Test-Path $testImagePath) {
        Write-Host "🖼️  3. Imagen de prueba encontrada, intentando subir..." -ForegroundColor Cyan
        
        try {
            $imageBytes = [System.IO.File]::ReadAllBytes($testImagePath)
            $response = Invoke-RestMethod -Uri $global:uploadUrl `
                -Method PUT `
                -Headers @{"Content-Type"="image/jpeg"} `
                -Body $imageBytes `
                -TimeoutSec 30

            Write-Host "✅ Imagen subida exitosamente a S3" -ForegroundColor Green
            Write-Host ""
            Write-Host "🌐 URL pública de la imagen:" -ForegroundColor Cyan
            Write-Host $global:imageUrl
            Write-Host ""
            Write-Host "💡 Puedes abrir esta URL en tu navegador para ver la imagen" -ForegroundColor Yellow
            
        } catch {
            Write-Host "❌ ERROR al subir la imagen:" -ForegroundColor Red
            Write-Host $_.Exception.Message -ForegroundColor Red
        }
    } else {
        Write-Host "ℹ️  3. No se encontró imagen de prueba ($testImagePath)" -ForegroundColor Yellow
        Write-Host "   Para probar la subida, crea un archivo 'test-image.jpg' en este directorio" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "💡 Puedes probar la subida manualmente con curl:" -ForegroundColor Cyan
        Write-Host "---"
        Write-Host "curl -X PUT `"$global:uploadUrl`" \"
        Write-Host "  -H `"Content-Type: image/jpeg`" \"
        Write-Host "  --data-binary `"@tu-imagen.jpg`""
    }

} catch {
    Write-Host "❌ ERROR al solicitar Presigned URL:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
    
    if ($_.Exception.Message -like "*404*") {
        Write-Host "💡 El endpoint no existe. Verifica:" -ForegroundColor Yellow
        Write-Host "   1. Que el microservicio esté actualizado con el código de S3" -ForegroundColor Yellow
        Write-Host "   2. Que la ruta sea: POST /api/productos/upload-url" -ForegroundColor Yellow
    } elseif ($_.Exception.Message -like "*500*") {
        Write-Host "💡 Error interno del servidor. Verifica:" -ForegroundColor Yellow
        Write-Host "   1. Los logs del servicio: sudo journalctl -u producto-service -f" -ForegroundColor Yellow
        Write-Host "   2. Que el bucket S3 exista y esté configurado" -ForegroundColor Yellow
        Write-Host "   3. Que el IAM Role tenga permisos de S3" -ForegroundColor Yellow
    }
    
    exit 1
}

Write-Host ""
Write-Host "=========================================="
Write-Host "  ✅ TEST COMPLETADO"
Write-Host "=========================================="
Write-Host ""
Write-Host "📋 Próximos pasos:" -ForegroundColor Cyan
Write-Host "  1. Verifica que la imagen sea accesible en la URL pública"
Write-Host "  2. Prueba la funcionalidad desde la app móvil"
Write-Host "  3. Monitorea los logs del servicio si hay problemas"
Write-Host ""
