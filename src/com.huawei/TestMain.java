package com.huawei;

import model.Good;

import java.util.ArrayList;
import java.util.List;

public class TestMain {
    public static void main(String[] args) {
        Good good1 = new Good(100,123,1);
        Good good2 = new Good(100,123,1);
        Good good3 = new Good(100,123,1);
        Good good4 = new Good(100,123,1);
        Good good5 = new Good(100,123,1);
        List<Good> list = new ArrayList<>();
        list.add(good1);
        list.add(good2);
        list.add(good3);
        list.add(good4);
        list.add(good5);
        long start = System.currentTimeMillis();
        for(int i = 0 ; i < 5;  i++){
            Good good = list.get(i);
            int x = 100;
            int y = 100;
            double ans = Math.sqrt((x - good.x) * (x - good.x) + (y - good.y) * (y - good.y));
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}
