package operator;

import instruction.Instruction;
import model.Berth;
import model.Boat;
import model.Good;
import model.Robot;
import util.astar.AStar;
import util.astar.MapInfo;
import util.astar.Node;
import util.floodfill.FloodFill;
import util.floodfill.MapNode;
import util.floodfill.PointMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class FinalOperator implements Operator {

    ReentrantLock[] locks = new ReentrantLock[10];

    /**
     * 地图 固定 200 * 200
     */
    char[][] map = new char[MAP_SIZE + 10][MAP_SIZE + 10];

    /**
     * 泊位 固定10个
     */
    List<Berth> berths = new ArrayList<>();

    /**
     * 船  固定 5 个
     */
    List<Boat> boats = new ArrayList<>();

    /**
     * 货品
     */
    List<Good> goods = new ArrayList<>();

    /**
     * 货物
     */
    List<CopyOnWriteArrayList<Good>> disGoodList = new CopyOnWriteArrayList<>();
    /**
     * 机器人
     */
    List<Robot> robots = new ArrayList<>();

    /**
     * 船容量
     */
    int boatCapacity;

    /**
     * 输入流
     */
    Scanner in;

    /**
     * 当前帧id
     */
    volatile int currentFrameId = 0;

    /**
     * 地图信息
     */
    Map<MapNode, PointMessage> mapMessage;

    /**
     * 泊位工作状态
     * key : 泊位id
     * value:工作的轮船id
     */
    int[] berth2Boat = new int[BERTH_NUM];
    int[] boat2Berth = new int[BOAT_NUM];


    public FinalOperator(Scanner in) {
        this.in = in;
        for (int i = 0; i < BERTH_NUM; i++) {
            disGoodList.add(new CopyOnWriteArrayList<Good>());
        }
        for (int i = 0; i < ROBOT_NUM; i++) {
            locks[i] = new ReentrantLock();
        }
        for (int i = 0; i < BOAT_NUM; i++) {
            boat2Berth[i] = -1;
        }
        for (int i = 0; i < BERTH_NUM; i++) {
            berth2Boat[i] = -1;
        }

    }

    private void interactBefore() {
        /**
         * 另起一个线程, 进行计算操作 .
         * 只负责计算和往对应机器人的指令队列放指令
         */
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                AStar aStar = new AStar('.', 0);
                while (true) {
                    try {
                        for (int i = 0; i < ROBOT_NUM; i++) {
                            locks[i].lock();
                            List<Good> goodList = disGoodList.get(i);
                            if (!goodList.isEmpty()) {
                                aStar.setRobotId(i);
                                Robot robot = robots.get(i);
                                Good good = null;
                                // 寻找第一个不过期的货物
                                while (!goodList.isEmpty() && good == null) {
                                    Good goodTemp = goodList.remove(0);
                                    // 货物1000帧消失 预留200帧机器人行走时间
                                    if (goodTemp.frameId + 1000 - 200 > currentFrameId) {
                                        good = goodTemp;
                                    }
                                }
                                if (good == null) continue;
                                // A*
                                aStar.setRobotId(i);
                                if (robot.instructions.isEmpty() && robot.state == 1) {
                                    Node robotNode = new Node(robot.x, robot.y);
                                    Node goodNode = new Node(good.x, good.y);
//                                Node goodNode = new Node(73,49);
                                    // A*计算路径
                                    aStar.start(new MapInfo(map, map.length, map.length, robotNode, goodNode));
                                    // 将A* 里面的指令copy到机器人指令队列
                                    while (!aStar.instructions.isEmpty()) {
                                        robot.instructions.add(aStar.instructions.pop());
                                    }
                                    robot.instructions.add(Instruction.getGoodString(i));
                                    robot.state = 2;
                                }
                            }
                            locks[i].unlock();
                        }
                    } catch (Exception e) {
                        throw e;
                    }
                }
            }
        });
        thread.start();
        try {
            // 初始化时间没用完  睡一会让A*计算
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 每帧与判题器的交互操作  1- 15000
     */
    void operate() throws InterruptedException {

        Thread.sleep(10);
        // 1. 机器人指令处理
        for (int i = 0; i < ROBOT_NUM; i++) {
            locks[i].lock();
            Robot robot = robots.get(i);
            if (robot.state >= 1 && robot.status == 1) {
                if (robot.state == 1) {
                    // 空闲状态 等待指令状态中
                } else if (robot.state == 2 && !robot.instructions.isEmpty()) {
                    // 取货中 取出自己的指令
                    System.out.println(robot.instructions.poll());
                } else if (robot.state == 2 && robot.instructions.isEmpty()) {
                    // 变更为前往泊位状态
                    robot.state = 3;
                } else if (robot.state == 3) {
                    // 取出当前节点的路径信息
                    PointMessage message = mapMessage.getOrDefault(new MapNode(robot.x, robot.y), null);
                    if (message == null) {
                        // 到达泊位 变更为空闲状态
//                        Instruction.pullGood(i);
                        robot.state = 1;
                    } else {
                        switch (message.actionCode) {
                            case 1:
                                Instruction.up(i);
                                break;
                            case 2:
                                Instruction.right(i);
                                break;
                            case 3:
                                Instruction.down(i);
                                break;
                            case 4:
                                Instruction.left(i);
                                break;
                            case 5:
                                Instruction.pullGood(i);
                                berths.get(i).goodNums++;
                                robot.state = 1;
                                break;
                        }
                    }
                }
            } else {
                // 异常状态 清空所有状态信息重新计算
                robot.instructions.clear();
                robot.state = 1;
            }

            locks[i].unlock();
        }

        // 2. 船指令
        // 找个没人的泊位
        for (int i = 0; i < boats.size(); i++) {
            Boat boat = boats.get(i);


            // TODO 自定义状态
            switch (boat.status) {
                case 0:
                    // 移动中
                    break;
                case 1:
                    // 正常运行状态
                    switch (boat.state){
                        case 0:
                            // 卸货（寻找泊位）
                            int target = -1;
                            int max = 0;
                            for (int j = 0; j < BERTH_NUM; j++) {
                                Berth berth = berths.get(j);
                                Integer condition = berth2Boat[j];
                                // 该泊位此时无船处理
                                if (condition == -1) {
                                    if (berth.goodNums > max) {
                                        max = berth.goodNums;
                                        target = berth.id;
                                    }
                                }
                            }
                            if (target >= 0) {
                                // 前往该泊位
                                Instruction.ship(i,target);
                                // 修改状态
                                berth2Boat[target] = i;
                                boat2Berth[i] = target;
                                boat.state = 1;
                            }
                            break;
                        case 1:
                            int x = boat2Berth[i];
                            if ( x != -1){
                                boat.stayFrame++;
                                if (boat.stayFrame > berths.get(x).goodNums / berths.get(x).loading_speed + 2){
                                    berths.get(x).goodNums = 0;
                                    Instruction.go(i);
                                    boat.state = 0;
                                    boat.stayFrame = 0;
                                    berth2Boat[x] = -1;
                                    boat2Berth[i] = -1;
                                }
                            }
                            break;

                    }
                    break;
                case 2:
                    //2 泊位外等待
                    break;

            }

        }

        // >= 1 为正常运行状态

        // 3. 结束后主动flush
        System.out.flush();

//        if (!robots.get(0).instructions.isEmpty()) System.out.println(robots.get(0).instructions.poll());
    }


    @Override
    public void run() {
        init();
        interactBefore();
        String okk = in.nextLine();
        System.out.println("OK");
        System.out.flush();
        for (int i = 0; i < 15000; i++) {
            try {
                step();
            } catch (Exception e) {
                // 不做异常处理 只是为了保证15000次循环能执行才做的异常捕获
            }
        }
    }


    private void step() throws InterruptedException {
        // 1. 读取
        // 第一行输入2个整数,表示帧序号, 当前金钱
        read();
        // 2. 操作
        operate();
        // 3. ok
        System.out.println("OK");
        // 4. 清空缓存区
        System.out.flush();
    }

    /**
     * 初始化
     */
    private void init() {
        // 1.读取地图
        getMap();
        // 2.读取泊位
        getBerths();
        // 2.1 初始化地图信息  对10个泊位进行洪水染色
        initMapMessage();
        // 3.读取船容量
        getBoatCapacity();
        // 4. 读取结束
        // 5. 先把机器人初始化了先
        for (int i = 0; i < ROBOT_NUM; i++) {
            robots.add(new Robot());
        }
        // 6. 先把船初始化了先
        for (int i = 0; i < BOAT_NUM; i++) {
            boats.add(new Boat());
        }
        in.nextLine();

    }

    private void initMapMessage() {
        this.mapMessage = FloodFill.getPointMessage(map, berths);
    }


    /**
     * 每一帧交互
     * 第一行输入两个整数, 表示帧序号 (从1开始递增)、 当前金钱数
     * 第二行输入一个整数, 表示场上新增货物的数量 k [0,10]
     * 紧接着K行数据,每一行表示一个新增货物, 分别由如下所示数据构成
     */
    private void read() {
        // 帧id
        this.currentFrameId = in.nextInt();
        // 当前金钱
        int money = in.nextInt();
        System.out.println(this.currentFrameId + " " + money);
        // 新增货物数量
        int k = in.nextInt();
        for (int i = 0; i < k; i++) {
            Good good = new Good(in.nextInt(), in.nextInt(), in.nextInt());
            PointMessage message = mapMessage.getOrDefault(new MapNode(good.x, good.y), null);
            if (message != null) {
                good.frameId = this.currentFrameId;
                CopyOnWriteArrayList<Good> goodListz = disGoodList.get(message.berthId);
                goodListz.add(good);
            }
//            goods.add(good);
        }

        // 接下来10行表示机器人 (是否携带货物, x , y , 状态 0:恢复状态, 1:正常运行)
        for (int i = 0; i < ROBOT_NUM; i++) {
            // 更新机器人数据
            Robot robot = robots.get(i);
            robot.goods = in.nextInt();
            robot.x = in.nextInt();
            robot.y = in.nextInt();
            robot.status = in.nextInt();
        }
        // 接下来5行数据表示船
        for (int i = 0; i < BOAT_NUM; i++) {
            Boat boat = boats.get(i);
            boat.status = in.nextInt();
            boat.pos = in.nextInt();
        }
        in.nextLine();
        String okk = in.nextLine();

    }

    private void getMap() {
        // 读取地图
        for (int i = 0; i < MAP_SIZE; i++) {
            String row = in.nextLine();
            map[i] = row.toCharArray();
        }
    }

    private void getBerths() {
        for (int i = 0; i < BERTH_NUM; i++) {
            berths.add(new Berth(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt()));
        }
    }

    private void getBoatCapacity() {
        this.boatCapacity = in.nextInt();
    }
}
