package ru.vsu.csf.skofenko.server.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.vsu.csf.skofenko.server.Application;
import ru.vsu.csf.skofenko.ui.generator.api.core.UI;
import ru.vsu.csf.framework.http.*;
import ru.vsu.csf.skofenko.server.dockerlogic.Endpoint;
import ru.vsu.csf.skofenko.server.dockerlogic.EndpointManager;
import ru.vsu.csf.skofenko.server.dockerlogic.di.ApplicationContext;
import ru.vsu.csf.skofenko.server.dockerlogic.frontend.UIFactory;
import ru.vsu.csf.skofenko.server.http.request.HttpRequest;
import ru.vsu.csf.skofenko.server.http.response.HttpResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarFile;

public class Servlet {

    private final ApplicationContext applicationContext;
    private final String baseUrl;

    public Servlet(JarFile jar) {
        this.applicationContext = new ApplicationContext(jar);
        this.baseUrl ="http://localhost:%d/%s".formatted(Application.PORT,
                applicationContext.getResourcePath().getFileName());
        startUI();
    }

    private void startUI() {
        Path resourcePath = applicationContext.getResourcePath();
        EndpointManager endpointManager = applicationContext.getEndpointManager();
        UI ui = UIFactory.createAngularUI(baseUrl, resourcePath, endpointManager);

        boolean overrideUI = "true".equals(applicationContext.getProperty("override-ui"));
        if (ui.create(overrideUI)) {
            if ("true".equals(applicationContext.getProperty("startup-ui"))){
                new Thread(ui).start();
            }
        }
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

    private void setResponse(HttpResponse response, Endpoint endpoint, Object[] params, ObjectMapper mapper) throws Exception {
        Method method = endpoint.method();
        ResponseStatus responseStatus = method.getAnnotation(ResponseStatus.class);
        HttpStatus status = responseStatus == null ? HttpStatus.OK : responseStatus.value();
        response.setStatus(status);
        ContentType typeAnnotation = method.getAnnotation(ContentType.class);
        String contentType = typeAnnotation == null ? "application/json" : typeAnnotation.value();
        byte[] body = new byte[0];
        switch (contentType) {
            case ("image/jpeg"), ("image/gif") ->
                    body = Base64.getDecoder().decode(method.invoke(endpoint.instance(), params).toString());
            case ("text/html; charset=UTF-8"), ("text/css"), ("application/javascript") ->
                    body = method.invoke(endpoint.instance(), params).toString().getBytes(StandardCharsets.UTF_8);
            case ("application/json") -> {
                Object result = method.invoke(endpoint.instance(), params);
                if (!method.getReturnType().equals(Void.TYPE)) {
                    String str = mapper.writeValueAsString(result);
                    body = str.getBytes(StandardCharsets.UTF_8);
                }
            }
        }
        response.setBody(body);
    }

    public void doResponse(HttpRequest request, HttpResponse response) throws Exception {
        RequestType type = request.getRequestType();
        if (type.equals(RequestType.OTHER)) {
            response.setStatus(HttpStatus.NOT_IMPLEMENTED);
            response.send();
            return;
        }
        String mapping = parseMapping(request);
        Optional<Endpoint> optionalEndpoint = applicationContext.getEndpointManager().fetchEndpoint(type, mapping);
        Endpoint endpoint;
        if (optionalEndpoint.isPresent()) {
            endpoint = optionalEndpoint.get();
        } else {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.send();
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            Object[] params = parseParams(endpoint.method(), request, mapper);
            setResponse(response, endpoint, params, mapper);
        } catch (InvocationTargetException invException) {
            Exception cause = (Exception) invException.getCause();
            endpoint = applicationContext.getEndpointManager().fetchExceptionPoint(cause.getClass())
                    .orElseThrow(() -> invException);
            setResponse(response, endpoint, new Object[]{cause}, mapper);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
        response.send();
    }
}