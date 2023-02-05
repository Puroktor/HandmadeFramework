package ru.vsu.csf.skofenko.server.dockerlogic.di.factory;

import ru.vsu.csf.skofenko.server.dockerlogic.di.ApplicationContext;

import java.util.Optional;

public interface StandardBeanFactory {
    Optional<?> createBean(ApplicationContext applicationContext);
}
