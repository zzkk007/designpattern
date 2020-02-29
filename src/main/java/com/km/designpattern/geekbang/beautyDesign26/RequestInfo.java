package com.km.designpattern.geekbang.beautyDesign26;

public class RequestInfo {

    private String apiName;
    private double responseTime;

    public RequestInfo(String apiName, double responseTime, long timestamp) {
        this.apiName = apiName;
        this.responseTime = responseTime;
        this.timestamp = timestamp;
    }

    private long timestamp;

    public String getApiName() {
        return apiName;
    }

    public double getResponseTime() {
        return responseTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }



}
