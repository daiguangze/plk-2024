package com.huawei;

import com.sun.java.swing.plaf.windows.WindowsInternalFrameTitlePane;
import operator.DefaultOperatorImpl;
import operator.OperatorImpl;

import java.util.Scanner;

public class MyMain {



    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
//        OperatorImpl operator = new OperatorImpl(in);
//        operator.init();
//        operator.run();
//        while(in.hasNextLine()){
//            in.nextLine();
//        }

        DefaultOperatorImpl operator = new DefaultOperatorImpl(in);
        operator.run();

    }



}
