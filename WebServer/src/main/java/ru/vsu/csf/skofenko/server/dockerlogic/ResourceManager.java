package ru.vsu.csf.skofenko.server.dockerlogic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ResourceManager {

    private static final Map<Class<?>, String> resourcePathMap = new HashMap<>();

    public static String put(Class<?> clas, String path) {
        return resourcePathMap.put(clas, path);
    }

    public static File get(Class<?> clas, String file) {
        return new File(resourcePathMap.get(clas) + "/" + file);
    }
}
