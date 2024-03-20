package util.astar;
import java.util.*;

public class AStarV2 {

    /**
     * 绘制地图中,代表障碍的值
     */
    public char notBar;

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
    Queue<Node> openList = new PriorityQueue<>();

    /**
     * 保存节点
     */
    List<Node> closeList = new ArrayList<>();


    /**
     * 指令命令
     */
    public Stack<Coord> instructions = new Stack<>();



    public AStarV2(char notBar) {
        this.notBar = notBar;
    }

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
        if (map.maps[x][y] != notBar) return false;
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
     * 判断坐标是否在close表中
     */
    private boolean isCoordInClose(Coord coord)
    {
        return coord!=null&&isCoordInClose(coord.x, coord.y);
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
    private Node findNodeInOpen(Coord coord) {
        if (coord == null || openList.isEmpty()) return null;
        for(Node node : openList){
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
        Node end = map.end;
        // 个人认为直接判断加入到优先队列即可
        if (canAddNodeToFrontier(map,x,y)){
            Coord coord = new Coord(x,y);
            int cost = current.cost + oneStepCost;
            Node child = findNodeInOpen(coord);
            // 若不在openlist中
            if (child == null){
                // 计算预估代价
                int estimatedCost = calEstimatedCost(end.coord,coord);
                if (isEndNode(end.coord,coord)){
                    child = end;
                    child.parent = current;
                    child.cost = cost;
                    child.estimatedCost = estimatedCost;
                }else{
                    child = new Node(coord,current,cost,estimatedCost);
                }
                openList.add(child);
            }else if(child.cost > cost){
                // ? 重新调整代价 感觉没必要啊
                child.cost = cost;
                child.parent = current;
                openList.add(child);
            }
        }
    }

    public void start(MapInfo map){
        if (map == null) return;
        // clean
        openList.clear();
        closeList.clear();
        instructions.clear();
        // find
        openList.add(map.start);
        moveNodes(map);
    }

    private void moveNodes(MapInfo map) {
        while(!openList.isEmpty()){
            Node current = openList.poll();
            closeList.add(current);
            addFrontierNode(map,current);
            if (isCoordInClose(map.end.coord)){
                // 绘制地图
                generateInstruction(map.end);
                break;
            }
        }
    }

    /**
     * 生成移动路径 更改为存放坐标,便于防止碰撞
     * @param end
     */
    private void generateInstruction(Node end){
        Node p = end;
        instructions.push(p.coord);
        while(p.parent != null){
            Node parent = p.parent;
            instructions.push(parent.coord);
            p = p.parent;
        }
        instructions.pop();
    }

}
