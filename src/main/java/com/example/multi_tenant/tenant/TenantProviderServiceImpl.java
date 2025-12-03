package com.example.multi_tenant.tenant;

import com.example.multi_tenant.config.DynamicDataSourceConnectionProvider;
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
            GRANT ALL PRIVILEGES ON DATABASE db_name TO db_user;
        */

        String createDbSql = "CREATE DATABASE " + req.getDbName();
        String createUserSql = "CREATE USER " + req.getDbUserName() +
                " WITH PASSWORD '" + req.getDbPassword() + "'";
        String grantSql = "GRANT ALL PRIVILEGES ON DATABASE " +
                req.getDbName() + " TO " + req.getDbUserName();

        try (Connection conn = masterDataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(createDbSql);
            stmt.executeUpdate(createUserSql);
            stmt.executeUpdate(grantSql);

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

//        try (Connection conn = ds.getConnection()) {
//            Liquibase liquibase = new Liquibase(
//                    "db/changelog/db.changelog-master.yaml",
//                    new ClassLoaderResourceAccessor(),
//                    DatabaseFactory.getInstance()
//                            .findCorrectDatabaseImplementation(
//                                    new JdbcConnection(conn)
//                            )
//            );
//            liquibase.update(new Contexts());
//        } catch (Exception e) {
//            throw new RuntimeException("Migration failed for tenant DB", e);
//        }
    }

    private void saveTenant(TenantRequest req) {
        Tenant tenant = new Tenant();
        tenant.setTenantKey(req.getTenantKey());
        tenant.setDbName(req.getDbName());
        tenant.setDbUserName(req.getDbUserName());
        tenant.setDbHashedPassword(req.getDbPassword());  //todo: need to hashed

        tenantRepository.save(tenant);
    }
}
