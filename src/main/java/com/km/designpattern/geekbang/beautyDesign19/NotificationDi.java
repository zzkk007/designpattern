package com.km.designpattern.geekbang.beautyDesign19;

// 依赖注入方式
// 通过依赖注入方式将依赖的类对象传递进来，提高代码的扩展性，灵活的替换依赖类。
public class NotificationDi {

    private MessageSender messageSender;

    // 通过构造函数将MessageSender 传递过来
    public NotificationDi(MessageSender messageSender){
        this.messageSender = messageSender;
    }

    public void sendMessage(String cellphone, String message){
        this.messageSender.send(cellphone, message);
    }

    public static void main(String[] args) {

        MessageSender messageSender = new MessageSender();
        NotificationDi notification = new NotificationDi(messageSender);

    }

}
