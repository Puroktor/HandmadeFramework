package ru.vsu.csf.skofenko.server.dockerlogic.frontend;

import lombok.experimental.UtilityClass;
import ru.vsu.csf.framework.http.RequestType;
import ru.vsu.csf.skofenko.ui.generator.api.core.UIRequestType;

@UtilityClass
public class RequestTypeUtil {
    public static UIRequestType toUIType(RequestType requestType) {
        return switch (requestType) {
            case GET -> UIRequestType.GET;
            case POST -> UIRequestType.POST;
            case PUT -> UIRequestType.PUT;
            case DELETE -> UIRequestType.DELETE;
            case OTHER -> UIRequestType.OTHER;
        };
    }
}
