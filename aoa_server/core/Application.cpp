#include <string>
#include <iostream>

#include "Application.h"
#include "../partial/tools.h"
#include "../partial/StringBuilder.h"
#include "../game/Player.h"
#include "Logger.h"
#include "Developer.h"
#include "../connection/ConnectionManager.h"
#include "../communication/CommunicationManager.h"


const std::string Application::USERNAME_VALIDATION_REGEX = "^[a-zA-Z0-9-_<>]{3,15}$";

Application::Application() {
    this->init();
}

Application::Application(ConnectionManager *conn, CommunicationManager *comm) {
    this->conn = conn;
    this->comm = comm;
    this->init();
}

void Application::init() {
    this->log = new StringBuilder();
}

bool Application::validateUsername(std::string username) {
    return validate(username, Application::USERNAME_VALIDATION_REGEX);
}

bool Application::checkUsernameAvailability(std::string username) {
    //iterator
    typedef std::map<std::string, int>::iterator it_type;

    // iteration through a map of usernames
    for(it_type iterator = this->usernames.begin(); iterator != this->usernames.end(); iterator++) {
        std::string uName = iterator->first;
        int id = iterator->second;

        // found -> not available
        if(!uName.compare(username))
            return false;
    }

    // available
    return true;
}

bool Application::registerUser(int uid, std::string username) {

    // charset check
    if(!this->validateUsername(username))
        return false;


    // is already assigned in a room?
    // ...


    // availability check
    if(!this->checkUsernameAvailability(username))
        return false;

    Player *p = new Player(uid, username);
    this->usernames[username] = uid;
    this->onlineUsers[uid] = *p;
    return true;
}

void Application::deregisterUser(int uid) {
    Player *p = &(this->onlineUsers[uid]);

    Developer::printOnlineOfflineUser(this->onlineUsers, this->offlineUsers);
    this->offlineUsers[uid] = *p;
    this->onlineUsers.erase(uid);
    Developer::printOnlineOfflineUser(this->onlineUsers, this->offlineUsers);

    this->log->clear();
    this->log->append("A user ");
    this->log->append(p->getUsername());
    this->log->append(" was marked as offline.");
    Logger::info(this->log->getString());
}

