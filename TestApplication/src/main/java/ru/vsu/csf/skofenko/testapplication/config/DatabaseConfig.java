package ru.vsu.csf.skofenko.testapplication.config;

import ru.vsu.csf.framework.di.Bean;
import ru.vsu.csf.framework.di.Config;
import ru.vsu.csf.framework.persistence.BaseDataSource;

@Config
public class DatabaseConfig {
    @Bean
    public BaseDataSource getDataSource() {
        return new BaseDataSource(
                "jdbc:postgresql://localhost/testing_system_db",
                "testing_system_user",
                "a,Pf{`?-vaP6Q;ya"
        );
    }
}
