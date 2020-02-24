package com.km.designpattern.geekbang.beautyDesign19;

import java.util.ArrayList;
import java.util.List;

// IOC 控制反转
// 这里的控制指的是对程序执行流程的控制，而“反转”指的是在没有使用框架之前，
// 程序员自己控制程序执行（UserServiceTest执行），在使用框架之后（JunitApplication）
// 整个程序的执行流程可以通过框架来控制。流程控制权从程序员“反转”到框架。
// 控制反转不是技巧二十一个比较笼统的设计思想。
public class JunitApplication {

    private static final List<TestCase> testCases = new ArrayList<>();

    public static void register(TestCase testCase){
        testCases.add(testCase);
    }

    public static void main(String[] args) {
        for (TestCase testCase :testCases) {
            testCase.run();
        }
    }

}
