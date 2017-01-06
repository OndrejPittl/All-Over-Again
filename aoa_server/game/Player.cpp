#include <string>


#include "Player.h"



Player::Player() {}

Player::Player(int id) {
    this->id = id;
}

Player::Player(int id, std::string username) {
    this->id = id;
    this->username = username;
}

void Player::init() {
    this->roomID = -1;
}

void Player::setID(int id) {
    this->id = id;
}

int Player::getID() const {
    return this->id;
}

void Player::setRoomID(int id) {
    this->roomID = id;
}



int Player::getRoomID() const {
    return this->id;
}

void Player::setUsername(std::string username) {
    this->username = username;
}

std::string Player::getUsername() const {
    return this->username;
}

void Player::setIsConnected(bool connected) {
    this->isConnected = connected;
}

bool Player::getIsConnected() const {
    return this->isConnected;
}

bool Player::hasRoom() {
    return this->roomID > -1;
}

