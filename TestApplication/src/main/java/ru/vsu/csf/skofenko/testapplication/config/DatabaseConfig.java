package ru.vsu.csf.skofenko.testapplication.config;

import ru.vsu.csf.framework.di.Bean;
import ru.vsu.csf.framework.di.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Config
public class DatabaseConfig {
    @Bean
    public Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost/testing_system_db";
        String user = "testing_system_user";
        String passwd = "a,Pf{`?-vaP6Q;ya";
        return DriverManager.getConnection(url, user, passwd);
    }
}
