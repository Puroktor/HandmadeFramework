package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class AngularTemplates {

    private static final String BASE_PATH = "/angular-templates/";
    public static final String COMPONENT_POSTFIX = "Component";
    public static final String ROUTING_MODULE = readTemplate("routing/routing-module.te");
    public static final String COMPONENT_IMPORT = readTemplate("routing/component-import.te");
    public static final String ROUTE = readTemplate("routing/route.te");

    private static String readTemplate(String path) {
        try {
            URI uri = AngularTemplates.class.getResource("%s%s".formatted(BASE_PATH, path)).toURI();
            return Files.readString(Path.of(uri));
        } catch (Exception e) {
            throw new RuntimeException("Error during reading Angular template", e);
        }
    }
}
