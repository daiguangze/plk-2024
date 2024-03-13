package util.floodfill;

import model.Berth;

import java.util.*;

/**
 * 洪水算法
 * 使用洪水算法进行染色 确定每个泊位所负责的范围
 */
public class FloodFill {

    public static Map<MapNode,String> getPointMessage(char[][] maps , List<Berth> berths) {
        Map<MapNode,String> ans = new HashMap<>();
        int[][] visited = new int[maps.length][maps.length];
        ArrayDeque<MapNode>[] queues = new ArrayDeque[berths.size()];
        // 放入初始节点

        // 广搜
        while(isEnd(queues)){
            for(int i = 0 ; i < berths.size() ; i++){
                ArrayDeque<MapNode> queue = queues[i];
                // 取出全部节点
                while(!queue.isEmpty()){
                    MapNode mapNode = queue.removeFirst();
                    addFrontiers(maps,visited,mapNode,queue,i,ans);
                }

            }
        }
        return ans;
    }

    private static void addFrontiers(char[][] maps, int[][] visited, MapNode mapNode,ArrayDeque<MapNode> queue,int i,Map<MapNode,String> ans) {
        int x =  mapNode.x;
        int y = mapNode.y;
        // 下
        if (isValid(maps,visited, x+1, y)){
            visited[x+1][y] = i;
            queue.addFirst(new MapNode(x+1,y));
        }
        // 右
        if (isValid(maps,visited, x, y+1)){
            visited[x][y+1] = i;
            queue.addFirst(new MapNode(x,y+1));
        }
        // 上
        if (isValid(maps,visited, x-1, y)){
            visited[x-1][y] = i;
            queue.addFirst(new MapNode(x-1,y));
        }
        // 左
        if (isValid(maps,visited, x, y-1)){
            visited[x][y-1] = i;
            queue.addFirst(new MapNode(x,y-1));
        }


    }

    private static boolean isValid(char[][] maps ,int[][] visited , int x, int y){
        if (x < 0 || x >= maps.length ) return false;
        if (y < 0 || y >= maps.length ) return false;
        if (visited[x][y] != 0) return false;
        if (maps[x][y] != '.') return false;
        return true;
    }
    private static boolean isEnd(ArrayDeque<MapNode>[] queues) {
        for(ArrayDeque deque : queues){
            if (!deque.isEmpty()) return false;
        }
        return true;
    }


}


class MapNode {
    public int x;
    public int y;

    public MapNode(int x, int y) {
        this.x = x;
        this.y = y;
    }

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

