//
// Created by k on 2024/3/13.
//

#ifndef PLK_2024_PORTMAP_H
#define PLK_2024_PORTMAP_H


#include <string>
#include <list>
#include "Berth.h"

class PortMap{
public:
    // 地图的大小
    static const int MAP_SIZE = 200;
    // 泊位个数
    static const int BERTH_NUM = 10;

    // 港口地图信息
    static char MapInfo[MAP_SIZE + 10][MAP_SIZE + 10];
    // 泊位信息
    static std::list<Berth*> BerthInfo;

    static void GetMap();

private:

};


#endif //PLK_2024_PORTMAP_H
