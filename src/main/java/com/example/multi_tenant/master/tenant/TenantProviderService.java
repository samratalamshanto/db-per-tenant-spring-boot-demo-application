package com.example.multi_tenant.master.tenant;

import org.springframework.data.util.Pair;

import javax.sql.DataSource;

public interface TenantProviderService {
    Pair<Boolean, String> createTenant(TenantRequest req);

    DataSource getOrCreate(String tenantId);
}
