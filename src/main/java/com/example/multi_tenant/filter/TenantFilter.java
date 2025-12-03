package com.example.multi_tenant.filter;

import com.example.multi_tenant.config.TenantIdentifierResolverImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {
    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String DEFAULT_TENANT = "tenantA";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        try {

            String tenant = request.getHeader(TENANT_HEADER);
            if (tenant == null || tenant.isEmpty()) {
                tenant = DEFAULT_TENANT;
            }

            TenantIdentifierResolverImpl.setCurrentTenant(tenant);
            filterChain.doFilter(request, response);
        } finally {
            TenantIdentifierResolverImpl.clearCurrentTenant();
        }
    }
}
