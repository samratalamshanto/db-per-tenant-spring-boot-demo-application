package com.example.multi_tenant.config.db;

import com.example.multi_tenant.config.CamelCaseToSnakeCaseStrategy;
import com.example.multi_tenant.config.properties.MasterDbProperties;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.multi_tenant.master",
        entityManagerFactoryRef = "masterEntityManagerFactory",
        transactionManagerRef = "masterTransactionManager"
)
@RequiredArgsConstructor
public class MasterDbConfig {
    private final MasterDbProperties masterDbProps;

    @Bean
    @Primary
    public DataSource masterDataSource() {
        return DataSourceBuilder.create()
                .url(masterDbProps.getUrl())
                .username(masterDbProps.getUsername())
                .password(masterDbProps.getPassword())
                .build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory() {

        Map<String, Object> jpaProperties = new HashMap<>();

        jpaProperties.put("hibernate.show_sql", true);
        jpaProperties.put("hibernate.format_sql", true);

        // camelCase to Db snakeCase issue
        jpaProperties.put("hibernate.physical_naming_strategy", new CamelCaseToSnakeCaseStrategy());
        jpaProperties.put("hibernate.implicit_naming_strategy", new ImplicitNamingStrategyJpaCompliantImpl());

        var emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(masterDataSource());
        emf.setPackagesToScan("com.example.multi_tenant.master");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setJpaPropertyMap(jpaProperties);
        return emf;
    }

    @Bean
    @Primary
    public PlatformTransactionManager masterTransactionManager(
            @Qualifier("masterEntityManagerFactory") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}

