package cl.duoc.backend.config;

import cl.duoc.backend.security.RestAccessDeniedHandler;
import cl.duoc.backend.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
        private final RestAuthenticationEntryPoint authenticationEntryPoint;
        private final RestAccessDeniedHandler accessDeniedHandler;
        private final JwtRoleConverter jwtRoleConverter; // 👈 NUEVO

        public SecurityConfig(
                        RestAuthenticationEntryPoint authenticationEntryPoint,
                        RestAccessDeniedHandler accessDeniedHandler,
                        JwtRoleConverter jwtRoleConverter) { // 👈 NUEVO
                this.authenticationEntryPoint = authenticationEntryPoint;
                this.accessDeniedHandler = accessDeniedHandler;
                this.jwtRoleConverter = jwtRoleConverter;
        }

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(Customizer.withDefaults())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/api/auth/**").permitAll()

                                                // 👇 RESTRICCIÓN POR ROL: Solo ADMIN puede tocar usuarios y roles
                                                .requestMatchers("/api/v1/users/**", "/api/v1/roles/**")
                                                .hasRole("ADMIN")

                                                .anyRequest().authenticated())
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(authenticationEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandler))
                                .oauth2ResourceServer(resourceServer -> resourceServer
                                                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtRoleConverter))
                                                .authenticationEntryPoint(authenticationEntryPoint))
                                .build();
        }
}