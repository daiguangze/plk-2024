package util;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.*;

public class AStar {

/*
    void aStarSearch(char[][] graph , Node start , Node goal){
        // 所有周边节点
        PriorityQueue<Node> frontier = new PriorityQueue<>();
        frontier.add(start);

        // 路径来向
        Map<Node,Node> cameFrom = new HashMap<>();
        // 节点代价
        Map<Node,Integer> costSoFar = new HashMap<>();
        cameFrom.put(start,null);
        costSoFar.put(start,0);

        while(!frontier.isEmpty()){
            // 从优先队列中取出一个元素
            Node current = frontier.peek();
            // 找到目标
            if (current.equals(goal)) break;

        }
    }
 */


    class Coord{
        int x ;
        int y ;

        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            // 检查引用, 一样就直接返回true
            if (obj == this) return true;
            // 使用instanceof 检查参数类型  为了效率不检查了..
            // if (! (obj instanceof Node)) return false;
            Coord node = (Coord)obj;
            // 检查 x y
            return this.x == node.x && this.y == node.y;
        }
    }

    class Node implements Comparable<Node>{

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
        public int Cost;

        /**
         * 预估代价
         */
        public int estimatedCost;

        public Node(int x, int y){
            this.coord = new Coord(x,y);
        }

        public Node(Coord coord, Node parent, int cost, int estimatedCost) {
            this.coord = coord;
            this.parent = parent;
            Cost = cost;
            this.estimatedCost = estimatedCost;
        }

        @Override
        public int compareTo(Node o) {
            return 0;
        }
    }

}