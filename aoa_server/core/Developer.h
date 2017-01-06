#ifndef DEVELOPER_H
#define DEVELOPER_H

#include <iostream>
#include <string>
#include <map>

#include "../partial/tools.h"
#include "../game/Player.h"

class Developer {

    public:
        static void printOnlineOfflineUser(std::map<int, Player> online, std::map<int, Player> offline){
            std::cout << "PRINTING ONLINE: " << std::endl;
            printMap(online);
            std::cout << "PRINTING OFFLINE: " << std::endl;
            printMap(offline);
        }

};


#endif