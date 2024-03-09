package model;

public class Boat {
    /**
     * 船id (暂时没用)
     */
    public int id;

    /**
     * 目标泊位, 虚拟则为-1
     */
    public int pos;

    /**
     * 状态 (0:表示移动中,1:正常运行,2:泊位外等待
     */
    public int status;


}