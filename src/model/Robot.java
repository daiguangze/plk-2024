package model;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class Robot {
    public int x;

    public int y;

    /**
     * 是否持有货物
     */
    public int goods;

    /**
     * 0:回复 -- 1:正常运行状态( 0 1 为判题器 给出的状态 以下为新增状态 来扩展正常运行状态)
     */
    public int status;

    /**
     * 扩展状态
     * 1: 空闲
     * 2: 取货中. (此状态时, 内涵
     * 3: 取货成功, 前往泊位状态
     * 4:
     */
    public volatile int state;

    public int mbx;

    public int mby;

    /**
     * 指令队列 , 暂时不用考虑线程安全
     */
    public Queue<String> instructions = new ArrayDeque<>();


    /**
     *  通过欧式距离找出,距离该机器人最近的节点
     * @param goods
     * @return
     */
    private Good findGood(List<Good> goods){
        //TODO
        return null;
    }
}
