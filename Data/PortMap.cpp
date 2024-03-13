//
// Created by k on 2024/3/13.
//

#include <iostream>
#include "PortMap.h"

int PortMap::BoatCapacity;
char PortMap::MapInfo[PortMap::MAP_SIZE + 10][PortMap::MAP_SIZE + 10] = {};
int PortMap::BerthRegion[PortMap::MAP_SIZE + 10][PortMap::MAP_SIZE + 10] = {};
std::list<Robot*> PortMap::Robots = {};
std::list<Boat*> PortMap::Boats = {};
std::list<Berth*> PortMap::Berths = {};

void PortMap::Init() {
    GetMap();
    InitData();
    Preprocess();

    // 初始化结束，交给判定器
    std::string input;
    std::cin >> input;
    if (std::equal(input.begin(), input.end(),"OK")){
        std::cout << "OK" << "\n";
    }
    else
    {
        std::cerr << "[Init] - No Receive OK but read finished" << std::endl;
    }

}

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
        Berths.push_back(new Berth());
    }
    // 读取船容量
    std::cin >> BoatCapacity;
}

// 初始化各类数据
void PortMap::InitData(){
    // 初始化机器人
    for (int i = 0; i < ROBOT_NUM; ++i){
        Robots.push_back(new Robot);
    }
    // 初始化船
    for (int i = 0; i < BOAT_NUM; ++i){
        Boats.push_back(new Boat);
    }
}

// 数据预处理
void PortMap::Preprocess(){
    // 洪水填充
    FloodFill();
}

void PortMap::FloodFill() {

}
