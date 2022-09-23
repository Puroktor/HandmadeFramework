package ru.vsu.csf.skofenko.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProperties {

    private static final Properties properties;

    static {
        try {
            InputStream is = WebServerApplication.class.getResourceAsStream("/config.properties");
            properties = new Properties();
            properties.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("No config!", e);
        }
    }

    public static String get(String prop) {
        return properties.getProperty(prop);
    }
}
