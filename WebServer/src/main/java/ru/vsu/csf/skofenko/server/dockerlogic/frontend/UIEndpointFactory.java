package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import lombok.experimental.UtilityClass;
import ru.vsu.csf.framework.frontend.DisplayName;
import ru.vsu.csf.skofenko.ui.generator.api.core.UIEndpoint;
import ru.vsu.csf.skofenko.ui.generator.api.core.UIRequestBody;
import ru.vsu.csf.skofenko.ui.generator.api.core.UIRequestType;
import ru.vsu.csf.skofenko.ui.generator.api.field.UIField;
import ru.vsu.csf.framework.http.Param;
import ru.vsu.csf.framework.http.RequestBody;
import ru.vsu.csf.framework.http.RequestType;
import ru.vsu.csf.skofenko.ui.generator.core.AngularEndpoint;
import ru.vsu.csf.skofenko.ui.generator.core.AngularRequestBody;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class UIEndpointFactory {

    public UIEndpoint createEndpoint(String mapping, RequestType requestType, Method method) {
        List<UIField> queryParams = new ArrayList<>();
        UIRequestBody requestBody = null;
        DisplayName nameAnnotation = method.getDeclaredAnnotation(DisplayName.class);
        String methodDisplayName = nameAnnotation == null ? method.getName() : nameAnnotation.value();
        for (Parameter parameter : method.getParameters()) {
            Param paramAnnotation = parameter.getDeclaredAnnotation(Param.class);
            RequestBody requestBodyAnnotation = parameter.getDeclaredAnnotation(RequestBody.class);
            if (paramAnnotation != null) {
                UIField uiField = UIFieldFactory.createUIField(parameter, parameter.getParameterizedType(), paramAnnotation.value());
                queryParams.add(uiField);
            } else if (requestBodyAnnotation != null) {
                List<UIField> fields = getUIFields(parameter.getType());
                String bodyName = UIFieldFactory.getFieldDisplayName(parameter, parameter.getParameterizedType(), parameter.getName());
                requestBody = new AngularRequestBody(bodyName, fields);
            } else {
                throw new IllegalStateException("Frontend param is neither query or request body parameter " + parameter);
            }
        }
        UIRequestType uiRequestType = RequestTypeUtil.toUIType(requestType);
        List<UIField> responseFields = getUIFields(method.getReturnType());
        return new AngularEndpoint(methodDisplayName, mapping, uiRequestType, queryParams, requestBody, responseFields);
    }

    private List<UIField> getUIFields(Class<?> typeClass) {
        return Arrays.stream(typeClass.getDeclaredFields())
                .map(field -> UIFieldFactory.createUIField(field, field.getGenericType(), field.getName()))
                .toList();
    }
}
