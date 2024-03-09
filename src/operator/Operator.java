package operator;

import java.util.Scanner;

public interface Operator {
    final int MAP_SIZE = 200;

    final int BERTH_NUM = 10;

    final int BOAT_NUM = 5;

    final int ROBOT_NUM = 10;


    public void init();

    public void run();

    public void step();


}
