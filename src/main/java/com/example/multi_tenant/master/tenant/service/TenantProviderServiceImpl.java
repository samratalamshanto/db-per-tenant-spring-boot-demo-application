package com.example.multi_tenant.master.tenant.service;


import com.example.multi_tenant.config.properties.TenantDbProperties;
import com.example.multi_tenant.master.tenant.Tenant;
import com.example.multi_tenant.master.tenant.TenantRepository;
import com.example.multi_tenant.master.tenant.dto.CreateTenantRequest;
import com.example.multi_tenant.util.DbUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantProviderServiceImpl implements TenantProviderService {
    private final JdbcTemplate masterDataSource;
    private final TenantRepository tenantRepository;
    private final TenantDbProperties tenantDbProperties;
    private final TenantManagementService tenantManagementService;
    private final Map<String, DataSource> cache = new ConcurrentHashMap<>();

    @Override
    public Tenant createTenant(CreateTenantRequest req) {
        createTenantDb(req);
        runMigrations(req);
        return tenantManagementService.saveTenant(req);
    }

    private void createTenantDb(CreateTenantRequest req) {
        createDatabase(req);
        createDbUser(req);
        grantDbPriviledges(req);
        createSchema(req);
        log.info("Privileges granted.");
    }

    private void createSchema(CreateTenantRequest req) {
        // 4) Check schema exists (must connect to tenant DB)
        DataSource tenantDs = DataSourceBuilder.create()
                .url("jdbc:postgresql://localhost:5432/" + req.getDbName())
                .username(req.getDbUserName())
                .password(req.getDbPassword())
                .driverClassName("org.postgresql.Driver")
                .build();

        JdbcTemplate tenantJdbc = new JdbcTemplate(tenantDs);

        boolean schemaExists = Boolean.TRUE.equals(
                tenantJdbc.queryForObject(
                        "SELECT EXISTS(" +
                                "SELECT schema_name FROM information_schema.schemata WHERE schema_name = ?" +
                                ")",
                        Boolean.class,
                        req.getDbName()
                )
        );

        if (!schemaExists) {
            tenantJdbc.execute(
                    "CREATE SCHEMA " + req.getDbName() +
                            " AUTHORIZATION " + req.getDbUserName()
            );
            log.info("Schema created: {}", req.getDbName());
        } else {
            log.info("Schema already exists — skipping");
        }
    }

    private void grantDbPriviledges(CreateTenantRequest req) {
        // 3) Grant privileges
        masterDataSource.execute(
                "GRANT ALL PRIVILEGES ON DATABASE " +
                        req.getDbName() + " TO " + req.getDbUserName()
        );

        masterDataSource.execute(
                "GRANT ALL PRIVILEGES ON DATABASE " +
                        req.getDbName() + " TO " + req.getDbUserName()
        );
    }

    private void createDbUser(CreateTenantRequest req) {
        // 2) Check User exists
        boolean userExists = Boolean.TRUE.equals(
                masterDataSource.queryForObject(
                        "SELECT EXISTS(SELECT 1 FROM pg_roles WHERE rolname = ?)",
                        Boolean.class,
                        req.getDbUserName()
                )
        );

        if (!userExists) {
            masterDataSource.execute(
                    "CREATE USER " + req.getDbUserName() +
                            " WITH PASSWORD '" + req.getDbPassword() + "'"
            );
            log.info("User created: {}", req.getDbUserName());
        } else {
            log.info("User already exists — skipping");
        }
    }

    private void createDatabase(CreateTenantRequest req) {
        // 1) Check DB exists
        boolean dbExists = Boolean.TRUE.equals(
                masterDataSource.queryForObject(
                        "SELECT EXISTS(SELECT 1 FROM pg_database WHERE datname = ?)",
                        Boolean.class,
                        req.getDbName()
                )
        );

        if (!dbExists) {
            masterDataSource.execute("CREATE DATABASE " + req.getDbName());
            log.info("Database created: {}", req.getDbName());
        } else {
            log.info("Database already exists — skipping");
        }
    }

    private void runMigrations(CreateTenantRequest req) {
        String dbUrl = DbUtil.getDbUrl(req, tenantDbProperties);

        DataSource ds = DataSourceBuilder.create()
                .url(dbUrl)
                .driverClassName(tenantDbProperties.getDriver())
                .username(req.getDbUserName())
                .password(req.getDbPassword())
                .build();


        try (Connection conn = ds.getConnection()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(conn));
            database.setDefaultSchemaName(req.getDbName());

            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-tenant.yaml",
                    new ClassLoaderResourceAccessor(),
                    database
            );
            liquibase.update(new Contexts());
        } catch (Exception e) {
            throw new RuntimeException("Migration failed for tenant DB", e);
        }
    }

    @Override
    public DataSource getOrCreate(String tenantId) {
        return cache.computeIfAbsent(tenantId, id -> {
            Tenant t = tenantRepository.findByTenantKey(id)
                    .orElseThrow(() -> new RuntimeException("Invalid tenant"));

            String dbUrl = DbUtil.getDbUrl(t, tenantDbProperties);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            config.setUsername(t.getDbUserName());
            config.setPassword(t.getDbHashedPassword());
            config.setMaximumPoolSize(5);

            HikariDataSource ds = new HikariDataSource(config);

            try (var _ = ds.getConnection()) {
                log.info("Db:{} connected", dbUrl);
            } catch (Exception e) {
                ds.close(); // cleanup HikariDataSource if connection fails
                throw new RuntimeException("Failed to connect to tenant DB: " + t.getDbName(), e);
            }

            return ds;
        });
    }
}
