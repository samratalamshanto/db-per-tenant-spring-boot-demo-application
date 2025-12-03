package com.example.multi_tenant.tenant;

import com.example.multi_tenant.config.DynamicDataSourceConnectionProvider;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantProviderServiceImpl implements TenantProviderService {
    private final DataSource masterDataSource;
    private final TenantRepository tenantRepository;
    private final DynamicDataSourceConnectionProvider connectionProvider;
    private final TenantDbProperties tenantDbProperties;

    @Transactional
    @Override
    public Pair<Boolean, String> createTenant(TenantRequest req) {
        saveTenant(req);
        createDatabase(req);
        runMigrations(req);

        connectionProvider.clearCache(req.getTenantKey());
        return Pair.of(true, "Success");
    }


    private void createDatabase(TenantRequest req) {
        /*
            CREATE DATABASE db_name;
            CREATE USER db_user WITH PASSWORD 'db_password';
            CREATE SCHEMA db_name AUTHORIZATION db_user;
            GRANT ALL PRIVILEGES ON DATABASE db_name TO db_user;
        */

        String createDbSql = "CREATE DATABASE " + req.getDbName();
        String createUserSql = "CREATE USER " + req.getDbUserName() +
                " WITH PASSWORD '" + req.getDbPassword() + "'";
        String createSchemaSql = "CREATE SCHEMA " + req.getDbName() + " AUTHORIZATION " + req.getDbUserName();
        String grantSql = "GRANT ALL PRIVILEGES ON DATABASE " +
                req.getDbName() + " TO " + req.getDbUserName();

        try (Connection conn = masterDataSource.getConnection();
             Statement stmt = conn.createStatement()
        ) {

            stmt.executeUpdate(createUserSql);
            stmt.executeUpdate(createDbSql);
            stmt.executeUpdate(grantSql);


            String tenantDbUrl = String.format(
                    "jdbc:postgresql://%s:%d/%s",
                    tenantDbProperties.getHost(),
                    tenantDbProperties.getPort(),
                    req.getDbName()
            );

            DataSource tenantDs = DataSourceBuilder.create()
                    .url(tenantDbUrl)
                    .driverClassName(tenantDbProperties.getDriver())
                    .username(req.getDbUserName())
                    .password(req.getDbPassword())
                    .build();
            try (Connection tenantConn = tenantDs.getConnection();
                 Statement tenantStmt = tenantConn.createStatement()
            ) {
                tenantStmt.executeUpdate(createSchemaSql);  //create user before creating schema with user authorization
            }


        } catch (Exception e) {
            throw new RuntimeException("Failed to create DB for tenant", e);
        }
    }

    private void runMigrations(TenantRequest req) {
        String dbUrl = String.format(
                "jdbc:postgresql://%s:%d/%s",
                tenantDbProperties.getHost(),
                tenantDbProperties.getPort(),
                req.getDbName()
        );

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

    private void saveTenant(TenantRequest req) {
        Tenant tenant = new Tenant();
        tenant.setTenantKey(req.getTenantKey());
        tenant.setDbName(req.getDbName());
        tenant.setSchemaName(req.getDbName());
        tenant.setDbUserName(req.getDbUserName());
        tenant.setDbHashedPassword(req.getDbPassword());  //todo: need to hashed

        tenantRepository.save(tenant);
    }
}
