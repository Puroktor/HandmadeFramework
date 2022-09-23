package ru.vsu.csf.skofenko.server.dockerlogic;

import java.util.HashMap;
import java.util.Map;

public class EndpointManager {
    private final Map<String, Endpoint> getPointsMap = new HashMap<>();
    private final Map<String, Endpoint> postPointsMap = new HashMap<>();
    private final Map<String, Endpoint> putPointsMap = new HashMap<>();
    private final Map<String, Endpoint> deletePointsMap = new HashMap<>();

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

    public Endpoint fetchGetPoint(String mapping) {
        return getPointsMap.get(mapping);
    }

    public Endpoint fetchPostPoint(String mapping) {
        return postPointsMap.get(mapping);
    }

    public Endpoint fetchPutPoint(String mapping) {
        return putPointsMap.get(mapping);
    }

    public Endpoint fetchDeletePoint(String mapping) {
        return deletePointsMap.get(mapping);
    }
}
