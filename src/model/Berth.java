package model;

public class Berth {
    public int id;
    public int x;
    public int y;

    /**
     * 轮船到虚拟点的时间
     */
    public int transport_time;

    /**
     * 每帧可以装载的物品数
     */
    public int loading_speed;

    /**
     * 该泊位现有的货物数量
     */
    public int goodNums;

    public Berth() {
    }

    public Berth(int id,int x, int y, int transport_time, int loading_speed) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.transport_time = transport_time;
        this.loading_speed = loading_speed;
        this.goodNums = 0;
    }
}