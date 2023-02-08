package ru.vsu.csf.skofenko.server.dockerlogic;

import ru.vsu.csf.framework.http.RequestType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EndpointManager {
    private final Map<RequestType, Map<String, Endpoint>> endpointsMap = Map.of(
            RequestType.GET, new HashMap<>(),
            RequestType.POST, new HashMap<>(),
            RequestType.PUT, new HashMap<>(),
            RequestType.DELETE, new HashMap<>()
    );
    private final Map<Class<? extends Exception>, Endpoint> exceptionPointMap = new HashMap<>();

    public Endpoint putEndpoint(RequestType requestType, String mapping, Endpoint endpoint) {
        return endpointsMap.get(requestType).put(mapping, endpoint);
    }
    public Endpoint putExceptionPoint(Class<? extends Exception> mapping, Endpoint endpoint) {
        return exceptionPointMap.put(mapping, endpoint);
    }

    public Optional<Endpoint> fetchEndpoint(RequestType requestType, String mapping) {
        Map<String, Endpoint> requestMap = endpointsMap.get(requestType);
        return Optional.ofNullable(requestMap.get(mapping));
    }

    public Optional<Endpoint> fetchExceptionPoint(Class<? extends Exception> mapping) {
        return Optional.ofNullable(exceptionPointMap.get(mapping));
    }
}
