package com.example.multi_tenant.master.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantCreationResponse {
    private Long id;
    private String tenantKey;
    private String dbName;
    private String schemaName;
    private String dbUserName;
    private String dbPassword;
}
