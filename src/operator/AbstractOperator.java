package operator;

import model.Berth;
import model.Boat;
import model.Good;
import model.Robot;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class AbstractOperator implements Operator{

    /**
     * 地图 固定 200 * 200
     */
    char[][] map = new char[MAP_SIZE+10][MAP_SIZE+10];

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



    abstract void getBerthsAfter();

    abstract void runBefore();



    abstract void stepOperate() throws InterruptedException;


    @Override
    public void run() {
        init();
        runBefore();
        for (int i = 0; i < 15000; i++) {
            try {
                step();
            }catch (Exception e){

            }
        }
    }

    private void step() throws InterruptedException {
        // 1. 读取
        // 第一行输入2个整数,表示帧序号, 当前金钱
        int frameId = stepRead();
        // 2. 操作
        stepOperate();
        // 3. ok
        System.out.println("OK");
        // 4. 清空缓存区
        System.out.flush();
    }


    /**
     * 每一帧交互
     * 第一行输入两个整数, 表示帧序号 (从1开始递增)、 当前金钱数
     * 第二行输入一个整数, 表示场上新增货物的数量 k [0,10]
     * 紧接着K行数据,每一行表示一个新增货物, 分别由如下所示数据构成
     */
    private int stepRead(){
        // 帧id
        int frameId = in.nextInt();
        // 当前金钱
        int money = in.nextInt();
        System.out.println(frameId + " "  + money);
        // 新增货物数量
        int k = in.nextInt();
        for (int i = 0; i < k; i++) {
            Good good = new Good(in.nextInt(),in.nextInt(),in.nextInt());
            goods.add(good);
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

        return frameId;
    }



    private void init() {
        // 1.读取地图
        getMap();
        // 2.读取泊位
        getBerths();
        getBerthsAfter();
        // 3.读取船容量
        getBoatCapacity();
        // 4. 读取结束
        // 5. 先把机器人初始化了先
        for(int i = 0 ; i < ROBOT_NUM ; i++){
            robots.add(new Robot());
        }
        // 6. 先把船初始化了先
        for (int i = 0; i < BOAT_NUM; i++) {
            boats.add(new Boat());
        }
        in.nextLine();
        String okk = in.nextLine();
        System.out.println("OK");
        System.out.flush();
    }

    private void getMap() {
        // 读取地图
        for (int i = 0; i < MAP_SIZE; i++) {
            String row = in.nextLine();
            map[i] = row.toCharArray();
        }
    }

    private void getBerths() {
        for(int i = 0; i < BERTH_NUM ;i++){
            berths.add(new Berth(in.nextInt(),in.nextInt(),in.nextInt(),in.nextInt(),in.nextInt()));
        }
    }

    private void getBoatCapacity(){
        this.boatCapacity = in.nextInt();
    }
}
