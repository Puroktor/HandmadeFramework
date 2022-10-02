package ru.vsu.csf.skofenko.testapplication.config;

import org.postgresql.ds.PGConnectionPoolDataSource;
import ru.vsu.csf.framework.di.Bean;
import ru.vsu.csf.framework.di.Config;

@Config
public class DatabaseConfig {
    @Bean
    public PGConnectionPoolDataSource getDataSource() {
        PGConnectionPoolDataSource dataSource = new PGConnectionPoolDataSource();
        dataSource.setURL("jdbc:postgresql://localhost/testing_system_db");
        dataSource.setUser("testing_system_user");
        dataSource.setPassword("a,Pf{`?-vaP6Q;ya");
        return dataSource;
    }
}
