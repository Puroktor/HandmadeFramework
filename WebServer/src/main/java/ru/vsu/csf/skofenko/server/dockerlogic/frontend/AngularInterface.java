package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import ru.vsu.csf.framework.frontend.FrontComponent;
import ru.vsu.csf.framework.frontend.FrontInterface;
import ru.vsu.csf.skofenko.server.AppProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AngularInterface implements FrontInterface {

    public static final String FRONTEND_DIR_NAME = "frontend";
    public static final String FRONTEND_LOGS_FILE = "frontend-log.txt";
    private final List<FrontComponent> components = new ArrayList<>();
    private final File resourceFile;

    public AngularInterface(File resourceFile) {
        this.resourceFile = resourceFile;
    }

    @Override
    public boolean addComponent(FrontComponent frontComponent) {
        return components.add(frontComponent);
    }

    @Override
    public boolean createProject() {
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
            AngularProjectFactory.createBaseProject(projectDir);
            for (FrontComponent component : components) {
                AngularProjectFactory.createComponent(component, projectDir);
            }
            AngularProjectFactory.createRouting(components, projectDir);
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
