package util.floodfill;

import enums.RobotActionCode;
import model.Berth;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * 平衡洪水算法
 * 添加新特性:
 * 1. 会做一轮平衡操作
 * 2. 会计算出新的机器人泊位对应关系
 */
public class RebalanceFloodFill {

    /**
     * 弃用的泊位列表
     */
    public static List<Integer> ignoreIds = new ArrayList<>();

    /**
     * map 1 :  [1870, 1613, 987, 545, 283, 7119, 7246, 7526, 7843, 38]  sum = 35070 avg = 3507       1500
     * map 2 :  [5495, 2804, 1693, 1862, 176, 1340, 1604, 7024, 1079, 3326]  sum = 26403 avg = 2640    1500
     * map 3 :  [3461, 1536, 1773, 4016, 1710, 5919, 58, 391, 2066, 6676]  sum = 27606 avg = 2760    1500
     * map 4 :  [438, 541, 3657, 3673, 2383, 2336, 3674, 3657, 539, 438]  sum = 21336               1500
     * map 5 :  [3428, 3515, 3429, 3484, 2933, 2868, 3422, 3320, 3301, 4038] sum = 33738            1500
     * map 6 :  [3887, 2610, 1816, 4125, 3325, 3343, 3953, 2023, 2566, 3697] sum = 31345            1500
     * map 7 :  [1070, 2139, 2336, 3006, 1783, 3305, 2496, 3339, 2468, 1034] sum = 22976            1500
     * map 8 :  [629, 1247, 2917, 393, 4445, 2496, 960, 1017, 2427, 1480] sum = 18011               1500
     * 目标 将map 1
     */
    public static int[] areas = new int[10];

