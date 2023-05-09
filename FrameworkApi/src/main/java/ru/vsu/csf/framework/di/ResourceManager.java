package ru.vsu.csf.framework.di;

import java.io.File;

public interface ResourceManager {
    String getPath(String name);

    File getFile(String name);
}
