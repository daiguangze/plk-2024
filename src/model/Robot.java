package model;

import java.util.ArrayDeque;
import java.util.Queue;

public class Robot {
    public int x;

    public int y;

    /**
     * 是否持有货物
     */
    public int goods;

    /**
     * 正常  与 恢复中
     */
    public int status;

    public int mbx;

    public int mby;

    /**
     * 指令队列 , 暂时不用考虑线程安全
     */
    public Queue<String> instructions = new ArrayDeque<>();

}
