package com.example.multi_tenant.tenant.service;

import com.example.multi_tenant.tenant.entity.TestEntity;
import com.example.multi_tenant.tenant.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestServiceImpl implements TestService {
    private final TestRepository testRepository;

    @Override
    public List<TestEntity> getAllTestEntities() {
        return testRepository.findAll();
    }
}
