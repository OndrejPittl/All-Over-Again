#include <string>


#include "Player.h"
#include "../core/Logger.h"


Player::Player() {}

Player::Player(int id) {
    this->ID = id;
    this->init();
}

Player::Player(int id, std::string username) {
    this->ID = id;
    this->username = username;
    this->init();
}

void Player::init() {
    this->setRoomID(-1);
    this->setOnline();
    this->clearIncorrectMsgCount();
    this->setUsername("");
}

void Player::setID(int id) {
    this->ID = id;
}

int Player::getID() const {
    return this->ID;
}
void Player::setRoomID(int id) {
    this->roomID = id;
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

bool Player::hasUsername() const {
    return this->username.length() > 0;
}

bool Player::hasRoom() {
    return this->roomID != -1;
}

void Player::leaveRoom() {
    this->setRoomID(-1);
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
    bool on = this->online;
     return on;
}

int Player::getIncorrectMsgCount() const {
    return incorrectMsgCount;
}

void Player::registerIncorrectMsgCount() {
    this->incorrectMsgCount++;
}

void Player::clearIncorrectMsgCount() {
    this->incorrectMsgCount = 0;
}

void Player::merge(Player *p) {
    this->setRoomID(p->getRoomID());
    this->setOnline();
    this->clearIncorrectMsgCount();
    this->setUsername(p->getUsername());
}