    /**
     * 机器人对应的泊位
     * 默认为 index对应
     */
    public static int[] allocation = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File(FloodFill.class.getResource("/").toString().substring(6) + "\\maps\\map5.txt"));
        char[][] maps = new char[210][210];
        List<Berth> berths = new ArrayList<>();
        Map<MapNode, PointMessageV2> ans;
        for (int i = 0; i < 200; i++) {
            String row = in.nextLine();
            maps[i] = row.toCharArray();
        }
        for (int i = 0; i < 10; i++) {
            berths.add(new Berth(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt()));
        }
        Map<MapNode, PointMessageV2>[] aaa = getSinglePointMessage(maps, berths);
        ans = getPointMessage(maps, berths);
        for (int xixi : areas) System.out.print(xixi + " ");
        System.out.println();
        for (int xixi : allocation) System.out.print(xixi + " ");

        System.out.println();
    }

    public static Map<MapNode, PointMessageV2> getPointMessage(char[][] maps, List<Berth> berths) {
        Map<MapNode, PointMessageV2> ans = new HashMap<>();
        int rebalanceNum = 2;
        for (int p = 0; p < rebalanceNum; p++) {
            // 1. 初始化visited
            int[][] visited = new int[maps.length][maps.length];
            for (int i = 0; i < visited.length; i++) {
                for (int j = 0; j < visited.length; j++) visited[i][j] = -1;
            }
            // 2. 初始化队列
            ArrayDeque<MapNode>[] queues = new ArrayDeque[berths.size()];
            for (int i = 0; i < queues.length; i++) {
                queues[i] = new ArrayDeque<>();
            }
            // 3.放入初始节点
            for (int i = 0; i < 10; i++) {
                // 忽略的节点不做操作
                if (ignoreIds.contains(i)) continue;
                int x = berths.get(i).x;
                int y = berths.get(i).y;
                // 泊位4*4 题目给你的为左上角数据.
                queues[i].addFirst(new MapNode(x, y));
//                queues[i].addFirst(new MapNode(x, y + 1));
//                queues[i].addFirst(new MapNode(x, y + 2));
//                queues[i].addFirst(new MapNode(x, y + 3));
//                queues[i].addFirst(new MapNode(x + 1, y));
//                queues[i].addFirst(new MapNode(x + 2, y));
//                queues[i].addFirst(new MapNode(x + 3, y));
//                queues[i].addFirst(new MapNode(x + 1, y + 3));
//                queues[i].addFirst(new MapNode(x + 2, y + 3));
//                queues[i].addFirst(new MapNode(x + 3, y + 3));
//                queues[i].addFirst(new MapNode(x + 3, y + 1));
//                queues[i].addFirst(new MapNode(x + 3, y + 2));
                ans.put(new MapNode(x, y), new PointMessageV2(i, RobotActionCode.PULL, 0));
                maps[x][y] = 'O';
//                ans.put(new MapNode(x, y + 1), new PointMessageV2(i, RobotActionCode.PULL, 0));
//                ans.put(new MapNode(x, y + 2), new PointMessageV2(i, RobotActionCode.PULL, 0));
//                ans.put(new MapNode(x, y + 3), new PointMessageV2(i, RobotActionCode.PULL, 0));
//                ans.put(new MapNode(x + 1, y), new PointMessageV2(i, RobotActionCode.PULL, 0));
//                ans.put(new MapNode(x + 2, y), new PointMessageV2(i, RobotActionCode.PULL, 0));
//                ans.put(new MapNode(x + 3, y), new PointMessageV2(i, RobotActionCode.PULL, 0));
//                ans.put(new MapNode(x + 1, y + 3), new PointMessageV2(i, RobotActionCode.PULL, 0));
//                ans.put(new MapNode(x + 2, y + 3), new PointMessageV2(i, RobotActionCode.PULL, 0));
//                ans.put(new MapNode(x + 3, y + 3), new PointMessageV2(i, RobotActionCode.PULL, 0));
//                ans.put(new MapNode(x + 3, y + 1), new PointMessageV2(i, RobotActionCode.PULL, 0));
//                ans.put(new MapNode(x + 3, y + 2), new PointMessageV2(i, RobotActionCode.PULL, 0));
                areas[i] += 1;
            }

            // 4. 广搜
            while (!isEnd(queues)) {
                for (int i = 0; i < berths.size(); i++) {
                    ArrayDeque<MapNode> queue = queues[i];
                    // 取出全部节点
                    if (!queue.isEmpty()) {
                        List<MapNode> mapNodes = new ArrayList<>();
                        while (!queue.isEmpty()) mapNodes.add(queue.removeFirst());
                        for (MapNode mapNode : mapNodes) {
                            addFrontiers(maps, visited, mapNode, queue, i, ans, true);
                        }
                    }
                }
            }

            // 5. 重平衡
            if (p == 0) {
                int sum = 0;
                int avg = 0;
                for (int i = 0; i < 10; i++) {
                    sum += areas[i];
                }
                avg = sum / 10;
                for (int i = 0; i < 10; i++) {
                    if (areas[i] < avg - 1500) {
                        areas[i] = Integer.MAX_VALUE;
                        ignoreIds.add(i);
                    }
                }
                while (ignoreIds.size() > 0) ignoreIds.remove(ignoreIds.size() - 1);
                while (ignoreIds.size() < 0) {
                    // 最少也会塞2个进去
                    int min = areas[0];
                    int index = 0;
                    for (int i = 0; i < 10; i++) {
                        if (min > areas[i]) {
                            min = areas[i];
                            index = i;
                        }
                    }
                    areas[index] = Integer.MAX_VALUE;
                    ignoreIds.add(index);
                }
                ans.clear();
                Arrays.fill(areas, 0);

            }

        }

        // 得出绑定关系
        int[] temp = Arrays.copyOf(areas, 10);
        for (int p = 0; p < ignoreIds.size(); p++) {
            int max = temp[0];
            int index = 0;
            for (int i = 0; i < 10; i++) {
                if (max < temp[i]) {
                    max = temp[i];
                    index = i;
                }
            }
            // 将 ignoreid中的机器人分配给这个比较多的区域
            temp[index] /= 2;
            allocation[ignoreIds.get(p)] = index;
        }
        return ans;
    }


    /**
     * 单个节点染色
     */
    public static Map<MapNode, PointMessageV2>[] getSinglePointMessage(char[][] maps, List<Berth> berths) {

        Map<MapNode, PointMessageV2>[] ansAll = new Map[10];
        for (Map<MapNode, PointMessageV2> a : ansAll) {
            a = new HashMap<>();
        }


        for (int z = 0; z < 10; z++) {
            Map<MapNode, PointMessageV2> ans = new HashMap<>();
            // 1. 初始化visited
            int[][] visited = new int[maps.length][maps.length];
            for (int i = 0; i < visited.length; i++) {
                for (int j = 0; j < visited.length; j++) visited[i][j] = -1;
            }
            // 2. 初始化队列
            ArrayDeque<MapNode> queue = new ArrayDeque();
            // 3.放入初始节点
            // 忽略的节点不做操作
            if (ignoreIds.contains(z)) continue;
            int x = berths.get(z).x;
            int y = berths.get(z).y;
            // 泊位4*4 题目给你的为左上角数据.
            queue.addFirst(new MapNode(x, y));
//            queue.addFirst(new MapNode(x, y + 1));
//            queue.addFirst(new MapNode(x, y + 2));
//            queue.addFirst(new MapNode(x, y + 3));
//            queue.addFirst(new MapNode(x + 1, y));
//            queue.addFirst(new MapNode(x + 2, y));
//            queue.addFirst(new MapNode(x + 3, y));
//            queue.addFirst(new MapNode(x + 1, y + 3));
//            queue.addFirst(new MapNode(x + 2, y + 3));
//            queue.addFirst(new MapNode(x + 3, y + 3));
//            queue.addFirst(new MapNode(x + 3, y + 1));
//            queue.addFirst(new MapNode(x + 3, y + 2));
            ans.put(new MapNode(x, y), new PointMessageV2(z, RobotActionCode.PULL, 0));
            maps[x][y] = 'O';
//            ans.put(new MapNode(x, y + 1), new PointMessageV2(z, RobotActionCode.PULL, 0));
//            ans.put(new MapNode(x, y + 2), new PointMessageV2(z, RobotActionCode.PULL, 0));
//            ans.put(new MapNode(x, y + 3), new PointMessageV2(z, RobotActionCode.PULL, 0));
//            ans.put(new MapNode(x + 1, y), new PointMessageV2(z, RobotActionCode.PULL, 0));
//            ans.put(new MapNode(x + 2, y), new PointMessageV2(z, RobotActionCode.PULL, 0));
//            ans.put(new MapNode(x + 3, y), new PointMessageV2(z, RobotActionCode.PULL, 0));
//            ans.put(new MapNode(x + 1, y + 3), new PointMessageV2(z, RobotActionCode.PULL, 0));
//            ans.put(new MapNode(x + 2, y + 3), new PointMessageV2(z, RobotActionCode.PULL, 0));
//            ans.put(new MapNode(x + 3, y + 3), new PointMessageV2(z, RobotActionCode.PULL, 0));
//            ans.put(new MapNode(x + 3, y + 1), new PointMessageV2(z, RobotActionCode.PULL, 0));
//            ans.put(new MapNode(x + 3, y + 2), new PointMessageV2(z, RobotActionCode.PULL, 0));
            // 4. 广搜
            while (!queue.isEmpty()) {
                // 取出全部节点
                if (!queue.isEmpty()) {
                    List<MapNode> mapNodes = new ArrayList<>();
                    while (!queue.isEmpty()) mapNodes.add(queue.removeFirst());
                    for (MapNode mapNode : mapNodes) {
                        addFrontiers(maps, visited, mapNode, queue, z, ans, false);
                    }
                }
            }
            ansAll[z] = ans;
        }
        return ansAll;
    }


    private static void addFrontiers(char[][] maps, int[][] visited, MapNode mapNode, ArrayDeque<MapNode> queue, int i, Map<MapNode, PointMessageV2> ans, boolean flag) {
        int x = mapNode.x;
        int y = mapNode.y;
        // 当前点的message
        PointMessageV2 messageV2 = ans.getOrDefault(new MapNode(x, y), null);
        int dist = 0; // 减400防止溢出
        if (messageV2 != null) {
            // 当前点到泊位的路程距离
            dist = messageV2.DistToBerth;
        }

        // 下   , 路径指向上
        if (isValid(maps, visited, x + 1, y)) {
            visited[x + 1][y] = i;
            queue.addFirst(new MapNode(x + 1, y));
            ans.put(new MapNode(x + 1, y), new PointMessageV2(i, RobotActionCode.UP, dist + 1));
            if (flag) areas[i] += 1;
        }
        // 右
        if (isValid(maps, visited, x, y + 1)) {
            visited[x][y + 1] = i;
            queue.addFirst(new MapNode(x, y + 1));
            ans.put(new MapNode(x, y + 1), new PointMessageV2(i, RobotActionCode.LEFT, dist + 1));
            if (flag) areas[i] += 1;
        }
        // 上
        if (isValid(maps, visited, x - 1, y)) {
            visited[x - 1][y] = i;
            queue.addFirst(new MapNode(x - 1, y));
            ans.put(new MapNode(x - 1, y), new PointMessageV2(i, RobotActionCode.DOWN, dist + 1));
            if (flag) areas[i] += 1;
        }
        // 左
        if (isValid(maps, visited, x, y - 1)) {
            visited[x][y - 1] = i;
            queue.addFirst(new MapNode(x, y - 1));
            ans.put(new MapNode(x, y - 1), new PointMessageV2(i, RobotActionCode.RIGHT, dist + 1));
            if (flag) areas[i] += 1;
        }


    }

    private static boolean isValid(char[][] maps, int[][] visited, int x, int y) {
        if (x < 0 || x >= maps.length) return false;
        if (y < 0 || y >= maps[0].length) return false;
        if (visited[x][y] != -1) return false;
        if (maps[x][y] != '.') {
            if (maps[x][y] != 'A'){
                if (maps[x][y] != 'B') return false;
            }
        }
        return true;
    }

    private static boolean isEnd(ArrayDeque<MapNode>[] queues) {
        for (ArrayDeque deque : queues) {
            if (!deque.isEmpty()) return false;
        }
        return true;
    }


}
