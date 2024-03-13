package com.huawei;

import com.sun.java.swing.plaf.windows.WindowsInternalFrameTitlePane;
import operator.DefaultOperatorImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MyMain {



    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(System.in);
//        Scanner in = new Scanner(new File(TestMain.class.getResource("/").toString().substring(6) + "\\log.txt"));
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
