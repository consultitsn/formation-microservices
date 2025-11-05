# ============================================
# Stage 1: Build - Compilation de l'application
# ============================================
FROM eclipse-temurin:17-jdk-jammy AS builder

# Informations sur le stage de build
LABEL stage=builder

# Installer Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Définir le répertoire de travail
WORKDIR /build

# Copier les fichiers de configuration Maven
COPY pom.xml .

# Télécharger les dépendances (cache layer)
RUN mvn dependency:go-offline -B || true

# Copier les sources
COPY src ./src

# Compiler l'application et créer le JAR
RUN mvn clean package -DskipTests && \
    mkdir -p /app && \
    cp target/productService-*.jar /app/app.jar

# ============================================
# Stage 2: Runtime - Image finale légère
# ============================================
FROM eclipse-temurin:17-jre-jammy

# Informations sur le mainteneur
LABEL maintainer="formation-microservices@example.com"
LABEL description="Product Service - Microservice de gestion des produits"
LABEL version="1.0.0"

# Variables d'environnement
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=docker

# Installer curl pour le health check
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

# Créer un utilisateur non-root pour la sécurité
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Créer le répertoire de travail
WORKDIR /app

# Copier le JAR depuis le stage de build
COPY --from=builder /app/app.jar app.jar

# Changer les permissions
RUN chown -R appuser:appuser /app

# Passer à l'utilisateur non-root
USER appuser

# Exposer le port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# Commande de démarrage
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
