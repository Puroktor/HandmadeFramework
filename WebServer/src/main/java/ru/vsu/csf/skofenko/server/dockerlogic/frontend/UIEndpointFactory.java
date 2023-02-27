package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import ru.vsu.csf.framework.frontend.*;
import ru.vsu.csf.framework.http.Param;
import ru.vsu.csf.framework.http.RequestBody;
import ru.vsu.csf.framework.http.RequestType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class UIEndpointFactory {

    public static UIEndpoint createEndpoint(String mapping, RequestType requestType, Method method) {
        List<UIQueryParam> queryParams = new ArrayList<>();
        UIRequestBody requestBody = null;
        Iterator<Parameter> parameterIterator = Arrays.stream(method.getParameters()).iterator();
        for (Annotation[] annotations : method.getParameterAnnotations()) {
            Parameter parameter = parameterIterator.next();
            Optional<Param> optionalQueryParam = getAnnotation(annotations, Param.class);
            Optional<RequestBody> optionalRequestBody = getAnnotation(annotations, RequestBody.class);
            Optional<UIName> optionalNameParam = getAnnotation(annotations, UIName.class);

            String paramName = optionalNameParam.isPresent() ? optionalNameParam.get().value() : parameter.getName();
            if (optionalQueryParam.isPresent()) {
                queryParams.add(new UIQueryParam(optionalQueryParam.get().value(), new UIField(paramName, UIField.Type.TEXT)));
            } else if (optionalRequestBody.isPresent()) {
                List<UIField> fields = Arrays.stream(parameter.getType().getDeclaredFields())
                        .map(field -> new UIField(field.getName(), UIField.Type.TEXT))
                        .toList();
                requestBody = new UIRequestBody(paramName, fields);
            } else {
                throw new IllegalStateException("Frontend param is neither query or request body parameter " + parameter);
            }
        }
        return new UIEndpoint(method.getName(), mapping, requestType, queryParams, requestBody, method.getReturnType());
    }

    private static <T extends Annotation> Optional<T> getAnnotation(Annotation[] annotations, Class<T> annotationClass) {
        return Arrays.stream(annotations)
                .filter(annotation -> annotation.annotationType().equals(annotationClass))
                .reduce((a, b) -> null)
                .map(annotation -> (T) annotation);
    }
}
