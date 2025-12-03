package com.example.multi_tenant.tenant;

import org.springframework.data.util.Pair;

public interface TenantProviderService {
    Pair<Boolean, String> createTenant(TenantRequest req);
}
