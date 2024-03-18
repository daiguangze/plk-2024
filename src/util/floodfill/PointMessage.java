package util.floodfill;

public class PointMessage{
    /**
     * 所属泊位id
     */
    public int berthId;

    /**
     * 下一步行动指令 12345 上右下左放货
     */
    public int actionCode;

    public PointMessage(int berthId, int actionCode) {
        this.berthId = berthId;
        this.actionCode = actionCode;
    }
}