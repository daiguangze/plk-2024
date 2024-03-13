package util.floodfill;

import model.Berth;

import java.util.*;

/**
 * 洪水算法
 * 使用洪水算法进行染色 确定每个泊位所负责的范围
 */
public class FloodFill {

    public static Map<MapNode,String> getPointMessage(char[][] maps , List<Berth> berths) {
        int[][] visited = new int[maps.length][maps.length];
        Deque<MapNode>[] queues = new ArrayDeque[berths.size()];
        // 放入初始节点

        // 广搜
        while(isEnd(queues)){
            for(int i = 0 ; i < berths.size() ; i++){
                // 取出全部节点
                // 加入周边合法节点
            }
        }
        return null;
    }

    private static boolean isEnd(Deque<MapNode>[] queues) {
        for(Deque deque : queues){
            if (deque.size() != 0) return false;
        }
        return true;
    }


}


class MapNode {
    public int x;
    public int y;

    @Override
    public boolean equals(Object obj) {
        MapNode node = (MapNode) obj;
        return node.x == this.x && node.y == this.y;
    }

    @Override
    public int hashCode() {
        return x+y;
    }
}

