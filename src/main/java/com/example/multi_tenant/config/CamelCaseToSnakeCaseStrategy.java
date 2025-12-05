package com.example.multi_tenant.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class CamelCaseToSnakeCaseStrategy extends PhysicalNamingStrategyStandardImpl {

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        if (name == null) return null;
        String regex = "([a-z])([A-Z])";
        String newName = name.getText().replaceAll(regex, "$1_$2").toLowerCase();
        return Identifier.toIdentifier(newName);
    }
}