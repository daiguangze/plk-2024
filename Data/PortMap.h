//
// Created by k on 2024/3/13.
//

#ifndef PLK_2024_PORTMAP_H
#define PLK_2024_PORTMAP_H


#include <string>
#include <list>
#include "Berth.h"
#include "Robot.h"
#include "Boat.h"

class PortMap{
public:
    // 地图的大小
    static const int MAP_SIZE = 200;
    // 泊位个数
    static const int BERTH_NUM = 10;
    // 机器人个数
    static const int ROBOT_NUM = 10;
    // 船个数
    static const int BOAT_NUM = 5;

    // 船容量
    static int BoatCapacity;

    // 港口地图信息
    static char MapInfo[MAP_SIZE + 10][MAP_SIZE + 10];
    // 泊位信息
    static std::list<Berth*> Berths;
    // 泊位负责区域的信息
    static int BerthRegion[MAP_SIZE + 10][MAP_SIZE + 10];
    // 机器人列表
    static std::list<Robot*> Robots;
    // 船列表
    static std::list<Boat*> Boats;


    static void Init();


private:
    static void GetMap();

    static void InitData();

    static void Preprocess();

    static void FloodFill();
};


#endif //PLK_2024_PORTMAP_H
