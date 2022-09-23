package ru.vsu.csf.skofenko.server.dockerlogic;


import ru.vsu.csf.annotations.http.HttpStatus;

import java.lang.reflect.Method;

public record Endpoint(Object instance, Method method, HttpStatus status) {

}
