package ru.vsu.csf.framework.frontend;

import ru.vsu.csf.framework.frontend.field.UIField;
import ru.vsu.csf.framework.http.RequestType;

import java.util.List;

public record UIEndpoint(String codeName,
                         String displayName,
                         String mapping,
                         RequestType requestType,
                         List<UIField> queryParams,
                         UIRequestBody requestBody) {
}
