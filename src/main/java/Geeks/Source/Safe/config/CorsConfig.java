package Geeks.Source.Safe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
        .allowedOrigins("http://localhost:5173","http://localhost:56790/", "https://teal-gorilla-464304.hostingersite.com")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("Content-Type", "Authorization", "X-Requested-With", "Accept")
        .exposedHeaders("Access-Control-Allow-Headers", "Authorization")
        .allowCredentials(true);

            }
        };
    }
}
