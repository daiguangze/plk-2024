package util.astar;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.*;

public class AStar {

    /**
     * 绘制地图中,代表障碍的值
     */
    public char bar;

    /**
     * 绘制地图中,代表可走路径的值
     */
    public char path;

    /**
     * 移动代价 (上下左右)
     */
    public int directValue = 1;

    /**
     * 移动代价(斜移动)
     */
    public int obliqueValue = 1;

    /**
     * 边界节点
     */
    Queue<Node> frontier = new PriorityQueue<>();

    /**
     * 保存节点
     */
    List<Node> closeList = new ArrayList<>();


    /**
     * 判断节点是否为最终节点
     *
     * @param end   最终节点
     * @param coord 当前节点
     * @return 是否为最终节点
     */
    private boolean isEndNode(Coord end, Coord coord) {
        return coord != null && end.equals(coord);
    }

    /**
     * 判断是否可以加入  边界节点表
     *
     * @param map
     * @param x
     * @param y
     * @return
     */
    private boolean canAddNodeToFrontier(MapInfo map, int x, int y) {
        // 是否在地图中
        if (x < 0 || x >= map.width || y < 0 || y >= map.hight) return false;
        // 是否是不可通过的节点
        if (map.maps[x][y] != path) return false;
        // 是否在close表
        if (isCoordInClose(x, y)) return false;
        return true;
    }

    /**
     * 判断节点是否在close中
     */
    private boolean isCoordInClose(int x, int y) {
        if (closeList.isEmpty()) return false;
        for(Node node : closeList){
            if (node.coord.x == x && node.coord.y == y) return true;
        }
        return false;
    }

    /**
     * 计算预估损失
     */
    private int calEstimatedCost(Coord end, Coord coord){
        return (Math.abs(end.x - coord.x) + Math.abs(end.y - coord.y)) * directValue;
    }

    /**
     * 在frontier中寻找下一个节点
     */
    private Node findNodeInFrontier(Coord coord) {
        if (coord == null || frontier.isEmpty()) return null;
        for(Node node : frontier){
            if (node.coord.equals(coord)) return node;
        }
        return null;
    }

    /**
     * 添加当前节点周边所有节点
     */
    private void addFrontierNode(MapInfo map , Node current){
        int x = current.coord.x;
        int y = current.coord.y;
        // 上下左右
        addFrontierNode(map,current,x-1,y,directValue);
        addFrontierNode(map,current,x,y-1,directValue);
        addFrontierNode(map,current,x+1,y,directValue);
        addFrontierNode(map,current,x,y+1,directValue);
    }

    /**
     * 添加一个节点到 frontier
     */
    private void addFrontierNode(MapInfo map,Node current,int x, int y , int oneStepCost){
        if(canAddNodeToFrontier(map,x,y)){
            Node end = map.end;
            Coord coord = new Coord(x,y);
            int cost = current.cost + oneStepCost;
            Node child = findNodeInFrontier(coord);
        }
    }


}