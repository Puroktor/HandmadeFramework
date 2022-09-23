package ru.vsu.csf.skofenko.server.dockerlogic;


import java.lang.reflect.Method;

public record Endpoint(Object instance, Method method) {

}
