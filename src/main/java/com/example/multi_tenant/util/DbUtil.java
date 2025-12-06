package com.example.multi_tenant.util;

import com.example.multi_tenant.config.properties.TenantDbProperties;
import com.example.multi_tenant.master.tenant.Tenant;
import com.example.multi_tenant.master.tenant.dto.CreateTenantRequest;

public final class DbUtil {
    private DbUtil() {
    }

    public static String getDbUrl(Tenant t, TenantDbProperties tenantDbProperties) {
        return String.format(
                "jdbc:postgresql://%s:%d/%s?currentSchema=%s",
                tenantDbProperties.getHost(),
                tenantDbProperties.getPort(),
                t.getDbName(),
                t.getDbName()
        );

    }

    public static String getDbUrl(CreateTenantRequest req, TenantDbProperties tenantDbProperties) {
        return String.format(
                "jdbc:postgresql://%s:%d/%s?currentSchema=%s",
                tenantDbProperties.getHost(),
                tenantDbProperties.getPort(),
                req.getDbName(),
                req.getSchemaName()
        );
    }
}
