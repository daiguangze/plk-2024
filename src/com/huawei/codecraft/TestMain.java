package com.huawei.codecraft;

import operator.FinalOperator;
import operator.TestOperator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TestMain {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(System.in);
//        Scanner in = new Scanner(new File("D:\\Projects\\huawei\\WindowsReleasev1.2\\WindowsReleasev1.2\\WindowsReleasev1.2\\log\\log.txt"));
//        OperatorImpl operator = new OperatorImpl(in);
//        operator.init();
//        operator.run();
//        while(in.hasNextLine()){
//            in.nextLine();
//        }

        TestOperator operator = new TestOperator(in);
        operator.run();

    }
}
