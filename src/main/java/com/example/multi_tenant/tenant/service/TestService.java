package com.example.multi_tenant.tenant.service;

import com.example.multi_tenant.tenant.entity.TestEntity;

import java.util.List;

public interface TestService {

    List<TestEntity> getAllTestEntities();
}
