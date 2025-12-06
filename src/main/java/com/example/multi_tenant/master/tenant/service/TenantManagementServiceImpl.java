package com.example.multi_tenant.master.tenant.service;

import com.example.multi_tenant.mapper.TenantDtoMapper;
import com.example.multi_tenant.master.tenant.Tenant;
import com.example.multi_tenant.master.tenant.TenantRepository;
import com.example.multi_tenant.master.tenant.dto.CreateTenantRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantManagementServiceImpl implements TenantManagementService {
    private final TenantRepository tenantRepository;
    private final TenantDtoMapper mapper;


    @Override
    public Tenant saveTenant(CreateTenantRequest req) {
        Tenant tenant = mapper.toEntity(req);
        tenant.setTenantKey(UUID.randomUUID().toString());
        tenant.setDbHashedPassword(req.getDbPassword());  //todo: need to hashed

        return tenantRepository.save(tenant);
    }
}
