package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import org.apache.commons.io.FileUtils;
import ru.vsu.csf.framework.frontend.FrontComponent;
import ru.vsu.csf.skofenko.server.Application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class AngularProjectFactory {

    public static void createBaseProject(File destinationProjectDir) throws Exception {
        File baseAngularProject = new File(Application.class.getResource("/angular-base").toURI());
        FileUtils.copyDirectory(baseAngularProject, destinationProjectDir, false);
    }

    public static void createComponent(FrontComponent component, File projectDir) throws IOException {
        File componentDir = new File(projectDir, "src/app/%s".formatted(component.getFileName()));
        componentDir.mkdirs();

        File componentCSS = new File(componentDir, "%s.component.css".formatted(component.getFileName()));
        componentCSS.createNewFile();

        File componentTS = new File(componentDir, "%s.component.ts".formatted(component.getFileName()));
        componentTS.createNewFile();

        File componentHTML = new File(componentDir, "%s.component.html".formatted(component.getFileName()));
        componentHTML.createNewFile();
    }

    public static void createRouting(List<FrontComponent> components, File projectDir) throws IOException {
        File routingModule = new File(projectDir, "src/app/app-routing.module.ts");
        StringBuilder importsBuilder = new StringBuilder();
        for (FrontComponent component : components) {
            String componentImport = format(AngularTemplates.COMPONENT_IMPORT, component.getScriptName(), component.getFileName());
            importsBuilder.append(componentImport);
        }
        StringBuilder routesBuilder = new StringBuilder();
        for (FrontComponent component : components) {
            String route = format(AngularTemplates.ROUTE, component.getFileName(), component.getScriptName());
            routesBuilder.append(route);
        }
        String moduleText = format(AngularTemplates.ROUTING_MODULE, importsBuilder, routesBuilder,
                components.stream().map(comp -> comp.getScriptName() + AngularTemplates.COMPONENT_POSTFIX).collect(Collectors.joining(", ")));
        Files.writeString(routingModule.toPath(), moduleText, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
