# Étape 1 : Construction de l'application avec Maven
FROM maven:3.8.5-openjdk-17-slim AS build
COPY . /app
WORKDIR /app
# Construit le projet et génère le fichier .jar
RUN mvn clean package -DskipTests

# Étape 2 : Création de l'image d'exécution légère
FROM openjdk:17-jdk-slim
# Copie le .jar construit à l'étape précédente
COPY --from=build /app/target/api-gateway-0.0.1-SNAPSHOT.jar app.jar
# Expose le port sur lequel l'application tourne
EXPOSE 8080
# Commande pour démarrer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]