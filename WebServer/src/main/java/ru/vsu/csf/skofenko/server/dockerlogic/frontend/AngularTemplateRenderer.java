package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import ru.vsu.csf.framework.frontend.UIComponent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AngularTemplateRenderer {

    private static final Configuration CONFIGURATION = new Configuration(Configuration.VERSION_2_3_22);

    static {
        ClassTemplateLoader loader = new ClassTemplateLoader(AngularTemplateRenderer.class, "/angular-templates");
        CONFIGURATION.setTemplateLoader(loader);
        CONFIGURATION.setDefaultEncoding("UTF-8");
        CONFIGURATION.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public static void renderHeaderHTML(File destinationFile, List<UIComponent> uiComponents) throws TemplateException, IOException {
        Template routingModuleTemplate = CONFIGURATION.getTemplate("header-html.ftl");
        Map<String, Object> variablesMap = Map.of("components", uiComponents);
        renderTemplate(destinationFile, routingModuleTemplate, variablesMap);
    }

    public static void renderComponentTS(File destinationFile, UIComponent uiComponent) throws TemplateException, IOException {
        Template routingModuleTemplate = CONFIGURATION.getTemplate("component-ts.ftl");
        Map<String, Object> variablesMap = Map.of("component", uiComponent);
        renderTemplate(destinationFile, routingModuleTemplate, variablesMap);
    }

    public static void renderComponentHTML(File destinationFile, UIComponent uiComponent) throws TemplateException, IOException {
        Template routingModuleTemplate = CONFIGURATION.getTemplate("component-html.ftl");
        Map<String, Object> variablesMap = Map.of("component", uiComponent);
        renderTemplate(destinationFile, routingModuleTemplate, variablesMap);
    }

    public static void renderRoutingModule(File destinationFile, List<UIComponent> uiComponents) throws TemplateException, IOException {
        Template routingModuleTemplate = CONFIGURATION.getTemplate("routing-module.ftl");
        Map<String, Object> variablesMap = Map.of("components", uiComponents);
        renderTemplate(destinationFile, routingModuleTemplate, variablesMap);
    }

    public static void renderProxyConfig(File destinationFile, String baseUrl) throws TemplateException, IOException {
        Template proxyConfigTemplate = CONFIGURATION.getTemplate("proxy-config-json.ftl");
        Map<String, Object> variablesMap = Map.of("baseUrl", baseUrl);
        renderTemplate(destinationFile, proxyConfigTemplate, variablesMap);
    }

    private static void renderTemplate(File destinationFile, Template template, Map<String, Object> variablesMap) throws TemplateException, IOException {
        try (FileWriter fileWriter = new FileWriter(destinationFile)) {
            template.process(variablesMap, fileWriter);
        }
    }
}
