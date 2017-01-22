#include <iostream>
#include <regex>
#include <iomanip>

#include "tools.h"
#include "../core/Logger.h"
#include "../game/Room.h"
#include "../game/GameStatus.h"

StringBuilder *Tools::sb;

const std::string Tools::VALIDATION_USERNAME_REGEX = "^[a-zA-Z0-9-_<>]{3,15}$";

//const std::string Tools::LOG_PADDING_LEFT = "                      ";



void Tools::init() {
    Tools::sb =  new StringBuilder();
}

bool Tools::validate(std::string str, std::string regexp) {
    std::regex regex(regexp);
    return regex_match(str, regex);
}

bool Tools::validateUsername(std::string username) {
    return validate(username, Tools::VALIDATION_USERNAME_REGEX);
}

/**
*	Checks whether a string given is a number or not.
*/
bool Tools::isNumber(std::string str) {
    return str.length() > 0 && Tools::validate(str, "^([0-9]*)$");
}

/**
*	Checks whether a string given is a number and in range or not.
*/
bool Tools::isNumberInRange(std::string str, int lowerLimit, int upperLimit) {
    int number;

    if(!Tools::isNumber(str))
        return false;

    number = atoi(str.c_str());
    return number >= lowerLimit && number <= upperLimit;
}

/**
*	Prints a line of a text.
*/
void Tools::println(std::string str){
    printf("%s\n", str.c_str());
}

void Tools::printTrueFalse(){
    std::cout << "true: " << true << std::endl;		//1
    std::cout << "false: " << false << std::endl;	//0
}

void Tools::removeChar(std::string *str, char c) {
    (*str).erase (std::remove((*str).begin(), (*str).end(), c), (*str).end());
}

long Tools::checksum(std::string str, int mod) {
    long sum = 0;

    for(std::string::size_type i = 0; i < str.size(); ++i) {
        sum += (long) str[i];
    }

    if(mod > 0)
        sum = sum % mod;

    return sum;
}

void Tools::printVector(std::vector<std::string> vec) {
    Logger::debug("Printing vector:");

    if(vec.size() <= 0)
        Logger::debug("Vector is empty.");

    Tools::sb->clear();

    int i = 0;
    for (auto const& v : vec){
        std::string str = std::string(v); removeChar(&str, '\n');

        Tools::sb->append(i++); Tools::sb->append(". polozka: ");
        Tools::sb->append(str); Logger::debug(Tools::sb->getString());
    }
}

void Tools::printPlayerVector(PlayerVector vec) {
    Logger::debug("Printing vector of players:");

    if(vec.size() <= 0)
        Logger::debug("No player in a vector.");

    Tools::sb->clear();

    int i = 0;
    for (auto const& v : vec){

        Tools::sb->append(i++); Tools::sb->append(". ["); Tools::sb->append(v->getID());
        Tools::sb->append(", "); Tools::sb->append((v->hasRoom() ? v->getRoomID() : -1)); Tools::sb->append("] ");
        Tools::sb->append(v->getUsername()); Tools::sb->append((v->isOnline() ? " (online)" : " (offline)"));
        Logger::debug(Tools::sb->getString());
    }

}


void Tools::printPlayers(PlayerMap &m) {
    Tools::buildPlayers(m);
    Logger::debug(Tools::sb->getString());
}

void Tools::buildPlayers(PlayerMap &m) {
    int nullPtrCount = 0, nullPtrCountLimit = 5, colWidth = 10, colCount = 5,
            colTotal = colCount * (colWidth + 3);

    if(m.size() <= 0) {
        Tools::sb->append(Tools::buildColumn(std::string("(empty)"), colTotal, ' '));
        return;
    }

    // header
    Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::string("index"), colWidth)); Tools::sb->append(" ");
    Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::string("user id"), colWidth)); Tools::sb->append(" ");
    Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::string("username"), colWidth)); Tools::sb->append(" ");
    Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::string("room id"), colWidth)); Tools::sb->append(" ");
    Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::string("status"), colWidth)); Tools::sb->append(" |\n");

    for(auto it = m.cbegin(); it != m.cend(); ++it) {
        Player *p = it->second;

        if(p == nullptr) {
            if(++nullPtrCount > nullPtrCountLimit)
                break;
            continue;
        };

        Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::to_string(it->first) + ".", colWidth)); Tools::sb->append(" ");
        Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::to_string(p->getID()), colWidth)); Tools::sb->append(" ");
        Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(p->hasUsername() ? p->getUsername() : std::string("-"), colWidth)); Tools::sb->append(" ");
        Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::to_string(p->getRoomID()), colWidth)); Tools::sb->append(" ");
        Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(p->isOnline() ? std::string("online") : std::string("offline"), colWidth)); Tools::sb->append(" |\n");
    }
}

