package com.km.designpattern.geekbang.beautyDesign48;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * InvocationHandler 是个接口，实现了InvocationHandler 接口的类用于加强目标类的主业务逻辑。
 * 这个接口中有一个方法 invoke()，具体加强的代码逻辑就定义在改方法中。程序调用注业务逻辑时，会自动
 * 自动调用该方法：
 *
 *  public Object invoke(object proxy, Method method, Object[] args)
 *  proxy: 代表生产的代理对象
 *  method: 代表目标方法
 *  args: 代码目标方法的参数
 *
 */
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
