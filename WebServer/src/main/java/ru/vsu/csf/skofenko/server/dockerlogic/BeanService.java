package ru.vsu.csf.skofenko.server.dockerlogic;


import ru.vsu.csf.annotations.di.Controller;
import ru.vsu.csf.annotations.di.Inject;
import ru.vsu.csf.annotations.http.GetMapping;
import ru.vsu.csf.annotations.http.HttpStatus;
import ru.vsu.csf.annotations.http.PostMapping;
import ru.vsu.csf.annotations.http.ResponseType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

class BeanService {

    static Object initialise(Class<?> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException("Couldn't create instance of class", e);
        }
    }

    static void parseEndpoints(Class<?> clazz, Object instance, EndpointManager manager) {
        Controller annotation = clazz.getDeclaredAnnotation(Controller.class);
        String base = "/" + annotation.value();
        for (Method method : clazz.getDeclaredMethods()) {
            GetMapping getMapping = method.getAnnotation(GetMapping.class);
            PostMapping postMapping = method.getAnnotation(PostMapping.class);
            ResponseType responseType = method.getAnnotation(ResponseType.class);
            HttpStatus status = responseType == null ? HttpStatus.OK : responseType.value();
            if (getMapping != null) {
                manager.putGetPoint(base + getMapping.value(), new Endpoint(instance, method, status));
            } else if (postMapping != null) {
                manager.putPostPoint(base + postMapping.value(), new Endpoint(instance, method, status));
            }
        }
    }

    static void setFields(Class<?> clazz, Object instance, Map<Class<?>, Object> map) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getAnnotation(Inject.class) != null) {
                Object value = map.get(field.getType());
                field.setAccessible(true);
                try {
                    field.set(instance, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Couldn't set injection fields", e);
                }
            }
        }
    }
}
