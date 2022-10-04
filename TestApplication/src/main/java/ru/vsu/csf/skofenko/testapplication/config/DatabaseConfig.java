package ru.vsu.csf.skofenko.testapplication.config;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.vsu.csf.framework.di.Bean;
import ru.vsu.csf.framework.di.Config;

import javax.sql.DataSource;

@Config
public class DatabaseConfig {
    @Bean
    public DataSource getDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:postgresql://localhost/testing_system_db");
        ds.setUsername("testing_system_user");
        ds.setPassword("a,Pf{`?-vaP6Q;ya");
        return ds;
    }
}
