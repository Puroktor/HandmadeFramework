package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import org.apache.commons.lang3.ClassUtils;
import ru.vsu.csf.framework.frontend.*;
import ru.vsu.csf.framework.http.Param;
import ru.vsu.csf.framework.http.RequestBody;
import ru.vsu.csf.framework.http.RequestType;

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

            String paramName = getFieldName(parameter, parameter.getType(), parameter.getName());
            if (paramAnnotation != null) {
                UIField uiField = new UIField(paramName, paramAnnotation.value(), getFieldUIType(parameter.getType()), true);
                queryParams.add(uiField);
            } else if (requestBodyAnnotation != null) {
                List<UIField> fields = Arrays.stream(parameter.getType().getDeclaredFields())
                        .map(field -> new UIField(getFieldName(field, field.getType(), field.getName()), field.getName(),
                                getFieldUIType(field.getType()), true))
                        .toList();
                requestBody = new UIRequestBody(paramName, fields);
            } else {
                throw new IllegalStateException("Frontend param is neither query or request body parameter " + parameter);
            }
        }
        return new UIEndpoint(method.getName(), methodDisplayName, mapping, requestType, queryParams, requestBody);
    }

    private static String getFieldName(AnnotatedElement element, Class<?> type, String codeName) {
        DisplayName nameAnnotation = element.getDeclaredAnnotation(DisplayName.class);
        if (nameAnnotation == null) {
            DisplayName classNameAnnotation = type.getDeclaredAnnotation(DisplayName.class);
            return classNameAnnotation == null ? codeName : classNameAnnotation.value();
        } else {
            return nameAnnotation.value();
        }
    }

    private static UIField.Type getFieldUIType(Class<?> filedClass) {
        filedClass = ClassUtils.primitiveToWrapper(filedClass);
        if (Number.class.isAssignableFrom(filedClass)) {
            return UIField.Type.NUMBER;
        } else if (Boolean.class.isAssignableFrom(filedClass)) {
            return UIField.Type.BOOL;
        } else {
            return UIField.Type.TEXT;
        }
    }
}
