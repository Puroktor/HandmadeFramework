package ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.post;

import ru.vsu.csf.framework.di.Controller;
import ru.vsu.csf.framework.frontend.FrontComponent;
import ru.vsu.csf.framework.frontend.FrontEndpoint;
import ru.vsu.csf.framework.http.RequestType;
import ru.vsu.csf.framework.http.mapping.DeleteMapping;
import ru.vsu.csf.framework.http.mapping.GetMapping;
import ru.vsu.csf.framework.http.mapping.PostMapping;
import ru.vsu.csf.framework.http.mapping.PutMapping;
import ru.vsu.csf.skofenko.server.dockerlogic.Endpoint;
import ru.vsu.csf.skofenko.server.dockerlogic.di.ApplicationContext;
import ru.vsu.csf.skofenko.server.dockerlogic.frontend.AngularComponent;
import ru.vsu.csf.skofenko.server.dockerlogic.frontend.FrontEndpointFactory;

import java.lang.reflect.Method;

public class ControllerPostProcessor implements PostProcessor {

    @Override
    public <T> T process(T bean, ApplicationContext applicationContext) {
        Class<?> clazz = bean.getClass();
        Controller annotation = clazz.getDeclaredAnnotation(Controller.class);
        if (annotation == null) {
            return bean;
        }
        FrontComponent component = null;
        if (annotation.generateUI()) {
            component = new AngularComponent(clazz.getSimpleName());
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
                    FrontEndpoint frontEndpoint = FrontEndpointFactory.createEndpoint(mapping, requestType, method);
                    component.addEndpoint(frontEndpoint);
                }
            }
        }
        if (annotation.generateUI()) {
            applicationContext.getFrontInterface().addComponent(component);
        }
        return bean;
    }
}
