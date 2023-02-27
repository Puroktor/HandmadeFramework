package ru.vsu.csf.framework.frontend;

import ru.vsu.csf.framework.http.RequestType;

import java.util.List;

public record UIEndpoint(String name,
                         String mapping,
                         RequestType requestType,
                         List<UIQueryParam> queryParams,
                         UIRequestBody requestBody,
                         Class<?> response) {
}
