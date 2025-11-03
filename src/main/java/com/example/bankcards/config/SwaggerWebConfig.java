package com.example.bankcards.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация для отображения Swagger-документации.
 * <p>
 * Настраивает обработчик ресурсов, чтобы Swagger-файлы
 * из папки {@code classpath:/docs/} были доступны по пути {@code /docs/**}.
 */
@Configuration
public class SwaggerWebConfig implements WebMvcConfigurer {

    /**
     * Регистрирует обработчик ресурсов для Swagger-документации.
     *
     * @param registry реестр обработчиков ресурсов
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/docs/**")
                .addResourceLocations("classpath:/docs/");
    }
}
