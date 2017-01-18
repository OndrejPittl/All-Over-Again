#ifndef DEVELOPER_H
#define DEVELOPER_H

#include <iostream>
#include <string>
#include <map>

#include "../partial/tools.h"
#include "../game/Player.h"

class Developer {

    public:
//        static void printOnlineOfflineUser(std::map<int, Player> online, std::map<int, Player> offline){
//            std::cout << "PRINTING ONLINE: " << std::endl;
//            printPlayers(online);
//            std::cout << "PRINTING OFFLINE: " << std::endl;
//            printPlayers(offline);
//        }

        static void printOnlineOfflineUsers(PlayerMap online, PlayerMap offline){

            std::cout << "============================" << std::endl;
            std::cout << "PRINTING ONLINE: " << std::endl;
            printPlayers(online);
            std::cout << "PRINTING OFFLINE: " << std::endl;
            printPlayers(offline);
            std::cout << "============================" << std::endl;
        }

};


#endif