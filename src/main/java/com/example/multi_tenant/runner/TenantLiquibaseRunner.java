package com.example.multi_tenant.runner;

import com.example.multi_tenant.config.properties.TenantDbProperties;
import com.example.multi_tenant.master.tenant.Tenant;
import com.example.multi_tenant.master.tenant.TenantRepository;
import com.example.multi_tenant.util.DbUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantLiquibaseRunner implements ApplicationRunner {
    private final TenantRepository tenantRepository;
    private final TenantDbProperties tenantDbProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (Tenant tenant : tenantRepository.findAll()) {
            // Run Liquibase for each tenant
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DbUtil.getDbUrl(tenant, tenantDbProperties));
            config.setUsername(tenant.getDbUserName());
            config.setPassword(tenant.getDbHashedPassword());  //todo: re-hashed password
            HikariDataSource ds = new HikariDataSource(config);
            SpringLiquibase liquibase = new SpringLiquibase();
            liquibase.setDataSource(ds);
            liquibase.setChangeLog("classpath:db/changelog/db.changelog-tenant.yaml");
            liquibase.afterPropertiesSet();
        }
        log.info("Liquibase Migration Completed");
    }
}
