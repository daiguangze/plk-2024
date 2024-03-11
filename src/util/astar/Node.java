package util.astar;

class Node implements Comparable<Node> {

    /**
     * 坐标
     */
    public Coord coord;

    /**
     * 父节点
     */
    public Node parent;

    /**
     * 起点到当前节点的代价
     */
    public int cost;

    /**
     * 预估代价
     */
    public int estimatedCost;

    public Node(int x, int y) {
        this.coord = new Coord(x, y);
    }

    public Node(Coord coord, Node parent, int cost, int estimatedCost) {
        this.coord = coord;
        this.parent = parent;
        this.cost = cost;
        this.estimatedCost = estimatedCost;
    }

    @Override
    public int compareTo(Node o) {
        if (o == null) return -1;
        if (estimatedCost + cost > o.estimatedCost + o.cost) return 1;
        else if (estimatedCost + cost < o.estimatedCost + o.cost) return -1;
        return 0;
    }
}