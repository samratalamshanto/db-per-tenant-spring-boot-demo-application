package com.example.multi_tenant.master.tenant;

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

    @PostMapping
    public String createTenant(@Valid @RequestBody TenantRequest req) {
        var res = service.createTenant(req);
        return res.getSecond();
    }
}
