package util;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.*;

public class AStar {


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
            Node current = frontier.peek();
        }
    }


    class Node{
        int x ;
        int y ;
    }

}