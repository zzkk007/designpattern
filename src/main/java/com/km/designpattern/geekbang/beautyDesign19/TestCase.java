package com.km.designpattern.geekbang.beautyDesign19;

// 非控制反转实现
public abstract class TestCase {

    public void run() {

        if (doTest()) {
            System.out.println("Test succeed.");
        } else {
            System.out.println("Test failed.");
        }
    }

    public abstract boolean doTest();
}