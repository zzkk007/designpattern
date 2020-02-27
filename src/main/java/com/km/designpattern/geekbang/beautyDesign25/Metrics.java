package com.km.designpattern.geekbang.beautyDesign25;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Metrics {

    /**
     *  recordResponseTime() 和 recordTimestamp() 记录接口的响应时间和访问时间
     *  startRepeatedReport() 函数以指定的频率统计数据并输出结果。
     */

    // Map 的key 是接口名称，value 对应接口请求的响应时间或时间戳

    private Map<String, List<Double>> responseTimes = new HashMap<>();
    private Map<String, List<Double>> timestamps = new HashMap<>();
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    // putIfAbsent 如果传入key对应的value已存在，就返回存在的value,不进行替换。
    // 如果不存在，就添加key和value.
    public void recordResponseTime(String apiName, double responseTime){
        responseTimes.putIfAbsent(apiName, new ArrayList<>());
        responseTimes.get(apiName).add(responseTime);
    }

    public void recordTimestamp(String apiName, double timesTamp){
        timestamps.putIfAbsent(apiName, new ArrayList<>());
        timestamps.get(apiName).add(timesTamp);
    }

    public void startRepeatedReport(long period, TimeUnit unit){

        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Map<String, Map<String, Double>> stats = new HashMap<>();
                for (Map.Entry<String, List<Double>> entry: responseTimes.entrySet()){
                    String apiName = entry.getKey();
                    List<Double> apiTimestamps = entry.getValue();
                    stats.putIfAbsent(apiName, new HashMap<>());
                    stats.get(apiName).put("count", (double) apiTimestamps.size());
                }

                System.out.println(stats.toString());
            }
        }, 0, period, unit);

    }
}