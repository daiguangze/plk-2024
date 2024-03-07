package model;

public class Berth {
    int id;
    int x;
    int y;
    int transport_time;
    int loading_speed;

    public Berth() {
    }

    public Berth(int id,int x, int y, int transport_time, int loading_speed) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.transport_time = transport_time;
        this.loading_speed = loading_speed;
    }
}