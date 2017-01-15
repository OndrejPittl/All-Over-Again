#include <iostream>
#include <map>

#include "MessageSerializer.h"
#include "CommunicationManager.h"
#include "../partial/tools.h"
#include "../core/Logger.h"


MessageSerializer::MessageSerializer() {
    this->init();
}

void MessageSerializer::init() {
    this->sb = new StringBuilder();
    this->log = new StringBuilder();

    Logger::info("MSGSerializer initialized.", false);
}

std::string MessageSerializer::serializeRooms(RoomMap &rooms) {
    unsigned long roomCount;

    Logger::info("serializing rooms...");

    printRooms(rooms);

    this->sb->clear();
    roomCount = rooms.size();

    for(auto it = rooms.cbegin(); it != rooms.cend(); ++it) {
        Room *r = it->second;
        this->serializeRoomAndJoin(r);
        if(--roomCount > 0) this->sb->append(Message::DELIMITER);
    }

    return this->sb->getString();
}

void MessageSerializer::serializeRoomAndJoin(Room *r) {
    unsigned long playerCount;
    this->sb->append(r->getID());
    this->sb->append(Message::DELIMITER);
    this->sb->append(r->getPlayerCount());
    this->sb->append(Message::DELIMITER);
    this->sb->append((int) r->getGameType());
    this->sb->append(Message::DELIMITER);
    this->sb->append((int) r->getDifficulty());
    this->sb->append(Message::DELIMITER);
    this->sb->append((int) r->getBoardDimension());
    this->sb->append(Message::DELIMITER);

    PlayerMap &players = r->getPlayers();
    playerCount = players.size();

    for(auto it = players.cbegin(); it != players.cend(); ++it) {
        this->sb->append(it->second->getUsername());
        if(--playerCount > 0) this->sb->append(Message::SUBDELIMITER);
    }
}

std::string MessageSerializer::serializeRoom(Room *r) {
    this->sb->clear();
    this->serializeRoomAndJoin(r);
    return this->sb->getString();
}

