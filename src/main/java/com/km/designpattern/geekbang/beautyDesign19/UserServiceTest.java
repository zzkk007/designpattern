package com.km.designpattern.geekbang.beautyDesign19;

// 控制反转实现
public class UserServiceTest {
    public static boolean doTest(){
        return true;
    }

    public static void main(String[] args) {
        if (doTest()){
            System.out.println("Test succeed.");
        }else {
            System.out.println("Test failed..");
        }
    }
}
