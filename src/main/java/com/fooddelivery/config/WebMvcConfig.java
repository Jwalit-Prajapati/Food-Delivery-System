package com.fooddelivery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC configuration.
 *
 * <p>Registers the webapp's {@code /resources/} directory as a static-resource
 * location so that CSS, JS and image files served by JSP pages (e.g.
 * {@code ${pageContext.request.contextPath}/resources/css/styles.css}) are
 * accessible via the Spring MVC resource handler.
 *
 * <p>Without this registration, Spring Boot's auto-configured resource handler
 * only serves files from {@code classpath:/static/}, {@code classpath:/public/},
 * {@code classpath:/resources/}, and {@code classpath:/META-INF/resources/} —
 * it does NOT serve files that live under {@code src/main/webapp/resources/}.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve /resources/** from src/main/webapp/resources/
        // This exposes styles.css (and any future JS/images) to JSP pages.
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
    }
}
