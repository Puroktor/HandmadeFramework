package ru.vsu.csf.skofenko.server.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.vsu.csf.framework.http.*;
import ru.vsu.csf.skofenko.server.dockerlogic.ApplicationContext;
import ru.vsu.csf.skofenko.server.dockerlogic.Endpoint;
import ru.vsu.csf.skofenko.server.http.request.HttpRequest;
import ru.vsu.csf.skofenko.server.http.request.RequestType;
import ru.vsu.csf.skofenko.server.http.response.HttpResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarFile;

public class Servlet {

    private final ApplicationContext applicationContext;

    public Servlet(JarFile jar) {
        this.applicationContext = new ApplicationContext(jar);
    }

    private String parseMapping(HttpRequest request) {
        String[] parts = request.getPath().split("/", 3);
        String mapping = "";
        if (parts.length == 3) {
            mapping = parts[2];
        }
        if (mapping.endsWith("/")) {
            mapping = mapping.substring(0, mapping.length() - 1);
        }
        return mapping;
    }

    private Object[] parseParams(Method method, HttpRequest request, ObjectMapper mapper) throws IOException {
        Map<String, String> requestParams = request.getParams();
        List<Object> params = new ArrayList<>();
        Iterator<Class<?>> typeIterator = Arrays.stream(method.getParameterTypes()).iterator();
        for (Annotation[] annotations : method.getParameterAnnotations()) {
            Class<?> paramType = typeIterator.next();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(Param.class)) {
                    String str = requestParams.get(((Param) annotation).value());
                    if (str == null)
                        continue;
                    Object object = paramType == String.class ? str : mapper.readValue(str, paramType);
                    params.add(object);
                } else if (annotation.annotationType().equals(RequestBody.class)) {
                    Object object = mapper.readValue(request.getBody(), paramType);
                    params.add(object);
                }
            }
        }
        return params.toArray();
    }

    public void doResponse(HttpRequest request, HttpResponse response) throws IOException {
        RequestType type = request.getRequestType();
        String mapping = parseMapping(request);
        Endpoint endpoint;
        switch (type) {
            case GET -> endpoint = applicationContext.getEndpointManager().fetchGetPoint(mapping);
            case POST -> endpoint = applicationContext.getEndpointManager().fetchPostPoint(mapping);
            case PUT -> endpoint = applicationContext.getEndpointManager().fetchPutPoint(mapping);
            case DELETE -> endpoint = applicationContext.getEndpointManager().fetchDeletePoint(mapping);
            default -> {
                response.setStatus(HttpStatus.NOT_IMPLEMENTED);
                response.send();
                return;
            }
        }
        Method method = endpoint.method();
        ObjectMapper mapper = new ObjectMapper();
        Object[] params = parseParams(method, request, mapper);
        byte[] body = new byte[0];
        try {
            ResponseStatus responseStatus = method.getAnnotation(ResponseStatus.class);
            HttpStatus status = responseStatus == null ? HttpStatus.OK : responseStatus.value();
            response.setStatus(status);
            ContentType typeAnnotation = method.getAnnotation(ContentType.class);
            String contentType = typeAnnotation == null ? "application/json" : typeAnnotation.value();
            response.putHeader("Content-Type", contentType);
            switch (contentType) {
                case ("image/jpeg"), ("image/gif") ->
                        body = Base64.getDecoder().decode(method.invoke(endpoint.instance(), params).toString());
                case ("text/html; charset=UTF-8"), ("text/css"), ("application/javascript") ->
                        body = method.invoke(endpoint.instance(), params).toString().getBytes(StandardCharsets.UTF_8);
                case ("application/json") -> {
                    Object result = method.invoke(endpoint.instance(), params);
                    String str = mapper.writeValueAsString(result);
                    body = str.getBytes(StandardCharsets.UTF_8);
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
        response.setBody(body);
        response.send();
    }

}