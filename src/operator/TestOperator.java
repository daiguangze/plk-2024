package operator;

import enums.RobotActionCode;
import enums.RobotState;
import instruction.Instruction;
import model.Berth;
import model.Boat;
import model.Good;
import model.Robot;
import util.astar.*;
import util.floodfill.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class TestOperator implements Operator {

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
     * 所有货物
     */
    volatile CopyOnWriteArrayList<Good> allGoods = new CopyOnWriteArrayList<>();
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
    Map<MapNode, PointMessageV2> mapMessage;

    Map<MapNode, PointMessageV2> singleMapMessage[] = new Map[10];

    /**
     * 碰撞检测地图
     */
    int[][] collision = new int[MAP_SIZE][MAP_SIZE];

    /**
     * 泊位工作状态
     * key : 泊位id
     * value:工作的轮船id
     */
    int[] berth2Boat = new int[BERTH_NUM];
    int[] boat2Berth = new int[BOAT_NUM];

    public TestOperator(Scanner in) {
        this.in = in;
        for (int i = 0; i < BERTH_NUM; i++) {
            //disGoodList.add(new CopyOnWriteArrayList<>());
            singleMapMessage[i] = new HashMap<>();
        }
        for (int i = 0; i < ROBOT_NUM; i++) {
            locks[i] = new ReentrantLock();
        }
        Arrays.fill(boat2Berth, -1);
        Arrays.fill(berth2Boat, -1);

        for (int i = 0; i < collision.length; i++) {
            for (int j = 0; j < collision[0].length; j++) {
                collision[i][j] = -1;
            }
        }
    }

    /**
     * 另起一个线程, 进行计算操作 .
     * 只负责计算和往对应机器人的指令队列放指令
     */
    /*private void interactBefore() {
        Thread thread = new Thread(() -> {
            AStarV2 aStar = new AStarV2('.');
            while (true) {
                try {
                    List<Good> unReach = new ArrayList<>();
                    for (int i = 0; i < ROBOT_NUM; i++) {
                        Robot robot = robots.get(i);
//                        List<Good> goodList = disGoodList.get(RebalanceFloodFill.allocation[i]);
                        if (!allGoods.isEmpty() && robot.robotState == RobotState.BORING) {
                            Good good = null;
                            while (!allGoods.isEmpty() && good == null) {
                                Optional<Good> maxCostBenefitGood = allGoods.stream()
                                        .max(Comparator.comparingDouble(g -> g.costBenefitRatio[RebalanceFloodFill.allocation[robot.id]]));
                                if (maxCostBenefitGood.isPresent()) {
                                    Good goodTemp = maxCostBenefitGood.get();
                                    if (goodTemp.costBenefitRatio[RebalanceFloodFill.allocation[robot.id]] == -1 ) break;
                                    // 货物1000帧消失 预留200帧机器人行走时间
                                    if (goodTemp.frameId + 1000 - 100 > currentFrameId) {
                                        good = goodTemp;
                                        // 锁定后超时
                                        if (!allGoods.remove(goodTemp)) {
                                            throw new Exception("remove fail");
                                        }
                                        break;
                                    } else {
                                        // 货物超时 , 删除记录
                                        if (!allGoods.remove(goodTemp)) {
                                            throw new Exception("remove fail");
                                        }
                                    }
                                }

                            }
                            locks[i].lock();
                            if (good != null) {
                                PointMessageV2 robotPointPosition = mapMessage.getOrDefault(new MapNode(robot.x, robot.y), null);
                                int robotInBerthId = robotPointPosition.berthId;
                                PointMessageV2 message = singleMapMessage[robotInBerthId].getOrDefault(new MapNode(good.x, good.y), null);
                                int x = good.x;
                                int y = good.y;
                                while (message.actionCode != RobotActionCode.PULL) {

                                    switch (message.actionCode) {
                                        case UP:
                                            robot.instructionsV2.addFirst(new Coord(x , y));
                                            x--;
                                            break;
                                        case DOWN:
                                            robot.instructionsV2.addFirst(new Coord(x , y));
                                            x++;
                                            break;
                                        case LEFT:
                                            robot.instructionsV2.addFirst(new Coord(x, y ));
                                            y--;
                                            break;
                                        case RIGHT:
                                            robot.instructionsV2.addFirst(new Coord(x, y ));
                                            y++;
                                            break;
                                    }
                                    message = singleMapMessage[robotInBerthId].getOrDefault(new MapNode(x, y), null);
                                }

                                // 加入 取货指令 先暂时用 -1 -1 的坐标代替一下 如果有更好的想法再改
                                robot.instructionsV2.addLast(new Coord(-1, -1));
                                robot.robotState = RobotState.FINDING_GOOD;
                                robot.price = good.price;
                            }
                            locks[i].unlock();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }*/

    public void findTargetGood(){
        try {
            List<Good> unReach = new ArrayList<>();
            for (int i = 0; i < ROBOT_NUM; i++) {
                Robot robot = robots.get(i);
//                        List<Good> goodList = disGoodList.get(RebalanceFloodFill.allocation[i]);
                if (singleMapMessage[RebalanceFloodFill.allocation[robot.id]].getOrDefault(new MapNode(robot.x,robot.y),null) != null && !allGoods.isEmpty() && robot.robotState == RobotState.BORING) {
                    Good good = null;
                    while (!allGoods.isEmpty() && good == null) {
                        Optional<Good> maxCostBenefitGood = allGoods.stream()
                                .max(Comparator.comparingDouble(g -> g.costBenefitRatio[RebalanceFloodFill.allocation[robot.id]]));
                        if (maxCostBenefitGood.isPresent()) {
                            Good goodTemp = maxCostBenefitGood.get();
                            if (goodTemp.costBenefitRatio[RebalanceFloodFill.allocation[robot.id]] == -1 ) break;
                            // 货物1000帧消失 预留200帧机器人行走时间
                            if (goodTemp.frameId + 1000 - 100 > currentFrameId) {
                                good = goodTemp;
                                // 锁定后超时
                                if (!allGoods.remove(goodTemp)) {
                                    throw new Exception("remove fail");
                                }
                                break;
                            } else {
                                // 货物超时 , 删除记录
                                if (!allGoods.remove(goodTemp)) {
                                    throw new Exception("remove fail");
                                }
                            }
                        }

                    }
//                    locks[i].lock();
                    if (good != null) {
                        PointMessageV2 robotPointPosition = singleMapMessage[RebalanceFloodFill.allocation[robot.id]].getOrDefault(new MapNode(robot.x, robot.y), null);
                        int robotInBerthId = robotPointPosition.berthId;
                        PointMessageV2 message = singleMapMessage[robotInBerthId].getOrDefault(new MapNode(good.x, good.y), null);
                        int x = good.x;
                        int y = good.y;
                        while (message.actionCode != RobotActionCode.PULL) {

                            switch (message.actionCode) {
                                case UP:
                                    robot.instructionsV2.addFirst(new Coord(x , y));
                                    x--;
                                    break;
                                case DOWN:
                                    robot.instructionsV2.addFirst(new Coord(x , y));
                                    x++;
                                    break;
                                case LEFT:
                                    robot.instructionsV2.addFirst(new Coord(x, y ));
                                    y--;
                                    break;
                                case RIGHT:
                                    robot.instructionsV2.addFirst(new Coord(x, y ));
                                    y++;
                                    break;
                            }
                            message = singleMapMessage[robotInBerthId].getOrDefault(new MapNode(x, y), null);
                        }

                        // 加入 取货指令 先暂时用 -1 -1 的坐标代替一下 如果有更好的想法再改
                        robot.instructionsV2.addLast(new Coord(-1, -1));
                        robot.robotState = RobotState.FINDING_GOOD;
                        robot.price = good.price;

                        double maxBenefit = -1;
                        int targetBerth = -1;
                        for(int j = 0; j < BERTH_NUM; j++){
                            if (good.costBenefitRatio[j] > maxBenefit){
                                targetBerth = j;
                                maxBenefit = good.costBenefitRatio[j];
                            }
                        }
                        if (targetBerth != -1){
                            RebalanceFloodFill.allocation[robot.id] = targetBerth;
                        }
                    }
//                    locks[i].unlock();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 每帧与判题器的交互操作  1- 15000
     */

    void operate() throws InterruptedException {
//        Thread.sleep(5);
        // 1. 机器人指令处理
        List<Robot> releaseRobots = new ArrayList<>();
        for (int i = 0; i < ROBOT_NUM; i++) {
            try {
                boolean needReleaseLock = false;
                locks[i].lock();
                Robot robot = robots.get(i);
                if ((robot.robotState == RobotState.BORING || robot.robotState == RobotState.FINDING_GOOD || robot.robotState == RobotState.GO_BERTH) && robot.status == 1) {
                    if (robot.robotState == RobotState.BORING) {
                        // 空闲状态 等待指令状态中 状态变更由指令计算线程完成
                        findTargetGood();
                    } else if (robot.robotState == RobotState.FINDING_GOOD && !robot.instructionsV2.isEmpty()) {
                        needReleaseLock = robot.move(robot.instructionsV2.getFirst(), collision, map);
                        if (!robot.instructionsV2.isEmpty() && robot.instructionsV2.getFirst().x == -1 && robot.instructionsV2.getFirst().y == -1){
                            Instruction.getGood(robot.id);
                            robot.instructionsV2.remove();
                        }
                    } else if (robot.robotState == RobotState.FINDING_GOOD && robot.instructionsV2.isEmpty()) {
                        // 变更为前往泊位状态
                        robot.robotState = RobotState.GO_BERTH;
                    } else if (robot.robotState == RobotState.GO_BERTH) {
                        // 取出当前节点的路径信息
//                        PointMessageV2 message = mapMessage.getOrDefault(new MapNode(robot.x, robot.y), null);
                        if (berths.get(RebalanceFloodFill.allocation[robot.id]).isClose){
                            int minDist = Integer.MAX_VALUE;
                            for(int j = 0; j< BERTH_NUM; j++){
                                if (!berths.get(j).isClose){
                                    PointMessageV2 message = singleMapMessage[j].getOrDefault(new MapNode(robot.x, robot.y), null);
                                    if (message != null && message.DistToBerth < minDist){
                                        minDist = message.DistToBerth;
                                        RebalanceFloodFill.allocation[robot.id] = j;
                                    }
                                }
                            }
                        }
                        PointMessageV2 message = singleMapMessage[RebalanceFloodFill.allocation[robot.id]].getOrDefault(new MapNode(robot.x, robot.y), null);
                        if (message == null) {
                            // 到达泊位 变更为空闲状态
//                        Instruction.pullGood(i);
                            robot.robotState = RobotState.BORING;
                        } else {
                            switch (message.actionCode) {
                                case UP:
                                    robot.move(new Coord(robot.x - 1, robot.y), collision, map);
                                    break;
                                case RIGHT:
                                    robot.move(new Coord(robot.x, robot.y + 1), collision, map);
                                    break;
                                case DOWN:
                                    robot.move(new Coord(robot.x + 1, robot.y), collision, map);
                                    break;
                                case LEFT:
                                    robot.move(new Coord(robot.x, robot.y - 1), collision, map);
                                    break;
                                case PULL:
                                    if (robot.goods == 1) Instruction.pullGood(i);
                                    robot.instructionsV2.clear();
                                    berths.get(message.berthId).addGood(robot.price);
                                    robot.robotState = RobotState.BORING;
                                    break;
                            }
                            // 再看一眼

                        }
                    }
                } else {
                    // 异常状态 清空所有状态信息重新计算
                    robot.instructionsV2.clear();
                    robot.robotState = RobotState.BORING;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                locks[i].unlock();
            }


        }
// ------------------------------------------------------------------------------------------------------------------------------
        // 2. 船指令
        for (int i = 0; i < boats.size(); i++) {
            Boat boat = boats.get(i);
            switch (boat.status) {
                case 0:
                    // 移动中
                    break;
                case 1:
                    // 正常运行状态
                    switch (boat.pos) {
                        case -1:
                            // 卸货（寻找泊位）
                            boat.loadedGoodsNum = 0;
                            getTargetBerth(i, 0);
                            break;
                        default:
                            // 在泊位装货
                            int x = boat2Berth[i];
                            if (x != -1) {
                                Berth berth = berths.get(x);

                                // 装货
                                {
                                    // 如果单次装货货物超出最大容量
                                    if (berth.loading_speed + boat.loadedGoodsNum > boat.capacity) {
                                        berth.removeGoods(boat.capacity - boat.loadedGoodsNum);
                                        boat.loadedGoodsNum = boat.capacity;
                                    }
                                    // 如果泊位剩余货物不足单次装货数量
                                    else if (berth.loading_speed > berth.goodNums) {
                                        boat.loadedGoodsNum += berth.goodNums;
                                        berth.clearAllGoods();
                                    }
                                    // 普通一帧内装货
                                    else {
                                        boat.loadedGoodsNum += berth.loading_speed;
                                        berth.removeGoods(berth.loading_speed);
                                    }
                                }

                                // 船满了，或者没时间了，去虚拟点
                                if (boat.loadedGoodsNum >= boat.capacity || MAX_FRAME - this.currentFrameId <= berth.transportTime + 1) {
                                    if (MAX_FRAME - this.currentFrameId <= berth.transportTime + 1){
                                        berth.isClose = true;
                                    }
                                    Instruction.go(i);
                                    berth2Boat[x] = -1;
                                    boat2Berth[i] = -1;
                                    boat.state = 0;
                                }
                                // 泊位空了，去下一个泊位
                                else if (berth.goodNums == 0) {
                                    if(boat.loadedGoodsNum >= boat.capacity * MyConfig.goToMoney){
                                        // 如果来不及去其他泊位了，就停在当前港口，直到最后一刻
                                        if(MAX_FRAME - this.currentFrameId <= 500 + berth.transportTime){
                                            break;
                                        }
                                        else{
                                            Instruction.go(i);
                                            berth2Boat[x] = -1;
                                            boat2Berth[i] = -1;
                                            boat.state = 0;
                                        }

                                    }else{
                                        getTargetBerth(i, 1);
                                    }
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
    }


    @Override
    public void run() {
        init();
//        interactBefore();
        String okk = in.nextLine();
        System.out.println("OK");
        System.out.flush();
        for (int i = 0; i < MAX_FRAME; i++) {
            try {
                step();
            } catch (Exception e) {
                // 不做异常处理 只是为了保证15000次循环能执行才做的异常捕获
                e.printStackTrace();
            }
//            if(i % 2000 == 0){
//                for(int j = 0; j< BERTH_NUM; j++){
//                    System.out.println("BerthID:" + j + "  goodsNums:" + berths.get(j).goodNums + "   totalPrice: " + berths.get(j).totalPrice);
//                }
//            }
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
            robots.add(new Robot(i, RobotState.GO_BERTH));
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
        this.singleMapMessage = RebalanceFloodFill.getSinglePointMessage(map, berths);
        this.mapMessage = RebalanceFloodFill.getPointMessage(map, berths);
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
            good.frameId = this.currentFrameId;
            for (int z = 0; z < BERTH_NUM; z++) {
                PointMessageV2 message = singleMapMessage[z].getOrDefault(new MapNode(good.x, good.y), null);
                if (message != null) {
                    good.costBenefitRatio[z] = (double) good.price / message.DistToBerth;
                    // disGoodList.get(message.berthId).add(good);
                }else{
                    good.costBenefitRatio[z] = -1;
                }
            }
            allGoods.add(good);
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

        int max = 0;
        int diff = Integer.MAX_VALUE;
        for (int j = 0; j < BERTH_NUM; j++) {
            Berth berth = berths.get(j);
//            if (berth2Boat[j] != -1 || (berth.totalPrice < max )) continue;
            if (berth2Boat[j] != -1) continue;
            switch (situation) {
                case 0:
                    // 在虚拟点时，找最多的
                    if (berth.totalPrice < max) continue;
                    // 优先找到一个能来回的泊位
                    if (RebalanceFloodFill.areas[j] != 0 && MAX_FRAME - this.currentFrameId >= berth.transportTime * 2  + 1) {
                        max = berth.totalPrice;
                        target = berth.id;
                    }
                    break;
                case 1:
                    // 在泊位时，找与capacity差距最小的
                    if (Math.abs(boat.capacity - (berth.goodNums + boat.loadedGoodsNum)) > diff) continue;
                    if ((berth.goodNums + boat.loadedGoodsNum >= boat.capacity * MyConfig.changeBerthCapacity) && MAX_FRAME - this.currentFrameId >= 500 + berth.transportTime + 1) {
                        max = berth.totalPrice;
                        target = berth.id;
                        diff = Math.abs(boat.capacity - (berth.goodNums + boat.loadedGoodsNum));
                    }
                    break;
            }
        }


        if (target == -1) return;

        // 没时间了！赶紧送货！！！
        /*if ((MAX_FRAME - this.currentFrameId <= 500 + berths.get(target).transportTime + 5) && (boat.loadedGoodsNum != 0)) {
            Instruction.go(i);
            if (boat2Berth[i] != -1) {
                berth2Boat[boat2Berth[i]] = -1;
                boat2Berth[i] = -1;
            }
            boats.get(i).state = 0;
            return;
        }*/


        // 最后一次移动时，关闭离开的泊位
        Berth targetBerth = berths.get(target);
        if (MAX_FRAME - this.currentFrameId < targetBerth.transportTime + 500 + Math.min(targetBerth.goodNums ,boat.capacity - boat.loadedGoodsNum) / targetBerth.loading_speed ){
            if (boat2Berth[i] != -1) {
               berths.get(boat2Berth[i]).isClose = true;
            }
        }
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
