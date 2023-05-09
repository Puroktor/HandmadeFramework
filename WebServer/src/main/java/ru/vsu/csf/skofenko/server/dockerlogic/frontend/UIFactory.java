package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import ru.vsu.csf.framework.di.Controller;
import ru.vsu.csf.framework.frontend.DisplayName;
import ru.vsu.csf.skofenko.ui.generator.api.core.UIComponent;
import ru.vsu.csf.skofenko.ui.generator.api.core.UIEndpoint;
import ru.vsu.csf.framework.http.RequestType;
import ru.vsu.csf.skofenko.server.dockerlogic.Endpoint;
import ru.vsu.csf.skofenko.server.dockerlogic.EndpointManager;
import ru.vsu.csf.skofenko.ui.generator.core.AngularComponent;
import ru.vsu.csf.skofenko.ui.generator.core.AngularUI;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class UIFactory {
    public static AngularUI createAngularUI(String baseUrl, Path resourcePath, EndpointManager endpointManager) {
        Map<RequestType, Map<String, Endpoint>> endpointMap = endpointManager.getEndpointsMap();
        Map<Class<?>, UIComponent> components = new HashMap<>();
        for (Map.Entry<RequestType, Map<String, Endpoint>> requestTypeEntry : endpointMap.entrySet()) {
            for (Map.Entry<String, Endpoint> endpointEntry : requestTypeEntry.getValue().entrySet()) {
                Class<?> clazz = endpointEntry.getValue().instance().getClass();
                Controller annotation = clazz.getDeclaredAnnotation(Controller.class);
                if (annotation.generateUI()) {
                    UIComponent component = components.get(clazz);
                    if (component == null) {
                        DisplayName controllerName = clazz.getDeclaredAnnotation(DisplayName.class);
                        component = new AngularComponent(controllerName != null ? controllerName.value() : clazz.getSimpleName());
                        components.put(clazz, component);
                    }
                    UIEndpoint uiEndpoint = UIEndpointFactory.createEndpoint(endpointEntry.getKey(), requestTypeEntry.getKey(), endpointEntry.getValue().method());
                    component.addEndpoint(uiEndpoint);
                }
            }
        }
        return new AngularUI(baseUrl, resourcePath, components.values());
    }
}
