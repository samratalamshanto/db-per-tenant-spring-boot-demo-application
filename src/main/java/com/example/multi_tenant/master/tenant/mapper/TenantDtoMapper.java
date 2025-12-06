package com.example.multi_tenant.master.tenant.mapper;

import com.example.multi_tenant.master.tenant.Tenant;
import com.example.multi_tenant.master.tenant.dto.CreateTenantRequest;
import com.example.multi_tenant.master.tenant.dto.TenantCreationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TenantDtoMapper {
    TenantCreationResponse toResponse(Tenant source);

    Tenant toEntity(CreateTenantRequest destination);
}
