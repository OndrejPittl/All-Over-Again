#scmake_minimum_required(VERSION 3.6)
project(aoa_server)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}")  # -v -Wall -Wextra



file(GLOB SRC_FILES_TOP
        ${PROJECT_SOURCE_DIR}/*.cpp
        ${PROJECT_SOURCE_DIR}/*.h
)

file(GLOB_RECURSE SRC_FILES_NESTED
        ${PROJECT_SOURCE_DIR}/communication/*.h
        ${PROJECT_SOURCE_DIR}/communication/*.cpp
        ${PROJECT_SOURCE_DIR}/connection/*.h
        ${PROJECT_SOURCE_DIR}/connection/*.cpp
        ${PROJECT_SOURCE_DIR}/core/*.h
        ${PROJECT_SOURCE_DIR}/core/*.cpp
        ${PROJECT_SOURCE_DIR}/partial/*.h
        ${PROJECT_SOURCE_DIR}/partial/*.cpp
        ${PROJECT_SOURCE_DIR}/game/*.h
        ${PROJECT_SOURCE_DIR}/game/*.cpp
)

set(SRC_FILES ${SRC_FILES_TOP} ${SRC_FILES_NESTED})

add_executable(aoa_server ${SRC_FILES})
