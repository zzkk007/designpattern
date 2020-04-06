package com.km.designpattern.geekbang.beautyDesign48;

import com.km.designpattern.geekbang.beautyDesign25.UserController;

public class MetricsCollector {

    public static void main(String[] args) {
        //MetricsCollectorProxy使用举例
        MetricsCollectorProxy proxy = new MetricsCollectorProxy();
        Object proxy1 = proxy.createProxy(new UserController());
    }
}
