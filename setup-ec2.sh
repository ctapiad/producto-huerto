# Script de configuración inicial para EC2
# Ejecutar este script en la EC2 una sola vez antes del primer deployment

echo "=== Configuración inicial de EC2 para Producto Service ==="

# Actualizar sistema
sudo apt-get update
sudo apt-get upgrade -y

# Instalar Java 17
echo "Instalando Java 17..."
sudo apt-get install -y openjdk-17-jdk

# Verificar instalación de Java
java -version

# Crear directorio para la aplicación
echo "Creando directorio de la aplicación..."
sudo mkdir -p /home/ubuntu/producto-service
sudo chown ubuntu:ubuntu /home/ubuntu/producto-service

# Copiar el archivo de servicio systemd
echo "Configurando systemd service..."
sudo cp producto-service.service /etc/systemd/system/

# Habilitar el servicio
sudo systemctl daemon-reload
sudo systemctl enable producto-service

# Configurar firewall (si UFW está habilitado)
echo "Configurando firewall..."
sudo ufw allow 8081/tcp
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

echo "=== Configuración completada ==="
echo "Siguiente paso: Configurar los secrets en GitHub:"
echo "  - AWS_HOST: 34.202.46.121"
echo "  - AWS_USER: ubuntu"
echo "  - SSH_PRIVATE_KEY: (tu clave privada SSH)"
