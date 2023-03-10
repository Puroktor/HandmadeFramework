package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import freemarker.template.TemplateException;
import ru.vsu.csf.framework.frontend.UI;
import ru.vsu.csf.framework.frontend.UIComponent;
import ru.vsu.csf.framework.frontend.UIEndpoint;
import ru.vsu.csf.skofenko.server.Application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AngularUI implements UI {

    public static final String FRONTEND_DIR_NAME = "frontend";
    public static final String FRONTEND_LOGS_FILE = "frontend-log.txt";
    private final List<UIComponent> components = new ArrayList<>();
    private final File resourceFile;
    private final String baseUrl;

    public AngularUI(File resourceFile) {
        this.resourceFile = resourceFile;
        this.baseUrl = "http://localhost:%d/%s".formatted(Application.PORT, resourceFile.getName());
    }

    @Override
    public boolean addComponent(UIComponent uiComponent) {
        return components.add(uiComponent);
    }

    @Override
    public boolean create(boolean overrideUI) {
        if (components.isEmpty()) {
            return false;
        }
        try {
            File projectDir = new File(resourceFile, FRONTEND_DIR_NAME);
            if (projectDir.exists() && !overrideUI) {
                return true;
            }
            projectDir.mkdir();
            AngularProjectGenerator.createBaseProject(projectDir);
            AngularProjectGenerator.createProxyConfig(getBaseUrl(), projectDir);
            for (UIComponent component : components) {
                File componentDir = AngularProjectGenerator.createComponent(component, projectDir);
                for (UIEndpoint endpoint : component.getEndpoints()) {
                    AngularProjectGenerator.createEndpoint(endpoint, component, componentDir);
                }
            }
            AngularProjectGenerator.createRouting(components, projectDir);
            return true;
        } catch (IOException | TemplateException e) {
            throw new IllegalStateException("Couldn't create Angular project", e);
        }
    }

    @Override
    public void run() {
        try {
            File directory = new File(resourceFile, FRONTEND_DIR_NAME);
            File logs = new File(resourceFile, FRONTEND_LOGS_FILE);
            new ProcessBuilder("npm.cmd", "install").directory(directory).start().waitFor();
            Process process = new ProcessBuilder("ng.cmd", "serve")
                    .directory(directory)
                    .redirectOutput(logs)
                    .redirectError(logs)
                    .start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                process.toHandle().children().forEach(ProcessHandle::destroy);
                process.destroy();
            }));
        } catch (Exception e) {
            throw new RuntimeException("Error during frontend startup", e);
        }
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }
}
