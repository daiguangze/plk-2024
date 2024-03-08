package com.huawei;

import com.sun.java.swing.plaf.windows.WindowsInternalFrameTitlePane;
import operator.OperatorImpl;

import java.util.Scanner;

public class MyMain {



    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        OperatorImpl operator = OperatorImpl.getInstance();
        operator.init(in);
    }
}
