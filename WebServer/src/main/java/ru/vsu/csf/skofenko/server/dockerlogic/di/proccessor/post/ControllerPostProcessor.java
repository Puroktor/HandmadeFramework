package ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.post;

import ru.vsu.csf.framework.di.Controller;
import ru.vsu.csf.framework.frontend.UIComponent;
import ru.vsu.csf.framework.frontend.UIEndpoint;
import ru.vsu.csf.framework.frontend.DisplayName;
import ru.vsu.csf.framework.http.RequestType;
import ru.vsu.csf.framework.http.mapping.DeleteMapping;
import ru.vsu.csf.framework.http.mapping.GetMapping;
import ru.vsu.csf.framework.http.mapping.PostMapping;
import ru.vsu.csf.framework.http.mapping.PutMapping;
import ru.vsu.csf.skofenko.server.dockerlogic.Endpoint;
import ru.vsu.csf.skofenko.server.dockerlogic.di.ApplicationContext;
import ru.vsu.csf.skofenko.server.dockerlogic.frontend.AngularComponent;
import ru.vsu.csf.skofenko.server.dockerlogic.frontend.UIEndpointFactory;

import java.lang.reflect.Method;

public class ControllerPostProcessor implements PostProcessor {

    @Override
    public <T> T process(T bean, ApplicationContext applicationContext) {
        Class<?> clazz = bean.getClass();
        Controller annotation = clazz.getDeclaredAnnotation(Controller.class);
        if (annotation == null) {
            return bean;
        }
        UIComponent component = null;
        if (annotation.generateUI()) {
            DisplayName controllerName = clazz.getDeclaredAnnotation(DisplayName.class);
            component = new AngularComponent(controllerName != null ? controllerName.value() : clazz.getSimpleName());
        }
        for (Method method : clazz.getDeclaredMethods()) {
            GetMapping getMapping = method.getAnnotation(GetMapping.class);
            PostMapping postMapping = method.getAnnotation(PostMapping.class);
            PutMapping putMapping = method.getAnnotation(PutMapping.class);
            DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
            String mapping = null;
            RequestType requestType = null;
            if (getMapping != null) {
                mapping = "%s/%s".formatted(annotation.value(), getMapping.value());
                requestType = RequestType.GET;
            } else if (postMapping != null) {
                mapping = "%s/%s".formatted(annotation.value(), postMapping.value());
                requestType = RequestType.POST;
            } else if (putMapping != null) {
                mapping = "%s/%s".formatted(annotation.value(), putMapping.value());
                requestType = RequestType.PUT;
            } else if (deleteMapping != null) {
                mapping = "%s/%s".formatted(annotation.value(), deleteMapping.value());
                requestType = RequestType.DELETE;
            }
            if (mapping != null) {
                applicationContext.getEndpointManager().putEndpoint(requestType, mapping, new Endpoint(bean, method));
                if (annotation.generateUI()) {
                    UIEndpoint uiEndpoint = UIEndpointFactory.createEndpoint(mapping, requestType, method);
                    component.addEndpoint(uiEndpoint);
                }
            }
        }
        if (annotation.generateUI()) {
            applicationContext.getUI().addComponent(component);
        }
        return bean;
    }
}
