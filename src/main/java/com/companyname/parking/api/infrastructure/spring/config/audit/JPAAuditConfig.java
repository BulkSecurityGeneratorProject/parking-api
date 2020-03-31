package com.companyname.parking.api.infrastructure.spring.config.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static com.companyname.parking.api.infrastructure.security.SecurityUtils.getCurrentUserLogin;

@Configuration
public class JPAAuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                return getCurrentUserLogin();
            } else {
                return Optional.of("Unknown");
            }
        };
    }
}
