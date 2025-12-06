package com.example.multi_tenant.mapper;

import com.example.multi_tenant.master.tenant.Tenant;
import com.example.multi_tenant.master.tenant.dto.CreateTenantRequest;
import com.example.multi_tenant.master.tenant.dto.TenantCreationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TenantDtoMapper {
    @Mapping(target = "dbPassword", source = "dbHashedPassword", qualifiedByName = "reHashPassword")
    TenantCreationResponse toResponse(Tenant source);

    @Named("reHashPassword")
    default String reHashPassword(String dbHashedPassword) {
        if (dbHashedPassword == null) return null;
        return dbHashedPassword; //todo: re-hased mechanism
    }

    Tenant toEntity(CreateTenantRequest destination);
}
