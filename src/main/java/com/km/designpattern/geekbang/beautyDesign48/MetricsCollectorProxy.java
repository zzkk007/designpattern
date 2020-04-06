package com.km.designpattern.geekbang.beautyDesign48;

import java.lang.reflect.Proxy;

public class MetricsCollectorProxy {

    private MetricsCollector metricsCollector;

    public MetricsCollectorProxy(){
        this.metricsCollector = new MetricsCollector();
    }

    public Object createProxy(Object proxiedObject){
        // 够获得这个对象所实现的接口
        Class<?>[] interfaces = proxiedObject.getClass().getInterfaces();

        DynamicProxyHandler handler = new DynamicProxyHandler(proxiedObject);
        return Proxy.newProxyInstance(proxiedObject.getClass().getClassLoader(), interfaces, handler);
    }

}
