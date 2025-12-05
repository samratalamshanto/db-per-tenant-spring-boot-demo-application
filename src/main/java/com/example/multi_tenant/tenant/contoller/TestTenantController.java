package com.example.multi_tenant.tenant.contoller;

import com.example.multi_tenant.tenant.entity.TestEntity;
import com.example.multi_tenant.tenant.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class TestTenantController {
    private final TestService testService;

    @GetMapping("/all")
    public List<TestEntity> getAllTestEntities() {
        return testService.getAllTestEntities();
    }
}
