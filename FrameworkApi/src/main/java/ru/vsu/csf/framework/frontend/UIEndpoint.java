package ru.vsu.csf.framework.frontend;

import ru.vsu.csf.framework.frontend.field.UIField;
import ru.vsu.csf.framework.http.RequestType;

import java.util.List;

public interface UIEndpoint {
    String getDisplayName();
    String getFileName();
    String getScriptName();
    String getMapping();
    RequestType getRequestType();
    List<UIField> getQueryParams();
    UIRequestBody getRequestBody();
}
