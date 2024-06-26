package model;

import com.sun.org.apache.bcel.internal.generic.BREAKPOINT;
import enums.RobotActionCode;
import enums.RobotState;
import instruction.Instruction;
import org.omg.CORBA.PUBLIC_MEMBER;
import util.astar.Coord;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

public class Robot {

    public int id;

    public int x;

    public int y;

    /**
     * 是否持有货物 0未携带 1携带
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

    public volatile RobotState robotState;

    public int mbx;

    public int mby;

    /**
     * 当前拿到的货物价值
     */
    public int price;

    /**
     * 指令队列 , 暂时不用考虑线程安全
     */
    public Queue<String> instructions = new ArrayDeque<>();

    /**
     * 第二版指令队列, 暂时为了不和第一版冲突做V2处理 暂时仅再TestOperator中使用
     */
    public Deque<Coord> instructionsV2 = new ArrayDeque<>();

    public Robot(int id, RobotState robotState) {
        this.id = id;
        this.robotState = robotState;
    }

    /**
     * 通过欧式距离找出,距离该机器人最近的节点
     *
     * @param goods
     * @return
     */
    private Good findGood(List<Good> goods) {

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

    public RobotActionCode getMoveDirection(Coord next) throws Exception {
        if (this.y != next.y) {
            if (this.y > next.y) {
                //左移
                return RobotActionCode.LEFT;
            } else {
                //右移
                return RobotActionCode.RIGHT;
            }
        } else if (this.x != next.x) {
            if (this.x > next.x) {
                // 上移
                return RobotActionCode.UP;
            } else {
                // 下移
                return RobotActionCode.DOWN;
            }
        }
        throw new Exception(String.format(" robot:[%d] 获取移动方向失败!!!   机器人坐标(%d,%d) , 下一步坐标(%d,%d)", this.id, this.x, this.y, next.x, next.y));
    }

    public String getMoveInstruction(Coord next) throws Exception {
        // 通过计算 robot坐标与 next坐标 判断是上下左右移动
        if (this.y != next.y) {
            if (this.y > next.y) {
                //左移
                return Instruction.leftString(this.id);
            } else {
                //右移
                return Instruction.rightString(this.id);
            }
        } else if (this.x != next.x) {
            if (this.x > next.x) {
                // 上移
                return Instruction.upString(this.id);
            } else {
                // 下移
                return Instruction.downString(this.id);
            }
        }
        throw new Exception(String.format(" robot:[%d] 移动方向计算错误!!!   机器人坐标(%d,%d) , 下一步坐标(%d,%d)", this.id, this.x, this.y, next.x, next.y));
    }

    public boolean move(Coord next, int[][] conflictMap, char[][] map) throws Exception {
        if (next.x == -1 || next.y == -1) {
            Instruction.getGood(this.id);
            this.instructionsV2.removeFirst();

            return true;
        }

        if (doMove(next, conflictMap, map)) {
            if (this.robotState == RobotState.FINDING_GOOD) {
                // 成功就删除
                this.instructionsV2.removeFirst();
            }
            return true;
        } else {
            return handleConflict(getMoveDirection(next), conflictMap, map);
        }
    }

    public boolean doMove(Coord next, int[][] conflictMap, char[][] map) throws Exception {
        if (next.equals(new Coord(this.x, this.y))) return true;
        if (!isConflict(next, conflictMap, map)) {
            // 解锁
            conflictMap[this.x][this.y] = -1;
            // 移动
            System.out.println(getMoveInstruction(next));
            // 锁定
            conflictMap[next.x][next.y] = this.id;
            return true;
        }
        return false;
    }

    /**
     * 2
     * 判断是否产生冲突
     *
     * @param coord       next坐标
     * @param conflictMap 冲突地图
     */
    private boolean isConflict(Coord coord, int[][] conflictMap, char[][] map) {
        if (coord.x >= conflictMap.length || coord.y >= conflictMap[0].length || coord.x < 0 || coord.y < 0) return true;
        if (conflictMap[coord.x][coord.y] != -1) return true;
        if (map[coord.x][coord.y] != '.' && map[coord.x][coord.y] != 'B' && map[coord.x][coord.y] != 'A' && map[coord.x][coord.y] != 'O') return true;
        return false;
    }

    /**
     * 处理冲突
     *
     * @param action 发生冲突的指令行为
     */
    private boolean handleConflict(RobotActionCode action, int[][] conflictMap, char[][] map) throws Exception {
        boolean result = false;
        switch (action) {
            case UP:
            case DOWN:
                result = doMove(new Coord(this.x, this.y + 1), conflictMap, map);
                if (!result) result = doMove(new Coord(this.x, this.y - 1), conflictMap, map);
                if (!result) result = doMove(new Coord(this.x - 1, this.y), conflictMap, map);
                if (!result) result = doMove(new Coord(this.x + 1, this.y), conflictMap, map);
                break;
            case LEFT:
            case RIGHT:
                result = doMove(new Coord(this.x + 1, this.y), conflictMap, map);
                if (!result) result = doMove(new Coord(this.x - 1, this.y), conflictMap, map);
                if (!result) result = doMove(new Coord(this.x, this.y + 1), conflictMap, map);
                if (!result) result = doMove(new Coord(this.x, this.y - 1), conflictMap, map);
                break;
        }
        if (result && this.robotState == RobotState.FINDING_GOOD) {
            this.instructionsV2.addFirst(new Coord(this.x, this.y));
        }
        return result;
    }
}
