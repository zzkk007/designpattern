package com.km.designpattern.geekbang.beautyDesign48;

import java.lang.reflect.Proxy;

/**
 *  使用JDK的 java.lang.reflect.Proxy 类实现动态代理，会使用其静态方法
 *  newProxyInstance(), 依据目标对象，业务接口以及业务增强逻辑三者，自动生产一个动态代理对象
 *
 *  public static newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler handler)
 *
 *  loader: 目标类的类加载器，通过目标类的反射可获取
 *  interfaces: 目标类实现的接口数组，通过目标类的反射可获取
 *  handler: 业务增强逻辑，需要再定义。
 *
 *
 */



public class MetricsCollectorProxy {

    private MetricsCollector metricsCollector;

    public MetricsCollectorProxy(){
        this.metricsCollector = new MetricsCollector();
    }

    public Object createProxy(Object proxiedObject){
        // 够获得这个对象所实现的接口
        Class<?>[] interfaces = proxiedObject.getClass().getInterfaces();

        // 业务增强
        DynamicProxyHandler handler = new DynamicProxyHandler(proxiedObject);
        return Proxy.newProxyInstance(proxiedObject.getClass().getClassLoader(), interfaces, handler);
    }

}
