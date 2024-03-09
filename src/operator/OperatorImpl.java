package operator;

import instruction.Instruction;
import model.Berth;
import model.Boat;
import model.Good;
import model.Robot;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OperatorImpl implements Operator {


    /**
     * 地图 固定 200 * 200
     */
    char[][] map = new char[MAP_SIZE][MAP_SIZE];

    /**
     * 泊位 固定10个
     */
    Berth[] berths = new Berth[BERTH_NUM];

    /**
     * 船  固定 5 个
     */
    List<Boat> boats = new ArrayList<>();

    List<Good> goods = new ArrayList<>();

    List<Robot> robots = new ArrayList<>();

    /**
     * 船容量
     */
    int boatCapacity;

    Scanner in;


    /**
     * 单例
     */

    public OperatorImpl(Scanner in){
        this.in = in ;
    }



    /**
     * 初始化: 选手程序初始化时,将按序输入
     * 1. 200 * 200 的字符组成的地图数据
     * 2. 10行的泊位数据  ( 5个数据, 分别代表 (id,x,y,time,velocity) )
     * 3. 1行的船的容积   ( 1个数据, 代表capacity)
     */
    @Override
    public void init() {
        // 1.读取地图
        getMap();
        // 2.读取泊位
        getBerths();
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
        String okk = in.nextLine();
        System.out.println("OK");
        System.out.flush();
    }

    @Override
    public void run() {
        for (int i = 0; i < 15000; i++) {
            step();
        }
    }

    /**
     * 每一帧操作包括
     *  1. 读取判题器信息
     *  2. 给出机器人行动信息
     */
    @Override
    public void step() {
        // 1. 读取
        // 第一行输入2个整数,表示帧序号, 当前金钱
        int frameId = stepRead();
        // 2. 操作
        stepOperate();
        // 3. 清空缓存区
        System.out.flush();
    }

    private void stepOperate() {
        // move 0:右 1:左 2:上 3:下
        Instruction.right(0);
        Instruction.right(1);
        Instruction.right(2);
        Instruction.right(3);
        Instruction.right(4);
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

        return frameId;
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
            berths[i] = new Berth(in.nextInt(),in.nextInt(),in.nextInt(),in.nextInt(),in.nextInt());
        }
    }

    private void getBoatCapacity(){
        this.boatCapacity = in.nextInt();
    }
}
