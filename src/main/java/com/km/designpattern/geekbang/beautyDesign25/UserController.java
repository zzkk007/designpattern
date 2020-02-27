package com.km.designpattern.geekbang.beautyDesign25;

import java.util.concurrent.TimeUnit;

public class UserController {

    private Metrics metrics = new Metrics();

    public UserController(){
        metrics.startRepeatedReport(60, TimeUnit.SECONDS);
    }

    public void register(String userVo){
        long startTimestamp = System.currentTimeMillis();
        metrics.recordTimestamp("register", startTimestamp);

        //.... 注册逻辑

        long respTime = System.currentTimeMillis() - startTimestamp;
        metrics.recordResponseTime("register", respTime);
    }

    public void login(String telephone, String password) {
        long startTimestamp = System.currentTimeMillis();
        metrics.recordTimestamp("login", startTimestamp);
        // 登录逻辑
        long respTime = System.currentTimeMillis() - startTimestamp;
        metrics.recordResponseTime("login", respTime);
    }
}
