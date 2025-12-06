package com.example.multi_tenant.master.tenant.service;

import com.example.multi_tenant.master.tenant.Tenant;
import com.example.multi_tenant.master.tenant.dto.CreateTenantRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


public interface TenantManagementService {
    Tenant saveTenant(CreateTenantRequest req);
}
