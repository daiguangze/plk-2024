package operator;

import instruction.Instruction;
import model.Good;
import model.Robot;
import strategy.decision.IDecision;
import util.astar.AStar;
import util.astar.Coord;
import util.astar.MapInfo;
import util.astar.Node;

import java.util.Scanner;

public class DefaultOperatorImpl extends AbstractOperator{

    IDecision decision;

    public DefaultOperatorImpl(Scanner in){
        this.in = in;
    }

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
                            if (robot.instructions.isEmpty()){
                                Node robotNode = new Node(robot.x,robot.y);
                                Node goodNode = new Node(good.x,good.y);
//                                Node goodNode = new Node(73,49);
                                aStar.start(new MapInfo(map,map.length,map.length,robotNode,goodNode));
                                while(!aStar.instructions.isEmpty()) {
                                    robot.instructions.add(aStar.instructions.pop());
                                }
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    void initBefore() {
        // 使用瀑布算法 计算每个泊位 TODO
    }

    @Override
    void stepOperate() {
//        for(Robot robot : robots){
//            if (!robot.instructions.isEmpty()) System.out.println(robot.instructions.poll());
//        }
        if (!robots.get(0).instructions.isEmpty()) System.out.println(robots.get(0).instructions.poll());
    }
}
