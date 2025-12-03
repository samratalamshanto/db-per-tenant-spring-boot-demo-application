package com.example.multi_tenant.tenant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantRequest {
    private String tenantKey;
    private String dbName;
    private String dbUserName;
    private String dbPassword;
}
