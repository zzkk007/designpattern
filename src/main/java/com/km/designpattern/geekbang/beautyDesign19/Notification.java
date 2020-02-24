package com.km.designpattern.geekbang.beautyDesign19;

/**
 *    Dependency Injection. 依赖注入
 *    不通过 new()的方式在类内部创建依赖类对象，而是将依赖的类对象在外部创建好之后，
 *    通过构造函数，函数参数等方式传递（或注入）给类使用。
 *
 */

// 非依赖注入实现方式
public class Notification {

    private MessageSender messageSender;

    public Notification(){
        this.messageSender = new MessageSender();
    }

    public void sendMessage(String cellphone, String message){
        this.messageSender.send(cellphone, message);
    }

    public static void main(String[] args) {
        //使用Notification
        Notification notification = new Notification();
    }
}

