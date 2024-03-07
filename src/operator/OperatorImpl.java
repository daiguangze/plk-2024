package operator;

import model.Berth;
import model.Boat;

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
    Boat[] boats = new Boat[BOAT_NUM];

    /**
     * 船容量
     */
    int boatCapacity;


    /**
     * 单例
     */
    private static final OperatorImpl INSTANCE = new OperatorImpl();

    private OperatorImpl(){}

    public static OperatorImpl getInstance(){
        return INSTANCE;
    }


    @Override
    public void init(Scanner in) {
        // 1.读取地图
        getMap(in);
        // 2.读取泊位
        getBerths(in);
        // 3.读取船容量
        getBoatCapacity(in);
        // 4. 读取结束
        String okk = in.nextLine();
        System.out.println("OK");
        System.out.flush();
    }

    private void getMap(Scanner in) {
        // 读取地图
        String[] mapTemp = new String[MAP_SIZE];
        for (int i = 0; i < MAP_SIZE; i++) {
            mapTemp[i] = in.nextLine();
        }
        // 转换为 char[][]
        for (int i = 0; i < MAP_SIZE; i++) {
            map[i] = mapTemp[i].toCharArray();
        }
    }

    private void getBerths(Scanner in) {
        for(int i = 0; i < BERTH_NUM ;i++){
            berths[i] = new Berth(in.nextInt(),in.nextInt(),in.nextInt(),in.nextInt(),in.nextInt());
        }
    }

    private void getBoatCapacity(Scanner in){
        this.boatCapacity = in.nextInt();
    }
}
