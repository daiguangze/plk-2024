package com.huawei.codecraft;

import operator.FinalOperator;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {



    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(System.in);
//        Scanner in = new Scanner(new File(TestMain.class.getResource("/").toString().substring(6) + "\\log.txt"));
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
