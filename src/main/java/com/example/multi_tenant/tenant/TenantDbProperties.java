package com.example.multi_tenant.tenant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.tenant.db")
public class TenantDbProperties {
    private String host;
    private int port;
    private String driver;
}
