package com.example.multi_tenant.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class MasterDbProperties {
    private String url;
    private String username;
    private String password;
    private String dbName;
    private String schema;
}
