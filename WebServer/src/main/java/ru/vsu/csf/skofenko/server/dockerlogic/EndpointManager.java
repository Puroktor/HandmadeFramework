package ru.vsu.csf.skofenko.server.dockerlogic;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EndpointManager {
    private final Map<String, Endpoint> getPointsMap = new HashMap<>();
    private final Map<String, Endpoint> postPointsMap = new HashMap<>();
    private final Map<String, Endpoint> putPointsMap = new HashMap<>();
    private final Map<String, Endpoint> deletePointsMap = new HashMap<>();
    private final Map<Class<? extends Exception>, Endpoint> exceptionPointMap = new HashMap<>();

    public Endpoint putGetPoint(String mapping, Endpoint endpoint) {
        return getPointsMap.put(mapping, endpoint);
    }

    public Endpoint putPostPoint(String mapping, Endpoint endpoint) {
        return postPointsMap.put(mapping, endpoint);
    }

    public Endpoint putPutPoint(String mapping, Endpoint endpoint) {
        return putPointsMap.put(mapping, endpoint);
    }

    public Endpoint putDeletePoint(String mapping, Endpoint endpoint) {
        return deletePointsMap.put(mapping, endpoint);
    }

    public Endpoint putExceptionPoint(Class<? extends Exception> mapping, Endpoint endpoint) {
        return exceptionPointMap.put(mapping, endpoint);
    }

    public Optional<Endpoint> fetchGetPoint(String mapping) {
        return Optional.ofNullable(getPointsMap.get(mapping));
    }

    public Optional<Endpoint> fetchPostPoint(String mapping) {
        return Optional.ofNullable(postPointsMap.get(mapping));
    }

    public Optional<Endpoint> fetchPutPoint(String mapping) {
        return Optional.ofNullable(putPointsMap.get(mapping));
    }

    public Optional<Endpoint> fetchDeletePoint(String mapping) {
        return Optional.ofNullable(deletePointsMap.get(mapping));
    }

    public Optional<Endpoint> fetchExceptionPoint(Class<? extends Exception> mapping) {
        return Optional.ofNullable(exceptionPointMap.get(mapping));
    }
}
