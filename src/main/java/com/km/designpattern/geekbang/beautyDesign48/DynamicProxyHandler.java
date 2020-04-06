package com.km.designpattern.geekbang.beautyDesign48;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DynamicProxyHandler implements InvocationHandler {

    private Object proxiedObject;

    public DynamicProxyHandler(Object proxiedObject) {
        this.proxiedObject = proxiedObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        long startTimestamp = System.currentTimeMillis();
        Object result = method.invoke(proxiedObject, args);
        long endTimeStamp = System.currentTimeMillis();
        long responseTime = endTimeStamp - startTimestamp;
        String apiName = proxiedObject.getClass().getName() + ":" + method.getName();
        //RequestInfo requestInfo = new RequestInfo(apiName, responseTime, startTimestamp);
        //metricsCollector.recordRequest(requestInfo);
        return result;
    }
}
