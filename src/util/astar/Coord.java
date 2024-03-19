package util.astar;

public class Coord {
    int x;
    int y;

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
        Coord node = (Coord) obj;
        // 检查 x y
        return this.x == node.x && this.y == node.y;
    }

    @Override
    public int hashCode() {
        return x+y;
    }
}