package ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.post;

import ru.vsu.csf.framework.di.Controller;
import ru.vsu.csf.framework.http.mapping.DeleteMapping;
import ru.vsu.csf.framework.http.mapping.GetMapping;
import ru.vsu.csf.framework.http.mapping.PostMapping;
import ru.vsu.csf.framework.http.mapping.PutMapping;
import ru.vsu.csf.skofenko.server.dockerlogic.Endpoint;
import ru.vsu.csf.skofenko.server.dockerlogic.di.ApplicationContext;

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
            if (getMapping != null) {
                applicationContext.getEndpointManager().putGetPoint("%s/%s".formatted(annotation.value(), getMapping.value()), new Endpoint(bean, method));
            } else if (postMapping != null) {
                applicationContext.getEndpointManager().putPostPoint("%s/%s".formatted(annotation.value(), postMapping.value()), new Endpoint(bean, method));
            } else if (putMapping != null) {
                applicationContext.getEndpointManager().putPutPoint("%s/%s".formatted(annotation.value(), putMapping.value()), new Endpoint(bean, method));
            } else if (deleteMapping != null) {
                applicationContext.getEndpointManager().putDeletePoint("%s/%s".formatted(annotation.value(), deleteMapping.value()), new Endpoint(bean, method));
            }
        }
        return bean;
    }
}
