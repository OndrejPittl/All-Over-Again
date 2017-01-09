#include <string>


#include "Player.h"
#include "../core/Logger.h"


Player::Player() {}

Player::Player(int id) {
    this->id = id;
    this->init();
}

Player::Player(int id, std::string username) {
    this->id = id;
    this->username = username;
    this->init();
}

void Player::init() {
    this->setRoomID(-1);
}

void Player::setID(int id) {
    this->id = id;
}

int Player::getID() const {
    return this->id;
}

void Player::setRoomID(int id) {
    this->roomID = id;
    Logger::info("PLAYER ROOM WAS SET:");
    Logger::info(std::to_string(this->roomID));
}



int Player::getRoomID() const {
    return this->roomID;
}

void Player::setUsername(std::string username) {
    this->username = username;
}

std::string Player::getUsername() const {
    return this->username;
}


bool Player::hasRoom() {
    return this->roomID != -1;
}

void Player::setStatus(bool online) {
    this->online = online;
}

void Player::setOnline() {
    this->setStatus(true);
}

void Player::setOffline() {
    this->setStatus(false);
}

bool Player::isOnline() const {
    return this->online;
}


