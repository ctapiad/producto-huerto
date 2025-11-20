package com.fullstack.producto.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class S3Service {

    @Autowired
    private S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.folder:productos/imagenes}")
    private String folder;

    @Value("${aws.s3.presigned-url-duration:15}")
    private int presignedUrlDurationMinutes;

    /**
     * Genera una URL firmada para subir una imagen directamente a S3
     * @param fileName Nombre original del archivo
     * @param contentType Tipo de contenido (ej: image/jpeg, image/png)
     * @return Map con la URL firmada y el key del objeto en S3
     */
    public Map<String, String> generatePresignedUploadUrl(String fileName, String contentType) {
        try {
            // Generar un nombre único para el archivo
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            String key = folder + "/" + uniqueFileName;

            // Crear la solicitud de PutObject
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            // Crear la solicitud de URL prefirmada
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(presignedUrlDurationMinutes))
                    .putObjectRequest(putObjectRequest)
                    .build();

            // Generar la URL prefirmada
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            String presignedUrl = presignedRequest.url().toString();

            // Construir la URL pública del archivo (sin los query params de la firma)
            String publicUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", 
                    bucketName, 
                    "us-east-1", 
                    key);

            Map<String, String> response = new HashMap<>();
            response.put("uploadUrl", presignedUrl);
            response.put("imageUrl", publicUrl);
            response.put("key", key);
            response.put("expiresIn", String.valueOf(presignedUrlDurationMinutes * 60)); // en segundos

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Error al generar URL prefirmada: " + e.getMessage(), e);
        }
    }

    /**
     * Valida que el tipo de contenido sea una imagen válida
     */
    public boolean isValidImageContentType(String contentType) {
        return contentType != null && (
            contentType.equals("image/jpeg") ||
            contentType.equals("image/jpg") ||
            contentType.equals("image/png") ||
            contentType.equals("image/gif") ||
            contentType.equals("image/webp")
        );
    }
}
