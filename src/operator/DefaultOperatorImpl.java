package operator;

import instruction.Instruction;
import model.Good;
import model.Robot;
import netscape.security.UserTarget;
import strategy.decision.IDecision;
import util.astar.AStar;
import util.astar.Coord;
import util.astar.MapInfo;
import util.astar.Node;
import util.floodfill.FloodFill;
import util.floodfill.MapNode;
import util.floodfill.PointMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DefaultOperatorImpl extends AbstractOperator{

    IDecision decision;

    Map<MapNode, PointMessage> mapMessage;

    public DefaultOperatorImpl(Scanner in){
        this.in = in;
    }

    /**
     * 埋点 : 获取泊位结束后
     */
    @Override
    void getBerthsAfter() {
        //瀑布算法
        this.mapMessage = FloodFill.getPointMessage(this.map,this.berths);
    }


    /**
     * 埋点 : 进行 15000次循环前
     */
    @Override
    void runBefore() {
        /**
         * 另起一个线程, 进行计算操作 .
         * 只负责计算和往对应机器人的指令队列放指令
         */
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                AStar aStar = new AStar('.');
                while (true){
                    try {
                        if (!goods.isEmpty()){
                            Robot robot = robots.get(0);
                            Good good = goods.remove(0);
                            if (robot.instructions.isEmpty() && robot.state == 1){
                                Node robotNode = new Node(robot.x,robot.y);
                                Node goodNode = new Node(good.x,good.y);
//                                Node goodNode = new Node(73,49);
                                aStar.start(new MapInfo(map,map.length,map.length,robotNode,goodNode));
                                robot.instructions.add(Instruction.getGoodString(0));
                                while(!aStar.instructions.isEmpty()) {
                                    robot.instructions.add(aStar.instructions.pop());
                                }
                                robot.state = 2;
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 每帧与判题器的交互操作
     */
    @Override
    void stepOperate() throws InterruptedException {
//        for(Robot robot : robots){
//            if (!robot.instructions.isEmpty()) System.out.println(robot.instructions.poll());
//        }
//        Thread.sleep(10);

        // >= 1 为正常运行状态
        Robot robot = robots.get(0);
        if (robot.state >= 1){
            if (robot.state == 1){
                // 空闲状态 等待指令状态中
            }else if (robot.state == 2 && !robot.instructions.isEmpty()) {
                // 取货中 取出自己的指令
                System.out.println(robot.instructions.poll());
            }else if (robot.state == 2 && robot.instructions.isEmpty()){
                // 变更为前往泊位状态
                robot.state = 3;
            }else if(robot.state == 99){
                // 取出当前节点的路径信息
                PointMessage message = mapMessage.getOrDefault(new MapNode(robot.x, robot.y), null);
                if (message == null ){
                    // 到达泊位
                    Instruction.pullGood(0);
                    robot.state = 1;
                }else {
                    switch (message.actionCode){
                        case 0:
                            Instruction.up(0);
                            break;
                        case 1:
                            Instruction.right(0);
                            break;
                        case 2:
                            Instruction.down(0);
                            break;
                        case 3:
                            Instruction.left(0);
                            break;
                    }
                }
            }
        }else{
            // 异常状态 清空所有状态信息重新计算
            robot.instructions.clear();
            robot.state = 1;
        }
        if (!robots.get(0).instructions.isEmpty()) System.out.println(robots.get(0).instructions.poll());
    }
}
