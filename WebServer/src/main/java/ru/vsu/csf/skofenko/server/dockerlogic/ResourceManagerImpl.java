package ru.vsu.csf.skofenko.server.dockerlogic;

import ru.vsu.csf.framework.di.ResourceManager;

import java.io.File;

public class ResourceManagerImpl implements ResourceManager {

    private final String basePath;

    public ResourceManagerImpl(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public String getPath(String name) {
        return "%s%s".formatted(basePath, name);
    }

    @Override
    public File getFile(String name) {
        return new File(getPath(name));
    }
}
