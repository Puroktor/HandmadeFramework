package ru.vsu.csf.skofenko.server.dockerlogic;


import ru.vsu.csf.framework.di.Controller;
import ru.vsu.csf.framework.di.Inject;
import ru.vsu.csf.framework.http.mapping.DeleteMapping;
import ru.vsu.csf.framework.http.mapping.GetMapping;
import ru.vsu.csf.framework.http.mapping.PostMapping;
import ru.vsu.csf.framework.http.mapping.PutMapping;

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
        String base = annotation.value();
        for (Method method : clazz.getDeclaredMethods()) {
            GetMapping getMapping = method.getAnnotation(GetMapping.class);
            PostMapping postMapping = method.getAnnotation(PostMapping.class);
            PutMapping putMapping = method.getAnnotation(PutMapping.class);
            DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
            if (getMapping != null) {
                manager.putGetPoint("%s/%s".formatted(base, getMapping.value()), new Endpoint(instance, method));
            } else if (postMapping != null) {
                manager.putPostPoint("%s/%s".formatted(base, postMapping.value()), new Endpoint(instance, method));
            } else if (putMapping != null) {
                manager.putPutPoint("%s/%s".formatted(base, putMapping.value()), new Endpoint(instance, method));
            } else if (deleteMapping != null) {
                manager.putDeletePoint("%s/%s".formatted(base, deleteMapping.value()), new Endpoint(instance, method));
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
