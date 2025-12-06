package com.example.multi_tenant.master.tenant;

import com.example.multi_tenant.master.tenant.dto.CreateTenantRequest;
import com.example.multi_tenant.master.tenant.service.TenantProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/tenants")
public class TenantController {
    private final TenantProviderService service;
//    private final TenantDtoMapper mapper;

    @PostMapping
    public Tenant createTenant(@Valid @RequestBody CreateTenantRequest req) {
        var res = service.createTenant(req);
//        return mapper.toResponse(res);
        return res;  //todo: mapping
    }
}
