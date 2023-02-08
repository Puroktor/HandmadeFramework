package ru.vsu.csf.framework.frontend;

import ru.vsu.csf.framework.http.RequestType;

import java.util.List;

public record FrontEndpoint(String mapping, RequestType requestType, List<FrontRequestParam> requestParams, Class<?> response) {
}
