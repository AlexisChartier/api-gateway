package com.polytech.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity // OBLIGATOIRE pour la sécurité avec Spring Cloud Gateway (qui est réactif)
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            // 1. Définir les règles d'autorisation
            .authorizeExchange(exchanges -> exchanges
                // EXEMPLE: Si vous avez un endpoint "public" (ex: actuator)
                // .pathMatchers("/actuator/**").permitAll() 
                
                // EXEMPLE: Autoriser les requêtes de type OPTIONS (pour CORS)
                // .pathMatchers(HttpMethod.OPTIONS).permitAll()

                // 2. Sécuriser TOUTES les autres routes
                .anyExchange().authenticated()
            )
            
            // 3. Activer la validation des tokens JWT (mode "Resource Server")
            .oauth2ResourceServer(oauth2 -> oauth2.jwt());
            
            // 4. Désactiver CSRF car nous sommes une API stateless
            http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}