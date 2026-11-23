package cl.duoc.backend.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Autowired
    private LoggingFilter loggingFilter;

    // Orígenes extra (el frontend desplegado), separados por coma.
    // Si la variable no existe, queda vacío y solo se usan los locales.
    @Value("${app.cors.allowed-origins:}")
    private String[] extraOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] localOrigins = {
                "http://localhost:3000",
                "http://localhost:4200",
                "http://localhost:5173"
        };

        String[] allOrigins = java.util.stream.Stream
                .concat(java.util.Arrays.stream(localOrigins), java.util.Arrays.stream(extraOrigins))
                .filter(o -> !o.isBlank())
                .toArray(String[]::new);

        registry.addMapping("/**")
                .allowedOrigins(allOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingFilter)
                .addPathPatterns("/**");
    }
}