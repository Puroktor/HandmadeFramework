package ru.vsu.csf.skofenko.server.dockerlogic.di.factory;

import ru.vsu.csf.skofenko.server.dockerlogic.ResourceManagerImpl;
import ru.vsu.csf.skofenko.server.dockerlogic.di.ApplicationContext;
import ru.vsu.csf.skofenko.server.dockerlogic.di.ContextLoader;

import java.io.File;
import java.util.Optional;

public class ResourceManagerFactory implements StandardBeanFactory {
    @Override
    public Optional<?> createBean(ApplicationContext applicationContext) {
        File resourceFile = ContextLoader.getResourceFile(applicationContext.getJarFile());
        return Optional.of(new ResourceManagerImpl(resourceFile.getPath() + "\\"));
    }
}
