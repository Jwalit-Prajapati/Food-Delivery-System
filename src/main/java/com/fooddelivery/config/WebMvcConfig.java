package com.fooddelivery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC customisation:
 * – Registers the {@link NoCacheInterceptor} for all dynamic HTML pages so that
 *   browsers always see fresh data after a POST+redirect.
 * – Excludes static resources from the no-cache policy.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new NoCacheInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/resources/**",
                    "/static/**",
                    "/webjars/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico"
                );
    }
}
