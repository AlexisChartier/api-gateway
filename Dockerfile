# api-gateway/Dockerfile (Version corrigée et robuste)

# Étape 1 : Construction de l'application avec Maven
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app

# 1. Copie des fichiers essentiels pour le Maven Wrapper
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# 2. CORRECTION ROBUSTE : Assure les permissions d'exécution et corrige les fins de ligne (CRLF -> LF)
RUN chmod +x ./mvnw && sed -i 's/\r$//' ./mvnw

# 3. Copie du code source et exécution du build via mvnw
COPY src/ src/
RUN ./mvnw clean package -DskipTests -e
# Étape 2 : Création de l'image d'exécution légère
FROM eclipse-temurin:17-jre 
# Copie le .jar construit à l'étape précédente
COPY --from=build /app/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar
# Expose le port sur lequel l'application tourne
EXPOSE 8080
# Commande pour démarrer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]