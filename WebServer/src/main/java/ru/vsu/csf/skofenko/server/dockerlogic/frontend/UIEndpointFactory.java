package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import org.apache.commons.lang3.ClassUtils;
import ru.vsu.csf.framework.frontend.*;
import ru.vsu.csf.framework.frontend.field.UIField;
import ru.vsu.csf.framework.http.Param;
import ru.vsu.csf.framework.http.RequestBody;
import ru.vsu.csf.framework.http.RequestType;
import ru.vsu.csf.skofenko.server.dockerlogic.frontend.field.AngularUIField;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class UIEndpointFactory {

    public static UIEndpoint createEndpoint(String mapping, RequestType requestType, Method method) {
        List<UIField> queryParams = new ArrayList<>();
        UIRequestBody requestBody = null;
        DisplayName nameAnnotation = method.getDeclaredAnnotation(DisplayName.class);
        String methodDisplayName = nameAnnotation == null ? method.getName() : nameAnnotation.value();
        for (Parameter parameter : method.getParameters()) {
            Param paramAnnotation = parameter.getDeclaredAnnotation(Param.class);
            RequestBody requestBodyAnnotation = parameter.getDeclaredAnnotation(RequestBody.class);

            if (paramAnnotation != null) {
                UIField uiField = UIFieldFactory.createUIField(parameter, parameter.getType(), paramAnnotation.value());
                queryParams.add(uiField);
            } else if (requestBodyAnnotation != null) {
                List<UIField> fields = Arrays.stream(parameter.getType().getDeclaredFields())
                        .map(field -> UIFieldFactory.createUIField(field, field.getType(), field.getName()))
                        .toList();
                String bodyName = UIFieldFactory.getFieldDisplayName(parameter, parameter.getType(), parameter.getName());
                requestBody = new UIRequestBody(bodyName, fields);
            } else {
                throw new IllegalStateException("Frontend param is neither query or request body parameter " + parameter);
            }
        }
        return new UIEndpoint(method.getName(), methodDisplayName, mapping, requestType, queryParams, requestBody);
    }
}
