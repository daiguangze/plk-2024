package model;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class Robot {

    public int x;

    public int y;

    /**
     * 是否持有货物 0未携带 1携带
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


    /**
     *  通过欧式距离找出,距离该机器人最近的节点
     * @param goods
     * @return
     */
    private Good findGood(List<Good> goods){

        Good res = null;
        double minDistance = Double.MAX_VALUE;
        for (Good good : goods) {
            double euDistance = Math.sqrt((double) ((good.x - this.x) * (good.x - this.x) + (good.y - this.y) * (good.y - this.y)));
            // 暂时优先距离考虑 不考虑价值
            if (euDistance < minDistance) {
                res = good;
                minDistance = euDistance;
            } else if (euDistance == minDistance) {
                // 距离相等，取价格大的
                if (good.price > res.price) {
                    res = good;
                }
            }
        }
        return res;
    }
}
