package instruction;

public class Instruction {


    public static void up(int robotId) {
        System.out.println(String.format("move %d 2", robotId));
    }

    public static void down(int robotId) {
        System.out.println(String.format("move %d 3", robotId));
    }

    public static void left(int robotId) {
        System.out.println(String.format("move %d 1", robotId));
    }

    public static void right(int robotId) {
        System.out.println(String.format("move %d 0", robotId));
    }

    public static void getGood(int robotId) {
        System.out.println(String.format("get %d", robotId));
    }


}
