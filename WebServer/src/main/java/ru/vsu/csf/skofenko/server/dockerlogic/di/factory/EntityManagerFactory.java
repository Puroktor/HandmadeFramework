package ru.vsu.csf.skofenko.server.dockerlogic.di.factory;

import ru.vsu.csf.framework.persistence.BaseDataSource;
import ru.vsu.csf.skofenko.server.dockerlogic.di.ApplicationContext;
import ru.vsu.csf.skofenko.server.persistance.EntityManagerImpl;

import java.util.Optional;

public class EntityManagerFactory implements StandardBeanFactory {
    @Override
    public Optional<?> createBean(ApplicationContext applicationContext) {
        BaseDataSource dataSource = null;
        for (Object bean : applicationContext.getBeans()) {
            if (bean instanceof BaseDataSource) {
                if (dataSource != null) {
                    throw new IllegalStateException("Application has several data sources");
                } else {
                    dataSource = (BaseDataSource) bean;
                }
            }
        }
        if (dataSource != null) {
            return Optional.of(new EntityManagerImpl(dataSource));
        }
        return Optional.empty();
    }
}
