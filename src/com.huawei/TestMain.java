package com.huawei;

import com.sun.org.apache.bcel.internal.generic.LSTORE;
import model.Good;

import java.util.ArrayList;
import java.util.List;

public class TestMain {
    public static void main(String[] args) {
       List<List<Good>> aaa = new ArrayList<>();
       for(int i = 0 ; i < 10 ; i ++){
           aaa.add(new ArrayList<Good>());

       }

        aaa.get(0).add(new Good(1,2,3));
        aaa.get(0).add(new Good(1,2,3));
        aaa.get(0).add(new Good(1,2,3));
        aaa.get(0).add(new Good(1,2,3));

    }
}
