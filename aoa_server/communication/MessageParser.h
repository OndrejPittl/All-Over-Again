#ifndef MESSAGE_PARSER_H
#define MESSAGE_PARSER_H



#include <string>
#include <vector>
#include <queue>
#include <map>

#include "../partial/StringBuilder.h"
#include "../game/Room.h"
#include "Message.h"

class MessageParser {
    private:
        StringBuilder *sb;

    public:
        MessageParser();
        void init();

        void parseNewRoomRequest(Message *msg, Room *room);

        std::queue<std::string> split(std::string message);

        int parseJoinRoomRequest(string msg);

        void parseTurn(string progress, std::queue<int>& queue);
};

#endif

