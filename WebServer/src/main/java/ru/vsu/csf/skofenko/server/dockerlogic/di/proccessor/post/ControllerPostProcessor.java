package ru.vsu.csf.skofenko.server.dockerlogic.di.proccessor.post;

import org.apache.commons.lang3.StringUtils;
import ru.vsu.csf.framework.di.Controller;
import ru.vsu.csf.framework.http.RequestType;
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
            String mapping = null;
            RequestType requestType = null;
            if (getMapping != null) {
                mapping = createMapping(annotation.value(), getMapping.value());
                requestType = RequestType.GET;
            } else if (postMapping != null) {
                mapping = createMapping(annotation.value(), postMapping.value());
                requestType = RequestType.POST;
            } else if (putMapping != null) {
                mapping = createMapping(annotation.value(), putMapping.value());
                requestType = RequestType.PUT;
            } else if (deleteMapping != null) {
                mapping = createMapping(annotation.value(), deleteMapping.value());
                requestType = RequestType.DELETE;
            }
            if (mapping != null) {
                applicationContext.getEndpointManager().putEndpoint(requestType, mapping, new Endpoint(bean, method));
            }
        }
        return bean;
    }

    private String createMapping(String baseMapping, String endpointMapping) {
        return StringUtils.isBlank(endpointMapping) ? baseMapping : "%s/%s".formatted(baseMapping, endpointMapping);
    }
}
