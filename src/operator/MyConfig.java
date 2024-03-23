package operator;

public class MyConfig {
    /**
     * 容量总和为容量最大值的多少时，移动泊位
     */
    public static double changeBerthCapacity = 0.70;

    /**
     * 泊位装空了，并且装了多少货物的时候去虚拟点
     */
    public static double goToMoney = 0.9;

    /**
     * 关闭多少个港口(最多)
     */
    public static int closeBerthMax = 3;

    /**
     * 关闭多少个港口(至少)
     */
    public static int closeBerthMin = 1;
}
