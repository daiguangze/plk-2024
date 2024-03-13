cmake_minimum_required(VERSION 3.25)
project(plk_2024)

set(CMAKE_CXX_STANDARD 17)

add_executable(plk_2024 main.cpp Data/PortMap.cpp Data/PortMap.h Data/Berth.cpp Data/Berth.h
        Data/Robot.cpp
        Data/Robot.h
        Data/Boat.cpp
        Data/Boat.h)
