package com.huawei;

import operator.OperatorImpl;

import java.util.Scanner;

public class MyMain {

    /**
     * 初始化: 选手程序初始化时,将按序输入
     * 1. 200 * 200 的字符组成的地图数据
     * 2. 10行的泊位数据  ( 5个数据, 分别代表 (id,x,y,time,velocity) )
     * 3. 1行的船的容积   ( 1个数据, 代表capacity)
     */
    void init(){
        Scanner in = new Scanner(System.in);
        OperatorImpl operator = OperatorImpl.getInstance();
        operator.init(in);
        in.close();
    }
}
