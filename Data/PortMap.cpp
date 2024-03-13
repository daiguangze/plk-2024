//
// Created by k on 2024/3/13.
//

#include <iostream>
#include "PortMap.h"

void PortMap::GetMap() {
    std::string mapLine;

    // 地区信息
    for (int i = 0; i < MAP_SIZE; ++i){
        std::getline(std::cin, mapLine);
        for(std::size_t j = 0; j < mapLine.length(); ++j){
            MapInfo[i][j] = mapLine[j];
        }
    }

    // 读取泊位信息
    for (int i = 0; i < BERTH_NUM; ++i){
        BerthInfo.push_back(new Berth());
    }

    // 读取船容量

}
