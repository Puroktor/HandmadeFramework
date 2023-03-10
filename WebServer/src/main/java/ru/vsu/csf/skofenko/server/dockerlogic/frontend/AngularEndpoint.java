package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import lombok.Getter;
import ru.vsu.csf.framework.frontend.UIEndpoint;
import ru.vsu.csf.framework.frontend.UIRequestBody;
import ru.vsu.csf.framework.frontend.field.UIField;
import ru.vsu.csf.framework.http.RequestType;

import java.util.List;

@Getter
public class AngularEndpoint implements UIEndpoint {
    private final String displayName;
    private final String fileName;
    private final String scriptName;
    private final String mapping;
    private final RequestType requestType;
    private final List<UIField> queryParams;
    private final UIRequestBody requestBody;

    public AngularEndpoint(String displayName, String mapping, RequestType requestType, List<UIField> queryParams, UIRequestBody requestBody) {
        this.displayName = displayName;
        this.fileName = displayName.replaceAll("\\s", "").toLowerCase();
        this.scriptName = fileName.substring(0, 1).toUpperCase() + fileName.substring(1);
        this.mapping = mapping;
        this.requestType = requestType;
        this.queryParams = queryParams;
        this.requestBody = requestBody;
    }
}
