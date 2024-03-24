package model;

import java.util.ArrayDeque;
import java.util.Queue;

public class Berth {
    public int id;

    public int x;
    public int y;

    /**
     * 轮船到虚拟点的时间
     */
    public int transportTime;


    /**
     * 每帧可以装载的物品数
     */
    public int loading_speed;

    /**
     * 该泊位现有的货物数量
     */
    public int goodNums;

    /**
     * 该泊位价值队列
     */
    public Queue<Integer> goodPrice = new ArrayDeque<>();

    /**
     * 总价值
     */
    public int totalPrice;

    /**
     * 是否关闭
     */
    public boolean isClose = false;

    public Berth() {
    }

    public Berth(int id, int x, int y, int transportTime, int loading_speed) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.transportTime = transportTime;
        this.loading_speed = loading_speed;
        this.goodNums = 0;
    }

    public void addGood(int price){
        this.goodPrice.add(price);
        this.totalPrice += price;
        this.goodNums++;
    }

    public void removeGoods(int num){
        for(int i = 0 ;i< num; i++){
            if (goodNums != 0 && !goodPrice.isEmpty()){
                goodNums--;
                totalPrice -= goodPrice.poll();
            }else{
                System.out.println("berth: 取货时，取货数量与实际不匹配");
            }
        }
    }

    public void clearAllGoods(){
        goodNums = 0;
        totalPrice = 0;
        goodPrice.clear();
    }
}