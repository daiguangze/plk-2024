package util.floodfill;

import enums.RobotActionCode;

public class PointMessageV2 {
    /**
     * 所属泊位id
     */
    public int berthId;

    /**
     * 下一步行动指令 12345 上右下左放货
     */
    public RobotActionCode actionCode;

    public PointMessageV2(int berthId, RobotActionCode actionCode) {
        this.berthId = berthId;
        this.actionCode = actionCode;
    }
}
