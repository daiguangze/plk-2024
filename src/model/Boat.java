package model;

public class Boat {
    /**
     * 船id (暂时没用)
     */
    public int id;

    /**
     * 目标泊位, 虚拟则为-1 实时更新
     */
    public int pos;

    /**
     * 状态 (0:表示移动中,1:正常运行,2:泊位外等待 实时更新
     */
    public int status;

    /**
     * 扩展状态（0:卸货（寻找泊位），1：装货）
     */
    public int state;


    /**
     * 容量
     */
    public int capacity;

    /**
     * 装载货物数量
     */
    public int loadedGoodsNum;

    /**
     * 停留帧
     */
    public int stayFrame;



}