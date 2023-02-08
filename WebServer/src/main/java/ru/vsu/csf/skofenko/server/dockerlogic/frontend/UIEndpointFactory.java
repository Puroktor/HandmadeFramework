package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import ru.vsu.csf.framework.frontend.UIEndpoint;
import ru.vsu.csf.framework.frontend.UIParam;
import ru.vsu.csf.framework.frontend.UIRequestParam;
import ru.vsu.csf.framework.http.Param;
import ru.vsu.csf.framework.http.RequestBody;
import ru.vsu.csf.framework.http.RequestType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class UIEndpointFactory {

    public static UIEndpoint createEndpoint(String mapping, RequestType requestType, Method method) {
        List<UIRequestParam> requestParams = new ArrayList<>();
        Iterator<Class<?>> typeIterator = Arrays.stream(method.getParameterTypes()).iterator();
        for (Annotation[] annotations : method.getParameterAnnotations()) {
            Class<?> paramType = typeIterator.next();
            Optional<Param> optionalQueryParam = getAnnotation(annotations, Param.class);
            Optional<RequestBody> optionalRequestBody = getAnnotation(annotations, RequestBody.class);
            Optional<UIParam> optionalFrontParam = getAnnotation(annotations, UIParam.class);

            String paramName = optionalFrontParam.isPresent() ? optionalFrontParam.get().value() : paramType.getSimpleName();
            UIRequestParam.SubmissionType submissionType;
            if (optionalQueryParam.isPresent() ^ optionalRequestBody.isPresent()) {
                submissionType = optionalQueryParam.isPresent()
                        ? UIRequestParam.SubmissionType.QUERY_PARAM
                        : UIRequestParam.SubmissionType.REQUEST_BODY;
            } else {
                throw new IllegalStateException("Frontend param is neither query or request body parameter " + paramType);
            }

            requestParams.add(new UIRequestParam(paramName, paramType, submissionType));
        }
        return new UIEndpoint(mapping, requestType, requestParams, method.getReturnType());
    }

    private static <T extends Annotation> Optional<T> getAnnotation(Annotation[] annotations, Class<T> annotationClass) {
        return Arrays.stream(annotations)
                .filter(annotation -> annotation.annotationType().equals(annotationClass))
                .reduce((a, b) -> null)
                .map(annotation -> (T) annotation);
    }
}
