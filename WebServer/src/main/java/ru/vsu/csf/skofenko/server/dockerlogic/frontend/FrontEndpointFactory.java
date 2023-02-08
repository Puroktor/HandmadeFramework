package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import ru.vsu.csf.framework.frontend.FrontEndpoint;
import ru.vsu.csf.framework.frontend.FrontParam;
import ru.vsu.csf.framework.frontend.FrontRequestParam;
import ru.vsu.csf.framework.http.Param;
import ru.vsu.csf.framework.http.RequestBody;
import ru.vsu.csf.framework.http.RequestType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class FrontEndpointFactory {

    public static FrontEndpoint createEndpoint(String mapping, RequestType requestType, Method method) {
        List<FrontRequestParam> requestParams = new ArrayList<>();
        Iterator<Class<?>> typeIterator = Arrays.stream(method.getParameterTypes()).iterator();
        for (Annotation[] annotations : method.getParameterAnnotations()) {
            Class<?> paramType = typeIterator.next();
            Optional<Param> optionalQueryParam = getAnnotation(annotations, Param.class);
            Optional<RequestBody> optionalRequestBody = getAnnotation(annotations, RequestBody.class);
            Optional<FrontParam> optionalFrontParam = getAnnotation(annotations, FrontParam.class);

            String paramName = optionalFrontParam.isPresent() ? optionalFrontParam.get().value() : paramType.getSimpleName();
            FrontRequestParam.SubmissionType submissionType;
            if (optionalQueryParam.isPresent() ^ optionalRequestBody.isPresent()) {
                submissionType = optionalQueryParam.isPresent()
                        ? FrontRequestParam.SubmissionType.QUERY_PARAM
                        : FrontRequestParam.SubmissionType.REQUEST_BODY;
            } else {
                throw new IllegalStateException("Frontend param is neither query or request body parameter " + paramType);
            }

            requestParams.add(new FrontRequestParam(paramName, paramType, submissionType));
        }
        return new FrontEndpoint(mapping, requestType, requestParams, method.getReturnType());
    }

    private static <T extends Annotation> Optional<T> getAnnotation(Annotation[] annotations, Class<T> annotationClass) {
        return Arrays.stream(annotations)
                .filter(annotation -> annotation.annotationType().equals(annotationClass))
                .reduce((a, b) -> null)
                .map(annotation -> (T) annotation);
    }
}
