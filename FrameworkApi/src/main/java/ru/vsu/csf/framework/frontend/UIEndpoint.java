package ru.vsu.csf.framework.frontend;

import ru.vsu.csf.framework.http.RequestType;

import java.util.List;

public record UIEndpoint(String mapping, RequestType requestType, List<UIRequestParam> requestParams, Class<?> response) {
}
