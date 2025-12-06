package com.example.multi_tenant.master.tenant.service;

import com.example.multi_tenant.master.tenant.Tenant;
import com.example.multi_tenant.master.tenant.dto.CreateTenantRequest;
import org.springframework.data.util.Pair;

import javax.sql.DataSource;

public interface TenantProviderService {
    Tenant createTenant(CreateTenantRequest req);

    DataSource getOrCreate(String tenantId);
}
