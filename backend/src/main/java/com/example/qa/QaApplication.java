package com.example.qa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class QaApplication {
    public static void main(String[] args) {
        SpringApplication.run(QaApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://prj2a-repo-hardeng.app.secoder.net/",
                                "https://prj2a-repo-dev-hardeng.app.secoder.net/",
                                "http://localhost:3000/",
                                "http://localhost:8080")
                        .allowedMethods("*")
                        .allowCredentials(true);
            }
        };
    }
}
