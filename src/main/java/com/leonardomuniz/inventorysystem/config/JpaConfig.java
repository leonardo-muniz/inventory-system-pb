package com.leonardomuniz.inventorysystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // Esta classe isola a configuração de auditoria
}
