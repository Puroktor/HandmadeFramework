package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import ru.vsu.csf.framework.frontend.UIComponent;
import ru.vsu.csf.framework.frontend.UI;
import ru.vsu.csf.skofenko.server.AppProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AngularUI implements UI {

    public static final String FRONTEND_DIR_NAME = "frontend";
    public static final String FRONTEND_LOGS_FILE = "frontend-log.txt";
    private final List<UIComponent> components = new ArrayList<>();
    private final File resourceFile;

    public AngularUI(File resourceFile) {
        this.resourceFile = resourceFile;
    }

    @Override
    public boolean addComponent(UIComponent uiComponent) {
        return components.add(uiComponent);
    }

    @Override
    public boolean create() {
        if (components.isEmpty()) {
            return false;
        }
        try {
            File projectDir = new File(resourceFile, FRONTEND_DIR_NAME);
            boolean shouldOverride = "true".equals(AppProperties.get("override-frontend"));
            if (projectDir.exists() && !shouldOverride) {
                return true;
            }
            projectDir.mkdir();
            AngularProjectGenerator.createBaseProject(projectDir);
            for (UIComponent component : components) {
                AngularProjectGenerator.createComponent(component, projectDir);
            }
            AngularProjectGenerator.createRouting(components, projectDir);
            return true;
        } catch (Exception e) {
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
}
