package com.example.multi_tenant.config;

import com.example.multi_tenant.tenant.Tenant;
import com.example.multi_tenant.tenant.TenantDbProperties;
import com.example.multi_tenant.tenant.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class DynamicDataSourceConnectionProvider implements MultiTenantConnectionProvider {
    private final TenantRepository tenantRepository;
    private final DataSource defaultDataSource;
    private final TenantDbProperties tenantDbProperties;

    private final Map<String, DataSource> dataSourceCache = new ConcurrentHashMap<>();

    @Override
    public Connection getAnyConnection() throws SQLException {
        return defaultDataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(Object tenantIdentifier) throws SQLException {
        DataSource dataSource = dataSourceCache.computeIfAbsent(tenantIdentifier.toString(), key -> {
            Tenant tenant = tenantRepository.findByTenantKey(key).orElse(null);
            return buildDataSource(tenant);
        });

        return dataSource.getConnection();
    }

    @Override
    public void releaseConnection(Object tenantIdentifier, Connection connection) throws SQLException {
        connection.close(); // return to pool
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    private DataSource buildDataSource(Tenant tenant) {

        String dbUrl = String.format(
                "jdbc:postgresql://%s:%d/%s",
                tenantDbProperties.getHost(),
                tenantDbProperties.getPort(),
                tenant.getDbName()
        );


        var dataSource = DataSourceBuilder
                .create()
                .url(dbUrl)
                .username(tenant.getDbName())
                .password(tenant.getDbHashedPassword())  //todo: re-hashed passwords
                .build();

        // Test connection early
        try (Connection ignored = dataSource.getConnection()) {
            // OK
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to tenant DB: " + tenant.getDbName(), e);
        }

        return dataSource;


    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }

    public void clearCache(String tenantKey) {
        dataSourceCache.remove(tenantKey);
    }
}
