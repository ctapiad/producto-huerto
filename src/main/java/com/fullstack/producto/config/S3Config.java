package com.fullstack.producto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    @Value("${aws.s3.access-key-id:}")
    private String accessKeyId;

    @Value("${aws.s3.secret-access-key:}")
    private String secretAccessKey;

    @Value("${aws.s3.use-iam-role:false}")
    private boolean useIamRole;

    /**
     * Configuración de S3Client con soporte para:
     * 1. IAM Role (recomendado para EC2) - usa DefaultCredentialsProvider
     * 2. Credenciales explícitas (para desarrollo local o otros entornos)
     */
    @Bean
    public S3Client s3Client() {
        // Si estamos en EC2 con IAM Role, usar DefaultCredentialsProvider
        if (useIamRole || (accessKeyId.isEmpty() && secretAccessKey.isEmpty())) {
            System.out.println("✅ Usando IAM Role de EC2 para S3Client");
            return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        } else {
            // Usar credenciales explícitas
            System.out.println("⚠️ Usando credenciales explícitas para S3Client");
            return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();
        }
    }

    @Bean
    public S3Presigner s3Presigner() {
        // Si estamos en EC2 con IAM Role, usar DefaultCredentialsProvider
        if (useIamRole || (accessKeyId.isEmpty() && secretAccessKey.isEmpty())) {
            System.out.println("✅ Usando IAM Role de EC2 para S3Presigner");
            return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        } else {
            // Usar credenciales explícitas
            System.out.println("⚠️ Usando credenciales explícitas para S3Presigner");
            return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();
        }
    }
}
