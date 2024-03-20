package util.floodfill;

import model.Berth;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * 洪水算法
 * 使用洪水算法进行染色 确定每个泊位所负责的范围
 */
public class FloodFill {

    // 测试
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File(FloodFill.class.getResource("/").toString().substring(6) + "\\testmap.txt"));
        char[][] maps = new char[210][210];
        List<Berth> berths = new ArrayList<>();
        Map<MapNode,PointMessage> ans;
        for(int i = 0 ; i < 200 ; i++){
            String row = in.nextLine();
            maps[i] = row.toCharArray();
        }
        for(int i = 0; i < 10 ;i++){
            berths.add(new Berth(in.nextInt(),in.nextInt(),in.nextInt(),in.nextInt(),in.nextInt()));
        }
        ans = getPointMessage(maps,berths);
        MapNode node = new MapNode(4, 1);
        PointMessage aaa = ans.getOrDefault(node, null);
        System.out.println(aaa.berthId + " "  + aaa.actionCode);
    }

    public static Map<MapNode,PointMessage> getPointMessage(char[][] maps , List<Berth> berths) {
        Map<MapNode,PointMessage> ans = new HashMap<>();
        int[][] visited = new int[maps.length][maps.length];
        for(int i = 0 ; i < visited.length ; i++){
            for(int j = 0 ; j < visited.length ; j++) visited[i][j] = -1;
        }
        ArrayDeque<MapNode>[] queues = new ArrayDeque[berths.size()];
        for(int i = 0 ; i < queues.length ; i++ ){
            queues[i] = new ArrayDeque<>();
        }
        // 放入初始节点
        for(int i = 0 ; i < 10 ; i++){
            int x = berths.get(i).x;
            int y = berths.get(i).y;
            // 泊位4*4 题目给你的为左上角数据.
            queues[i].addFirst(new MapNode(x,y));
            queues[i].addFirst(new MapNode(x,y+1));
            queues[i].addFirst(new MapNode(x,y+2));
            queues[i].addFirst(new MapNode(x,y+3));
            queues[i].addFirst(new MapNode(x+1,y));
            queues[i].addFirst(new MapNode(x+2,y));
            queues[i].addFirst(new MapNode(x+3,y));
            queues[i].addFirst(new MapNode(x+1,y+3));
            queues[i].addFirst(new MapNode(x+2,y+3));
            queues[i].addFirst(new MapNode(x+3,y+3));
            queues[i].addFirst(new MapNode(x+3,y+1));
            queues[i].addFirst(new MapNode(x+3,y+2));
            ans.put(new MapNode(x,y),new PointMessage(i,5));
            ans.put(new MapNode(x,y+1),new PointMessage(i,5));
            ans.put(new MapNode(x,y+2),new PointMessage(i,5));
            ans.put(new MapNode(x,y+3),new PointMessage(i,5));
            ans.put(new MapNode(x+1,y),new PointMessage(i,5));
            ans.put(new MapNode(x+2,y),new PointMessage(i,5));
            ans.put(new MapNode(x+3,y),new PointMessage(i,5));
            ans.put(new MapNode(x+1,y+3),new PointMessage(i,5));
            ans.put(new MapNode(x+2,y+3),new PointMessage(i,5));
            ans.put(new MapNode(x+3,y+3),new PointMessage(i,5));
            ans.put(new MapNode(x+3,y+1),new PointMessage(i,5));
            ans.put(new MapNode(x+3,y+2),new PointMessage(i,5));
        }

        long start = System.currentTimeMillis();
        // 广搜
        while(!isEnd(queues)){
            for(int i = 0 ; i < berths.size() ; i++){
                ArrayDeque<MapNode> queue = queues[i];
                // 取出全部节点
                if(!queue.isEmpty()){
                    List<MapNode> mapNodes = new ArrayList<>();
                    while(!queue.isEmpty()) mapNodes.add(queue.removeFirst());
                    for(MapNode mapNode : mapNodes){
                        addFrontiers(maps,visited,mapNode,queue,i,ans);
                    }
                }

            }
        }
        return ans;
    }

    private static void addFrontiers(char[][] maps, int[][] visited, MapNode mapNode,ArrayDeque<MapNode> queue,int i,Map<MapNode,PointMessage> ans) {
        int x =  mapNode.x;
        int y = mapNode.y;
        // 下   , 路径指向上
        if (isValid(maps,visited, x+1, y)){
            visited[x+1][y] = i;
            queue.addFirst(new MapNode(x+1,y));
            ans.put(new MapNode(x+1,y),new PointMessage(i,1));
        }
        // 右
        if (isValid(maps,visited, x, y+1)){
            visited[x][y+1] = i;
            queue.addFirst(new MapNode(x,y+1));
            ans.put(new MapNode(x,y+1),new PointMessage(i,4));
        }
        // 上
        if (isValid(maps,visited, x-1, y)){
            visited[x-1][y] = i;
            queue.addFirst(new MapNode(x-1,y));
            ans.put(new MapNode(x-1,y),new PointMessage(i,3));
        }
        // 左
        if (isValid(maps,visited, x, y-1)){
            visited[x][y-1] = i;
            queue.addFirst(new MapNode(x,y-1));
            ans.put(new MapNode(x,y-1),new PointMessage(i,2));
        }


    }

    private static boolean isValid(char[][] maps ,int[][] visited , int x, int y){
        if (x < 0 || x >= maps.length ) return false;
        if (y < 0 || y >= maps[0].length ) return false;
        if (visited[x][y] != -1) return false;
        if (maps[x][y] != '.'){
            if (maps[x][y] != 'A') return false;
        }
        return true;
    }
    private static boolean isEnd(ArrayDeque<MapNode>[] queues) {
        for(ArrayDeque deque : queues){
            if (!deque.isEmpty()) return false;
        }
        return true;
    }


}



