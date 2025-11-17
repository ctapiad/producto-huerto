#!/bin/bash
# Script para subir el código a GitHub y configurar deployment

echo "╔════════════════════════════════════════════════════════════╗"
echo "║  Despliegue de Producto Service a GitHub y AWS EC2        ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Paso 1: Verificar repositorio
echo -e "${YELLOW}[1/6]${NC} Verificando repositorio Git..."
git remote -v
echo ""

# Paso 2: Verificar cambios pendientes
echo -e "${YELLOW}[2/6]${NC} Verificando estado del repositorio..."
git status
echo ""

# Paso 3: Agregar cambios
echo -e "${YELLOW}[3/6]${NC} Agregando todos los archivos..."
git add .
echo -e "${GREEN}✓${NC} Archivos agregados"
echo ""

# Paso 4: Hacer commit
echo -e "${YELLOW}[4/6]${NC} Creando commit..."
git commit -m "Configuración completa de CI/CD para deployment en EC2

- Pipeline de GitHub Actions configurado
- Scripts de deployment para EC2
- Servicio systemd configurado
- Documentación completa incluida
- Puerto configurado a 8081
- MongoDB Atlas conectado" || echo -e "${YELLOW}⚠${NC} Sin cambios para commit"
echo ""

# Paso 5: Push a GitHub
echo -e "${YELLOW}[5/6]${NC} Subiendo a GitHub..."
echo -e "${RED}⚠ Si hay conflictos, se forzará el push${NC}"
read -p "¿Desea continuar? (s/n): " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Ss]$ ]]
then
    git push origin main --force
    echo -e "${GREEN}✓${NC} Código subido a GitHub"
else
    echo -e "${RED}✗${NC} Push cancelado"
    exit 1
fi
echo ""

# Paso 6: Instrucciones finales
echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║                  ¡PUSH EXITOSO!                            ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${YELLOW}📋 SIGUIENTES PASOS:${NC}"
echo ""
echo -e "1️⃣  ${YELLOW}Configurar Secrets en GitHub${NC}"
echo "   → Ve a: https://github.com/ctapiad/producto-huerto/settings/secrets/actions"
echo "   → Revisa el archivo: ${GREEN}GITHUB_SECRETS.md${NC} para los valores exactos"
echo "   → Necesitas agregar:"
echo "      • EC2_HOST = 54.158.158.91"
echo "      • EC2_USER = ubuntu"
echo "      • EC2_SSH_KEY = (contenido de ~/Downloads/huerto.pem)"
echo ""
echo -e "2️⃣  ${YELLOW}Configurar EC2 (primera vez)${NC}"
echo "   Ejecuta estos comandos:"
echo -e "   ${GREEN}scp -i ~/Downloads/huerto.pem setup-ec2.sh ubuntu@54.158.158.91:/home/ubuntu/${NC}"
echo -e "   ${GREEN}scp -i ~/Downloads/huerto.pem producto-service.service ubuntu@54.158.158.91:/home/ubuntu/${NC}"
echo -e "   ${GREEN}ssh -i ~/Downloads/huerto.pem ubuntu@54.158.158.91${NC}"
echo "   Luego en EC2:"
echo -e "   ${GREEN}chmod +x setup-ec2.sh && ./setup-ec2.sh${NC}"
echo ""
echo -e "3️⃣  ${YELLOW}Configurar Security Group en AWS${NC}"
echo "   Asegúrate de tener abiertos los puertos:"
echo "   • 22 (SSH)"
echo "   • 8081 (Aplicación)"
echo "   • 80, 443 (opcional)"
echo ""
echo -e "4️⃣  ${YELLOW}Configurar MongoDB Atlas${NC}"
echo "   • Agregar IP de EC2: 54.158.158.91"
echo "   • O permitir acceso desde: 0.0.0.0/0"
echo ""
echo -e "5️⃣  ${YELLOW}Verificar Deployment${NC}"
echo "   • GitHub Actions: https://github.com/ctapiad/producto-huerto/actions"
echo "   • Health Check: http://54.158.158.91:8081/api/productos/health"
echo "   • Swagger UI: http://54.158.158.91:8081/swagger-ui/index.html"
echo ""
echo -e "${GREEN}════════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "${YELLOW}📖 Documentación disponible:${NC}"
echo "   • GITHUB_SECRETS.md - Secrets de GitHub"
echo "   • DEPLOYMENT.md - Guía completa de deployment"
echo "   • README.md - Documentación del proyecto"
echo ""
