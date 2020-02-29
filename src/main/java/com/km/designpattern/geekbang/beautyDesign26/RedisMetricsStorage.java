package com.km.designpattern.geekbang.beautyDesign26;

import java.util.List;
import java.util.Map;

public class RedisMetricsStorage implements MetricsStorage {
    @Override
    public void saveRequestInfo(RequestInfo requestInfo) {
        //....
    }

    @Override
    public List getRequestInfos(String apiName, long startTimeInMillis, long endTimeInMillis) {
        //...
        return null;
    }

    @Override
    public Map<String, List<RequestInfo>> getRequestInfos(long startTimeInMillis, long endTimeInMillis) {
        //...
        return null;
    }
}
