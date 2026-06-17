package com.fooddelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Entry point for the Food Delivery System — Spring Boot 3 / Jakarta EE 10.
 *
 * <p>What this single annotation replaces from the legacy setup:
 * <ul>
 *   <li>{@code @Configuration}        — context is now auto-configured by Boot</li>
 *   <li>{@code @ComponentScan}        — scans {@code com.fooddelivery.**} by default</li>
 *   <li>{@code @EnableAutoConfiguration} — wires DataSource, JPA, Security, Web MVC, etc.
 *       from {@code application.yml} without any explicit {@code @Bean} boilerplate</li>
 * </ul>
 *
 * <p>{@link EnableTransactionManagement} is kept explicit here so the intent is
 * obvious to future maintainers, even though Boot's auto-config would enable it
 * anyway when a {@code DataSource} is present.
 */
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableTransactionManagement
public class FoodDeliveryApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(FoodDeliveryApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(FoodDeliveryApplication.class, args);
    }

}
