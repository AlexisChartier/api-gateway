package com.polytech.gateway.route;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class GatewayConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayConfig.class);

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        LOGGER.info("Configuration des routes de la passerelle Eventy");

        return builder.routes()
                // --- Route pour le service Utilisateurs ---
                .route("user-service-route", r -> r
                        .path("/api/users/**") // Si une requête arrive sur ce chemin...
                        .filters(f -> f.stripPrefix(1)) // ... on retire les 2 premiers segments ('/api/users') avant de la transférer...
                        .uri("lb://eventy-users-service") // ... vers le service nommé 'user-service' trouvé via Eureka.
                )

                // --- Route pour le service Événements ---
                .route("event-service-route", r -> r
                        .path("/api/events/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://eventy-events-service")
                )

                // --- Route pour le service Tickets ---
                .route("ticket-service-route", r -> r
                        .path("/api/tickets/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://tickets-service")
                )

                // --- Route pour le service Transactions ---
                .route("transaction-service-route", r -> r
                        .path("/api/transactions/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://transactions-service")
                )

                // --- Route pour le service Interactions (Messages, Avis, Signalements) ---
                .route("interaction-service-route", r -> r
                        .path("/api/interactions/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://interaction-service")
                )
                .route("event-categories-route", r -> r
                        .path("/api/event-categories/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://eventy-events-service")
                )
                .build();
    }
}