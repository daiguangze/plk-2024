package com.huawei.codecraft;

import operator.FinalOperator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
//        Scanner in = new Scanner(System.in);
        Scanner in = new Scanner(new File("C:\\Users\\15941\\Desktop\\plk-2024\\WindowsReleasev1.2\\log.txt"));
//        OperatorImpl operator = new OperatorImpl(in);
//        operator.init();
//        operator.run();
//        while(in.hasNextLine()){
//            in.nextLine();
//        }

        FinalOperator operator = new FinalOperator(in);
        operator.run();

    }
}
