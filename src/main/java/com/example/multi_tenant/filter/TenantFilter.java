package com.example.multi_tenant.filter;

import com.example.multi_tenant.config.TenantIdentifierResolverImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilter extends OncePerRequestFilter {
    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String DEFAULT_TENANT = "defaultTenant";

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        try {

            String tenant = request.getHeader(TENANT_HEADER);  //will parse from JWT token
            if (tenant == null || tenant.isEmpty()) {
                tenant = DEFAULT_TENANT;
            }

            TenantIdentifierResolverImpl.setCurrentTenant(tenant);
            filterChain.doFilter(request, response);
        } finally {
           // TenantIdentifierResolverImpl.clearCurrentTenant();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 1. Skip filter for tenant onboarding endpoints
        String path = request.getRequestURI();
        String[] skipPatterns = {"/api/v1/admin/tenants/**"};

        for (String pattern : skipPatterns) {
            if (pathMatcher.match(pattern, path)) {
                return true; // skip the filter
            }
        }

        // 2. If no tenant header exists → do not run filter
        // (your controller will decide what to do)
        String tenant = request.getHeader(TENANT_HEADER);
        return (tenant == null || tenant.isBlank());
    }
}
