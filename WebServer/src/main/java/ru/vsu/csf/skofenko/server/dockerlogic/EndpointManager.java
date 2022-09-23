package ru.vsu.csf.skofenko.server.dockerlogic;

import java.util.HashMap;
import java.util.Map;

public class EndpointManager {
    private final Map<String, Endpoint> getPointsMap = new HashMap<>();
    private final Map<String, Endpoint> postPointsMap = new HashMap<>();

    public Endpoint putGetPoint(String mapping, Endpoint endpoint){
        return getPointsMap.put(mapping, endpoint);
    }

    public Endpoint putPostPoint(String mapping, Endpoint endpoint){
        return postPointsMap.put(mapping, endpoint);
    }

    public Endpoint fetchGetPoint(String mapping){
        return getPointsMap.get(mapping);
    }

    public Endpoint fetchPostPoint(String mapping){
        return postPointsMap.get(mapping);
    }
}
