package com.huawei;

import operator.OperatorImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TestMain {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File("D:\\Projects\\huawei\\plk-2024\\test.txt"));
        OperatorImpl operator = new OperatorImpl(in);
        operator.init();
        operator.run();
    }
}
