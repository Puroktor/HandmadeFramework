package ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.post;

import ru.vsu.csf.framework.di.Controller;
import ru.vsu.csf.framework.http.mapping.DeleteMapping;
import ru.vsu.csf.framework.http.mapping.GetMapping;
import ru.vsu.csf.framework.http.mapping.PostMapping;
import ru.vsu.csf.framework.http.mapping.PutMapping;
import ru.vsu.csf.skofenko.server.dockerlogic.Endpoint;
import ru.vsu.csf.skofenko.server.dockerlogic.EndpointManager;
import ru.vsu.csf.skofenko.server.dockerlogic.di.ApplicationContext;
import ru.vsu.csf.skofenko.server.http.request.RequestType;

import java.lang.reflect.Method;

public class ControllerPostProcessor implements PostProcessor {

    @Override
    public <T> T process(T bean, ApplicationContext applicationContext) {
        Class<?> clazz = bean.getClass();
        Controller annotation = clazz.getDeclaredAnnotation(Controller.class);
        if (annotation == null) {
            return bean;
        }
        for (Method method : clazz.getDeclaredMethods()) {
            GetMapping getMapping = method.getAnnotation(GetMapping.class);
            PostMapping postMapping = method.getAnnotation(PostMapping.class);
            PutMapping putMapping = method.getAnnotation(PutMapping.class);
            DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
            EndpointManager manager = applicationContext.getEndpointManager();
            if (getMapping != null) {
                manager.putEndpoint(RequestType.GET, "%s/%s".formatted(annotation.value(), getMapping.value()), new Endpoint(bean, method));
            } else if (postMapping != null) {
                manager.putEndpoint(RequestType.POST, "%s/%s".formatted(annotation.value(), postMapping.value()), new Endpoint(bean, method));
            } else if (putMapping != null) {
                manager.putEndpoint(RequestType.PUT, "%s/%s".formatted(annotation.value(), putMapping.value()), new Endpoint(bean, method));
            } else if (deleteMapping != null) {
                manager.putEndpoint(RequestType.DELETE, "%s/%s".formatted(annotation.value(), deleteMapping.value()), new Endpoint(bean, method));
            }
        }
        return bean;
    }
}
