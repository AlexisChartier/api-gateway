# Api Gateway
Cette API Gateway est le point d'entrée unique pour toute l'architecture microservices du projet Eventy. Elle est construite avec Spring Cloud Gateway et est conçue pour router les requêtes externes (provenant de l'application mobile) vers le microservice interne approprié de manière dynamique et sécurisée.

## Rôle et fonctionnement
Le rôle principal de cette gateway est de simplifier la communication avec le backend en agissant comme une façade. Plutôt que de laisser le client (l'application mobile) connaître les adresses de chaque microservice, il ne communique qu'avec la gateway.

Les fonctionnalités clés sont :

-  **Point d'Entrée Unique** : Toutes les requêtes API passent par cette gateway sur le port 8080.

-  **Routage Dynamique** : Les requêtes sont redirigées vers le service compétent en se basant sur le chemin de l'URL (par exemple, /api/users/** est envoyé au user-service).

-  **Découverte de Services** : La gateway est connectée à un serveur Eureka, ce qui lui permet de trouver automatiquement l'emplacement (IP et port) des autres microservices, même s'ils sont déplacés ou mis à l'échelle.

-  **Centralisation des "Cross-Cutting Concerns"** : À l'avenir, elle pourra gérer de manière centralisée l'authentification, la limitation de débit (rate limiting), la journalisation (logging), etc.

## Dépendances utilisées

Ce projet repose sur les dépendances Spring Boot suivantes :

spring-cloud-starter-gateway: Le framework principal qui fournit toutes les fonctionnalités de routage, de filtrage et de proxy.
spring-cloud-starter-netflix-eureka-client: Permet à la gateway de s'enregistrer auprès du serveur Eureka et de découvrir d'autres services.
spring-boot-starter-actuator: Expose des endpoints de monitoring (/actuator) pour vérifier l'état de la gateway.

# Configuration des Routes

Le routage est défini de manière programmatique dans la classe GatewayConfig.java. Chaque route intercepte un modèle de chemin spécifique et le redirige vers un service découvert par Eureka.

Exemple de route pour le user-service :

.route("user-service-route", r -> r
    .path("/api/users/**")                 // 1. Intercepte les requêtes commençant par /api/users/
    .filters(f -> f.stripPrefix(2))       // 2. Retire '/api/users' de l'URL
    .uri("lb://user-service")             // 3. Redirige vers le service 'user-service' via Eureka
)

Le préfixe lb:// indique à Spring Cloud Gateway d'utiliser son mécanisme de répartition de charge (Load Balancing) pour trouver une instance saine du service via Eureka.

## Guide pour les autres Microservices (Intégration avec Eureka)

Pour qu'un microservice (par exemple, user-service, ticket-service, etc.) puisse être découvert et utilisé par cette gateway, il DOIT 

s'enregistrer comme un client Eureka.Voici les étapes à suivre pour chaque microservice :

1. Ajouter la dépendance Eureka ClientDans le fichier pom.xml de votre microservice, ajoutez la dépendance suivante :
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

N'oubliez pas d'inclure la section <dependencyManagement> pour spring-cloud-dependencies si ce n'est pas déjà fait.

2. Configurer le application.ymlDans le fichier src/main/resources/application.yml de votre microservice, vous devez définir son nom et l'adresse du serveur Eureka.spring:
  application:
    # IMPORTANT : C'est ce nom que la gateway utilise pour le routage (ex: "lb://user-service")
    name: user-service # Remplacez par le nom de votre service

eureka:
  client:
    service-url:
      # L'URL où se trouve votre serveur Eureka
      defaultZone: http://localhost:8761/eureka/
  instance:
    # Préférer l'adresse IP à l'hostname pour l'enregistrement
    prefer-ip-address: true
3. Activer la découverteAssurez-vous que l'annotation @EnableDiscoveryClient est présente sur votre classe principale d'application Spring Boot.@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args); 
    }
}
Une fois ces trois étapes complétées, votre microservice s'enregistrera automatiquement auprès d'Eureka au démarrage, et la gateway sera capable de lui transférer les requêtes. +TEST




