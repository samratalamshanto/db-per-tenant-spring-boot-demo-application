package com.example.multi_tenant.master.tenant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tenant_details")
@Builder
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tenantKey;
    private String dbName;
    private String schemaName;
    private String dbUserName;
    private String dbHashedPassword;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Tenant(String tenantKey, String dbName, String schemaName, String dbPassword) {
        this.tenantKey = tenantKey;
        this.dbName = dbName;
        this.schemaName = schemaName;
        this.dbUserName = dbPassword;
        this.dbHashedPassword = dbPassword;
    }
}
