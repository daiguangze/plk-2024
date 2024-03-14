package util.floodfill;

public class PointMessage{
    /**
     * 所属泊位id
     */
    public int berthId;

    /**
     * 下一步行动指令 1234 上右下左
     */
    public int actionCode;

    public PointMessage(int berthId, int actionCode) {
        this.berthId = berthId;
        this.actionCode = actionCode;
    }
}