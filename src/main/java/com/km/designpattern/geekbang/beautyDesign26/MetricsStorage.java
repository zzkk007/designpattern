package com.km.designpattern.geekbang.beautyDesign26;

import java.util.List;
import java.util.Map;

public interface MetricsStorage {

   void saveRequestInfo(RequestInfo requestInfo);

   List getRequestInfos(String apiName, long startTimeInMillis, long endTimeInMillis);

   Map<String, List<RequestInfo>> getRequestInfos(long startTimeInMillis, long endTimeInMillis);

}
