package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import org.apache.commons.io.FileUtils;
import ru.vsu.csf.framework.frontend.UIComponent;
import ru.vsu.csf.skofenko.server.Application;

import java.io.File;
import java.util.List;

public class AngularProjectGenerator {

    public static void createBaseProject(File destinationProjectDir) throws Exception {
        File baseAngularProject = new File(Application.class.getResource("/angular-base").toURI());
        FileUtils.copyDirectory(baseAngularProject, destinationProjectDir, false);
    }

    public static void createComponent(UIComponent component, File projectDir) throws Exception {
        File componentDir = new File(projectDir, "src/app/%s".formatted(component.getFileName()));
        componentDir.mkdirs();

        File componentCSS = new File(componentDir, "%s.component.css".formatted(component.getFileName()));
        componentCSS.createNewFile();

        File componentTS = new File(componentDir, "%s.component.ts".formatted(component.getFileName()));
        AngularTemplateRenderer.renderComponentTS(componentTS, component);

        File componentHTML = new File(componentDir, "%s.component.html".formatted(component.getFileName()));
        componentHTML.createNewFile();
    }

    public static void createRouting(List<UIComponent> components, File projectDir) throws Exception {
        File routingModule = new File(projectDir, "src/app/app-routing.module.ts");
        AngularTemplateRenderer.renderRoutingModule(routingModule, components);

        AngularTemplateRenderer.renderComponentTS(new File(projectDir, "src/app/header/header.component.ts"), new AngularComponent("header"));
        AngularTemplateRenderer.renderHeaderHTML(new File(projectDir, "src/app/header/header.component.html"), components);
    }
}
