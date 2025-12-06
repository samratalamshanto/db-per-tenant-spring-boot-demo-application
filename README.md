# DB-per-Tenant Spring Boot Demo Application

[![Java](https://img.shields.io/badge/Java-17+-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-lightgrey)](LICENSE)

---

## Description

This is a **database-per-tenant** (multi-tenant) demo application using **Spring Boot, Hibernate, and Liquibase**. It demonstrates runtime creation of tenant databases and automatic schema migration, while keeping all tenant metadata in a **master database**.

---

## Key Features 

* **Master & Tenant Liquibase Support:**

    * Master DB has its own Liquibase changelog for metadata.
    * Each tenant DB has a separate Liquibase changelog for schema.
    * On startup, all tenant DBs automatically run their respective changelogs to stay synchronized.

* **Runtime Tenant Creation:**

    * Dynamically create tenant databases at runtime.
    * Apply schema changes immediately via Liquibase.

* **Dynamic DataSource Routing:**

    * Requests are routed to the appropriate tenant database based on tenant identifier.

* **Strong Data Isolation:**

    * Each tenant has its own dedicated database to prevent data leakage.

* **Scalable & Maintainable:**

    * Add new tenants without downtime.
    * Schema updates per tenant while master DB remains consistent.

---

## Getting Started

### Prerequisites

* Java 17+
* Maven or Maven Wrapper
* PostgreSQL (or any JDBC-compatible DB) for master and tenant DBs

![img.png](img.png)

### Build & Run

```bash
# Build the project
./mvnw clean package -DskipTests

# Run the application
java -jar target/db-per-tenant-demo-0.0.1-SNAPSHOT.jar
```

### Configuration

* Master DB configuration: `application.yaml` / `application.properties`
* Tenant DBs are dynamically created during runtime.
* Metadata (DB name, credentials) stored in master DB.

---

## Workflow

1. **Startup**

    * Master DB runs its Liquibase changelog.
    * All existing tenant DBs run their Liquibase changelog to sync schema.

2. **Tenant Creation (Runtime)**

    * Tenant DB created dynamically.
    * Tenant-specific Liquibase changelog applied.
    * Metadata saved in master DB.

3. **Request Handling**

    * Tenant identifier extracted from request.
    * DataSource routing ensures queries hit the correct tenant DB.

---

## Quick Start Example

```bash
# Create a new tenant via REST API
curl -X POST \
  http://localhost:8090/api/v1/admin/tenants \
  -H "Content-Type: application/json" \
  -d '{
        "dbName": "test_db_21",
        "schemaName": "test_schema",
        "dbUserName": "test_user_21",
        "dbPassword": "12345"
      }'
```

### Sample Response

```json
{
  "id": 8,
  "tenantKey": "a625205b-6104-4ef4-9a0e-76f1f5978c39",
  "dbName": "test_db_21",
  "schemaName": "test_schema",
  "dbUserName": "test_user_21",
  "dbPassword": "12345"
}
```

---

## Recommendations

* Use secure password storage (hashed or encrypted).
* Enable connection pooling for tenant databases in production.
* Version-control all Liquibase changelogs.
* Optionally, implement tenant lifecycle management (disable, delete).

---
