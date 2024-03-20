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

    /**
     * 到达泊位点的最短路径
     */
    public int DistToBerth;

    public PointMessage(int berthId, int actionCode, int dist) {
        this.berthId = berthId;
        this.actionCode = actionCode;
        this.DistToBerth = dist;
    }
}