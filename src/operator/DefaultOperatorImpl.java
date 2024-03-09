package operator;

import instruction.Instruction;
import model.Robot;
import strategy.decision.IDecision;

import java.util.Scanner;

public class DefaultOperatorImpl extends AbstractOperator{

    IDecision decision;

    public DefaultOperatorImpl(Scanner in){
        this.in = in;
    }
    @Override
    void stepOperate() {
        /**
         * 另起一个线程, 进行计算操作 .
         * 只负责计算和往对应机器人的指令队列放指令
         */
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
//                    for(int i = 0 ; i < ROBOT_NUM ; i++){
//                        Robot robot = robots.get(i);
//                        robot.instructions.add(Instruction.upString(i));
//                    }
                    // 先测试算法 仅仅使用一个机器人进行算法测试

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.start();

        for(Robot robot : robots){
            if (!robot.instructions.isEmpty()) System.out.println(robot.instructions.poll());
        }
    }
}
