package com.example.multi_tenant.master.tenant.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTenantRequest {
    @NotEmpty
    private String dbName;
    @NotEmpty
    private String schemaName;
    @NotEmpty
    private String dbUserName;
    @NotEmpty
    private String dbPassword;
}
