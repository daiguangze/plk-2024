package model;

import java.awt.*;

public class Good {

    /**
     * 坐标x
     */
    public int x;

    /**
     * 坐标y
     */
    public int y;

    /**
     * 价值
     */
    public int price;

    /**
     * 货物状态
     * 是否已有机器人锁定(0: 空闲 , 1: 已被预定)
     */
    public int state;

    /**
     * 生成时的帧id
     */
    public int frameId;


    public Good(int x, int y, int price) {
        this.x = x;
        this.y = y;
        this.price = price;
    }
    Good(){}

    @Override
    public boolean equals(Object obj) {
        Good good = (Good) obj;
        return this.x == good.x && this.y == good.y;
    }
}
