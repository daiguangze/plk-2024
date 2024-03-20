package operator;

import com.sun.jndi.ldap.Ber;
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

import java.time.chrono.IsoChronology;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class FinalOperator implements Operator {

    ReentrantLock[] locks = new ReentrantLock[10];

    /**
     * 地图 固定 200 * 200
     */
    char[][] map = new char[MAP_SIZE][MAP_SIZE];

    /**
     * 泊位 固定10个
     */
    List<Berth> berths = new ArrayList<>();

    /**
     * 船  固定 5 个
     */
    List<Boat> boats = new ArrayList<>();

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
            disGoodList.add(new CopyOnWriteArrayList<>());
        }
        for (int i = 0; i < ROBOT_NUM; i++) {
            locks[i] = new ReentrantLock();
        }
        Arrays.fill(boat2Berth, -1);
        Arrays.fill(berth2Boat, -1);

    }

    /**
     * 另起一个线程, 进行计算操作 .
     * 只负责计算和往对应机器人的指令队列放指令
     */
    private void interactBefore() {
        Thread thread = new Thread(() -> {
            AStar aStar = new AStar('.', 0);
            while (true) {
                try {
                    for (int i = 0; i < ROBOT_NUM; i++) {
                        locks[i].lock();
                        Robot robot = robots.get(i);
                        List<Good> goodList = disGoodList.get(i);
                        Berth berth = berths.get(i);
                        if (!goodList.isEmpty() && robot.state == 1) {
                            aStar.setRobotId(i);
                            Good good = null;
                            // 寻找距离港口最近的货物
                            while (!goodList.isEmpty() && good == null) {
                                Optional<Good> maxCostBenefitGood = goodList.stream()
                                        .max(Comparator.comparingDouble(g -> g.costBenefitRatio));
                                if (maxCostBenefitGood.isPresent()) {
                                    Good goodTemp = maxCostBenefitGood.get();
                                    // 货物1000帧消失 预留200帧机器人行走时间
                                    if (goodTemp.frameId + 1000 - 200 > currentFrameId) {
                                        good = goodTemp;
                                        goodList.remove(goodTemp);
                                        break;
                                    } else {
                                        goodList.remove(goodTemp);
                                    }
                                }
                            }
                            if (good != null) {
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
                        }
                        locks[i].unlock();
                    }
                } catch (Exception e) {
                    throw e;
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
            try{
                locks[i].lock();
            }catch (Exception e){

            }

            Robot robot = robots.get(i);
            if (robot.state >= 1 && robot.status == 1) {
                if (robot.state == 1) {
                    // 空闲状态 等待指令状态中
                } else if (robot.state == 2 && !robot.instructions.isEmpty()) {
                    // 取货中 取出自己的指令 如果有性价比更高的货物，则更改目标货物 没加这个，24w5
                    // changeTargetGoodByAstar(i, robot);
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
        for (int i = 0; i < boats.size(); i++) {
            Boat boat = boats.get(i);
            switch (boat.status) {
                case 0:
                    // 移动中
                    break;
                case 1:
                    // 正常运行状态
                    switch (boat.state) {
                        case 0:
                            // 卸货（寻找泊位）
                            boat.loadedGoodsNum = 0;
                            getTargetBerth(i, 0);
                            break;
                        case 1:
                            // 在泊位装货
                            int x = boat2Berth[i];
                            if (x != -1) {
                                Berth berth = berths.get(x);

                                // 装货
                                {
                                    // 如果单次装货货物超出最大容量
                                    if (berth.loading_speed + boat.loadedGoodsNum > boat.capacity) {
                                        berth.goodNums -= boat.capacity - boat.loadedGoodsNum;
                                        boat.loadedGoodsNum = boat.capacity;
                                    }
                                    // 如果泊位剩余货物不足单次装货数量
                                    else if (berth.loading_speed > berth.goodNums) {
                                        boat.loadedGoodsNum += berth.goodNums;
                                        berth.goodNums = 0;
                                    }
                                    // 普通一帧内装货
                                    else {
                                        boat.loadedGoodsNum += berth.loading_speed;
                                        berth.goodNums -= berth.loading_speed;
                                    }
                                }

                                // 船满了，或者没时间了，去虚拟点
                                if (boat.loadedGoodsNum == boat.capacity || MAX_FRAME - this.currentFrameId <= berth.transportTime + 5) {
                                    Instruction.go(i);
                                    berth2Boat[x] = -1;
                                    boat2Berth[i] = -1;
                                    boat.state = 0;
                                }
                                // 泊位空了，去下一个泊位
                                else if (berth.goodNums == 0) {
                                    getTargetBerth(i, 1);
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
        for (int i = 0; i < MAX_FRAME; i++) {
            try {
                step();
            } catch (Exception e) {
                // 不做异常处理 只是为了保证15000次循环能执行才做的异常捕获
            }
        }
//        for(int i =0; i< BERTH_NUM; i++){
//            Berth berth = berths.get(i);
//            System.out.println("berthID: " + i + "    berth good Nums: " + berth.goodNums);
//        }

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
            robots.add(new Robot(i));
        }
        // 6. 先把船初始化了先
        for (int i = 0; i < BOAT_NUM; i++) {
            Boat boat = new Boat();
            boat.capacity = this.boatCapacity;
            boats.add(boat);
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
                // 计算新货物相对于所属泊位的性价比
//                for (Berth berth : berths) {
//                    if (berth.id == message.berthId) {
//                        double euDistance = Math.sqrt((double) ((good.x - berth.x) * (good.x - berth.x) + (good.y - berth.y) * (good.y - berth.y)));
//                        good.costBenefitRatio = good.price / euDistance;
//                    }
//                }
                good.costBenefitRatio = (double) good.price / message.DistToBerth;
                disGoodList.get(message.berthId).add(good);
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

    /**
     * 寻找目标泊位，并移动到该泊位
     *
     * @param i         船的序号
     * @param situation 情况（0：在虚拟点寻找目标泊位；1：在泊位处寻找目标泊位）
     */
    private void getTargetBerth(int i, int situation) {
        int target = -1;
        Boat boat = boats.get(i);

        switch (situation) {
            case 0: // 在虚拟点，优先找到一个货物最大的泊位
                int max = 0;
                for (int j = 0; j < BERTH_NUM; j++) {
                    Berth berth = berths.get(j);
                    int condition = berth2Boat[j];
                    // 该泊位此时无船处理
                    if (condition == -1) {
                        if (berth.goodNums >= max) {
                            max = berth.goodNums;
                            target = berth.id;
                        }
                    }
                }
                break;
            case 1: // 在泊位处，等到某个泊位处的货物数量超过了容积的3/4，即动身前往
                for (int j = 0; j < BERTH_NUM; j++) {
                    Berth berth = berths.get(j);
                    if (berth.goodNums + boat.loadedGoodsNum >= boat.capacity * 0.75 && berth2Boat[j] == -1) {
                        target = berth.id;
                    }
                }
                break;
        }


        if (target == -1) return;

        // 没时间了！赶紧送货！！！
        if ((MAX_FRAME - this.currentFrameId <= 500 + berths.get(target).transportTime + 5) && (boat.loadedGoodsNum != 0)) {
            Instruction.go(i);
            if (boat2Berth[i] != -1) {
                berth2Boat[boat2Berth[i]] = -1;
                boat2Berth[i] = -1;
            }
            boats.get(i).state = 0;
            return;
        }

        {
            // 前往该泊位
            Instruction.ship(i, target);
            // 修改状态
            if (boat2Berth[i] != -1) {
                berth2Boat[boat2Berth[i]] = -1;
            }
            berth2Boat[target] = i;
            boat2Berth[i] = target;
            boats.get(i).state = 1;
        }

    }
    private void changeTargetGood(int i, Robot robot) {
        AStar aStar = new AStar('.', i);
        List<Good> goodList = disGoodList.get(i);
        if (!goodList.isEmpty()) {
            aStar.setRobotId(i);
            Good good = null;
            // 寻找性价比最高的货物
            while (!goodList.isEmpty() && good == null) {
                Optional<Good> maxCostBenefitGood = goodList.stream()
                        .max(Comparator.comparingDouble(g -> g.costBenefitRatio));
                if (maxCostBenefitGood.isPresent()) {
                    Good goodTemp = maxCostBenefitGood.get();
                    // 货物1000帧消失 预留200帧机器人行走时间
                    if (goodTemp.frameId + 1000 - 200 > currentFrameId) {
                        good = goodTemp;
                        goodList.remove(goodTemp);
                        break;
                    } else {
                        goodList.remove(goodTemp);
                    }
                }
            }
            if (good != null) { // A*
                aStar.setRobotId(i);
                Node robotNode = new Node(robot.x, robot.y);
                Node goodNode = new Node(good.x, good.y);
//                                Node goodNode = new Node(73,49);
                // A*计算路径
                aStar.start(new MapInfo(map, map.length, map.length, robotNode, goodNode));
                // 将A* 里面的指令copy到机器人指令队列
                while (!aStar.instructions.isEmpty()) {
                    robot.instructions.clear();
                    robot.instructions.add(aStar.instructions.pop());
                }
                robot.instructions.add(Instruction.getGoodString(i));
            }
        }
    }
    private void changeTargetGoodByAstar(int i, Robot robot) {
        List<Good> goodList = disGoodList.get(i);
        if (!goodList.isEmpty()) {
            Good good = null;
            // 寻找性价比最高的货物
            while (!goodList.isEmpty() && good == null) {
                Optional<Good> maxCostBenefitGood = goodList.stream()
                        .max((good1, good2) -> {
                            AStar aStar1 = new AStar('.', i);
                            AStar aStar2 = new AStar('.', i);
                            Node robotNode = new Node(robot.x, robot.y);
                            Node goodNode1 = new Node(good1.x, good1.y);
                            aStar1.start(new MapInfo(map, map.length, map.length, robotNode, goodNode1));
                            Node goodNode2 = new Node(good2.x, good2.y);
                            aStar2.start(new MapInfo(map, map.length, map.length, robotNode, goodNode2));
                            int size1 = aStar1.instructions.size();
                            int size2 = aStar2.instructions.size();
                            int cost = good1.price / size1;
                            int cost2 = good2.price / size2;
                            return cost2 - cost;
                        });
                if (maxCostBenefitGood.isPresent()) {
                    Good goodTemp = maxCostBenefitGood.get();
                    // 货物1000帧消失 预留200帧机器人行走时间
                    if (goodTemp.frameId + 1000 - 200 > currentFrameId) {
                        good = goodTemp;
                        goodList.remove(goodTemp);
                        break;
                    } else {
                        goodList.remove(goodTemp);
                    }
                }
            }
            if (good != null) {
                AStar aStar = new AStar('.', i);
                // A*
                aStar.setRobotId(i);
                Node robotNode = new Node(robot.x, robot.y);
                Node goodNode = new Node(good.x, good.y);
//                                Node goodNode = new Node(73,49);
                // A*计算路径
                aStar.start(new MapInfo(map, map.length, map.length, robotNode, goodNode));
                // 将A* 里面的指令copy到机器人指令队列
                while (!aStar.instructions.isEmpty()) {
                    robot.instructions.clear();
                    robot.instructions.add(aStar.instructions.pop());
                }
                robot.instructions.add(Instruction.getGoodString(i));
            }
        }
    }
}
