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
        Room parseNewRoomRequest(Message *msg);

        std::queue<std::string> split(std::string message);

        int parseJoinRoomRequest(string msg);

        std::queue<int>& parseTurn(string progress);
};

#endif

