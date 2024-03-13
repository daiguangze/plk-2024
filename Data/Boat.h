//
// Created by 15941 on 2024/3/13.
//

#ifndef PLK_2024_BOAT_H
#define PLK_2024_BOAT_H


class Boat {
public:
    int Id;
    // 目标泊位id，虚拟位为-1
    int Position;
    // 状态（0：移动中；1：正常运行；2：泊位外等待）
    int Status;
};


#endif //PLK_2024_BOAT_H
