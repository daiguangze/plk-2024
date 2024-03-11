package util.astar;

public class MapInfo {
    /**
     * 地图数据
     */
    public char[][] maps;

    /**
     * 地图宽
     */
    public int width;

    /**
     * 地图高
     */
    public int hight;

    /**
     * 起始节点
     */
    public Node start;

    /**
     * 结束节点
     */
    public Node end;


    public MapInfo(char[][] maps, int width, int hight, Node start, Node end) {
        this.maps = maps;
        this.width = width;
        this.hight = hight;
        this.start = start;
        this.end = end;
    }
}
