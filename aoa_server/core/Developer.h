#ifndef DEVELOPER_H
#define DEVELOPER_H

#include <iostream>
#include <string>
#include <map>

#include "../partial/tools.h"
#include "../game/Player.h"
#include "Logger.h"

class Developer {
    public:
        static void printOnlineOfflineUsers(PlayerMap online, PlayerMap offline) {
            std::string str = "";
            str.append("========= ONLINE =========\n");
            printPlayers(online);
            str.append("========= OFFLINE ========\n");
            printPlayers(offline);
            str.append("===========================");
            Logger::info(str);
        }

};


#endif