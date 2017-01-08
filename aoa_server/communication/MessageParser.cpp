#include <sstream>
#include <iostream>
#include <vector>
#include <string>
#include <queue>
#include <map>

#include "MessageParser.h"
#include "../partial/StringBuilder.h"
#include "../game/Room.h"
#include "Message.h"
#include "../core/Logger.h"


MessageParser::MessageParser() {
    this->init();
}

void MessageParser::init() {
    this->sb = new StringBuilder();
    std::cout << "MSGParser initialized." << std::endl;
}



/**
 *input: p-limit /g-type ; diff ; dim
 *             2 ; 0 ; 3
 * @param msg
 * @return
 */
Room MessageParser::parseNewRoomRequest(Message *msg) {
    std::string part, typeStr, diffStr, dimStr;
    std::queue<std::string> parts;

    GameType type;
    GameDifficulty diff;
    BoardDimension  dim;

    parts = this->split(msg->getMessage());
    typeStr = parts.front(); parts.pop();
    diffStr = parts.front(); parts.pop();
    dimStr = parts.front(); parts.pop();

    type = convertInternalGameType(std::stoi(typeStr));
    diff = convertInternalGameDifficulty(std::stoi(diffStr));
    dim = convertInternalBoardDimension(std::stoi(dimStr));

    return *(new Room(type, diff, dim));
}

std::queue<std::string> MessageParser::split(std::string message) {
    std::queue<std::string> parts;
    istringstream f(message);
    std::string str;

    while (std::getline(f, str, Message::DELIMITER)) {
        parts.push(str);
    }

    return parts;
}

int MessageParser::parseJoinRoomRequest(string msg) {
    return std::stoi(msg);
}

/**
 * 0;0;0; 4;1;1; 8;3;4
 * @param progess
 * @return
 */
std::queue<int>& MessageParser::parseTurn(string progress) {
    std::queue<int> out;
    std::queue<std::string> parts;

    parts = this->split(progress);

    while(!parts.empty()) {
        int i = std::stoi(parts.front());
        out.push(i);
        parts.pop();
    }

    return out;
}


