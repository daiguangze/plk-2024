package util.floodfill;

public class MapNode {
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
