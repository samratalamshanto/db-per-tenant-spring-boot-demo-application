package com.example.multi_tenant.config.db;

import com.example.multi_tenant.config.CamelCaseToSnakeCaseStrategy;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.multi_tenant.tenant",
        entityManagerFactoryRef = "tenantEntityManagerFactory",
        transactionManagerRef = "tenantTransactionManager"
)
public class TenantDbConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(
            MultiTenantConnectionProvider connectionProvider,
            CurrentTenantIdentifierResolver tenantResolver) {

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.show_sql", true);
        jpaProperties.put("hibernate.format_sql", true);
        jpaProperties.put("hibernate.multiTenancy", "DATABASE");
        jpaProperties.put("hibernate.multi_tenant_connection_provider", connectionProvider);
        jpaProperties.put("hibernate.tenant_identifier_resolver", tenantResolver);

        // camelCase to Db snakeCase issue
        jpaProperties.put("hibernate.physical_naming_strategy", new CamelCaseToSnakeCaseStrategy());
        jpaProperties.put("hibernate.implicit_naming_strategy", new ImplicitNamingStrategyJpaCompliantImpl());


        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPackagesToScan("com.example.multi_tenant.tenant");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setJpaPropertyMap(jpaProperties);
        return emf;
    }

    @Bean
    public PlatformTransactionManager tenantTransactionManager(
            @Qualifier("tenantEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
