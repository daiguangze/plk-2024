//
// Created by 15941 on 2024/3/13.
//

#ifndef PLK_2024_ROBOT_H
#define PLK_2024_ROBOT_H


#include <string>
#include <queue>

class Robot {
public:
    int X;
    int Y;
    int Goods;
    int Status;

    int mbX;
    int mbY;

    std::queue<std::string> Instruction;
};


#endif //PLK_2024_ROBOT_H
