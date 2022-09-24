package ru.vsu.csf.skofenko.testapplication.config;

import ru.vsu.csf.framework.di.Bean;
import ru.vsu.csf.framework.di.Config;
import ru.vsu.csf.skofenko.testapplication.mapper.AttemptMapper;
import ru.vsu.csf.skofenko.testapplication.mapper.TestMapper;
import ru.vsu.csf.skofenko.testapplication.mapper.UserMapper;

@Config
public class MappersConfig {

    @Bean
    public AttemptMapper attemptMapper() {
        return new AttemptMapper();
    }

    @Bean
    public TestMapper testMapper() {
        return new TestMapper();
    }

    @Bean
    public UserMapper userMapper() {
        return new UserMapper();
    }
}