void Tools::buildRooms(RoomMap &m) {
    int nullPtrCount = 0, nullPtrCountLimit = 5, colWidth = 10, colCount = 8,
            colTotal = colCount * (colWidth + 3);

    Tools::sb->append("\n" + Tools::buildColumn(std::string(" ROOMS "), colTotal, '=') + "\n");

    if(m.size() <= 0) {
        Tools::sb->append(Tools::buildColumn(std::string("(no room)"), colTotal, ' ') + "\n");
        Tools::sb->append(std::string(colTotal, '='));
        return;
    }

    // header
    Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::string("index"), colWidth)); Tools::sb->append(" ");
    Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::string("room id"), colWidth)); Tools::sb->append(" ");
    Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::string("difficulty"), colWidth)); Tools::sb->append(" ");
    Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::string("dimension"), colWidth)); Tools::sb->append(" ");
    Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::string("pl-limit"), colWidth)); Tools::sb->append(" ");
    Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::string("pl-total"), colWidth)); Tools::sb->append(" ");
    Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::string("pl-online"), colWidth)); Tools::sb->append(" ");
    Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::string("status"), colWidth)); Tools::sb->append(" |\n");


    for(auto it = m.cbegin(); it != m.cend(); ++it) {
        Room *r = it->second;

        if(r == nullptr) {
            if(++nullPtrCount > nullPtrCountLimit)
                break;
            continue;
        };

        Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::to_string(it->first), colWidth)); Tools::sb->append(" ");
        Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::to_string(r->getID()), colWidth)); Tools::sb->append(" ");
        Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(r->hasDifficulty() ? std::to_string((int) r->getDifficulty()) : std::string("-"), colWidth)); Tools::sb->append(" ");
        Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(r->hasDimension() ? std::to_string((int) r->getBoardDimension()) : std::string("-"), colWidth)); Tools::sb->append(" ");
        Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(r->hasType() ? std::to_string((int) r->getGameType()) : std::string("-"), colWidth)); Tools::sb->append(" ");
        Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::to_string(r->getPlayerCount()), colWidth)); Tools::sb->append(" ");
        Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::to_string(r->countOnlinePlayers()), colWidth)); Tools::sb->append(" ");
        Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(translateGameStatus(r->getStatus()), colWidth)); Tools::sb->append(" |\n");
    }

    Tools::sb->append(std::string(colTotal, '='));
}

void Tools::printRooms(RoomMap &m) {
    Tools::sb->clear();
    Tools::buildRooms(m);
    Logger::debug(Tools::sb->getString());
}

bool Tools::checkIfExistsInPlayerVector(PlayerVector &vec, int uid) {
    if(vec.size() <= 0)
        return false;

    int i = 0;
    for (auto const& p : vec){
        if(p->getID() == uid)
            return true;
    }

    return false;
}

void Tools::buildUsernames(std::map<std::string, int> usernames) {
    int nullPtrCount = 0, nullPtrCountLimit = 5, colWidth = 15, colCount = 3,
            colTotal = colCount * (colWidth + 3);

    Tools::sb->clear();
    Tools::sb->append("\n" + Tools::buildColumn(std::string(" USERNAMES "), colTotal, '-') + "\n");

    if(usernames.size() <= 0) {
        Tools::sb->append(Tools::buildColumn(std::string("(no username)"), colTotal, ' ') + "\n");
        Tools::sb->append(std::string(colTotal, '-'));
        return;
    }

    // header
    Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::string("index"), colWidth)); Tools::sb->append(" ");
    Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::string("username"), colWidth)); Tools::sb->append(" ");
    Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::string("uid(on)/index(off)"), colWidth)); Tools::sb->append(" |\n");


    int i = 0;

    for(auto it = usernames.cbegin(); it != usernames.cend(); ++it) {
        std::string u = it->first;

        if(u.length() <= 0) {
            if(++nullPtrCount > nullPtrCountLimit)
                break;
            continue;
        };

        Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::to_string(i), colWidth)); Tools::sb->append(" ");
        Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(u, colWidth)); Tools::sb->append(" ");
        Tools::sb->append("| "); Tools::sb->append(Tools::buildColumn(std::to_string(it->second), colWidth)); Tools::sb->append(" |\n");

        i++;
    }

    Tools::sb->append(std::string(colTotal, '-'));
}

void Tools::printUsernames(std::map <std::string, int> usernames){
    Tools::buildUsernames(usernames);
    Logger::debug(Tools::sb->getString());
}


bool Tools::keyExistsInPlayerMap (PlayerMap &players, int uid) {
    return players.find(uid) != players.end();
}


void Tools::printOnlineOfflineUsers(PlayerMap online, PlayerMap offline) {
    int colWidth = 5 * (10 + 3);

    Tools::sb->clear();
    Tools::sb->append("\n" + Tools::buildColumn(std::string("ONLINE"), colWidth, '=') + "\n");
    Tools::buildPlayers(online);
    Tools::sb->append("\n" + std::string(colWidth, '=') + "\n");

    Tools::sb->append("\n" + Tools::buildColumn(std::string("OFFLINE"), colWidth, '=') + "\n");
    Tools::buildPlayers(offline);
    Tools::sb->append("\n" + std::string(colWidth, '=') + "\n");
    Logger::debug(Tools::sb->getString());
}






