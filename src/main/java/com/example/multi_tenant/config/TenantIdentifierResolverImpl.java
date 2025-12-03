package com.example.multi_tenant.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver  {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    public static void setCurrentTenant(String tenant) {
        CURRENT_TENANT.set(tenant);
    }
    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }
    public static void clearCurrentTenant() {
        CURRENT_TENANT.remove();
    }


    /**
     *  Returns current tenant identifier. Hibernate uses it to route queries
     * to correct DataSource
     */
    @Override
    public Object resolveCurrentTenantIdentifier() {
        String tenantId = TenantIdentifierResolverImpl.getCurrentTenant();
        return (tenantId != null && !tenantId.isEmpty()) ? tenantId : null;
    }

    /** Determines whether Hibernate should revalidate the current tenant identifier
     * on every transaction. Returning true ensures dynamic switching.
     */
    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
