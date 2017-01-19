#include <iostream>
#include <regex>

#include "tools.h"
#include "../core/Logger.h"


bool validate(std::string str, std::string regexp) {
    std::regex regex(regexp);
    return regex_match(str, regex);
}

bool validateUsername(std::string username) {
    return validate(username, VALIDATION_USERNAME_REGEX);
}

/**
*	Checks whether a string given is a number or not.
*/
bool isNumber(std::string str){
	return str.length() > 0 && validate(str, "^([0-9]*)$");
}

/**
*	Checks whether a string given is a number and in range or not.
*/
bool isNumberInRange(std::string str, int lowerLimit, int upperLimit) {
	int number;

	if(!isNumber(str))
		return false;

	number = atoi(str.c_str());
	return number >= lowerLimit && number <= upperLimit;
}

/**
*	Prints a line of a text.
*/
void println(std::string str){
	printf("%s\n", str.c_str());
}

void printTrueFalse(){
	std::cout << "true: " << true << std::endl;		//1
	std::cout << "false: " << false << std::endl;	//0
}

void removeChar(std::string *str, char c) {
	(*str).erase (std::remove((*str).begin(), (*str).end(), c), (*str).end());
}

long checksum(std::string str, int mod) {
    long sum = 0;

    for(std::string::size_type i = 0; i < str.size(); ++i) {
        sum += (long) str[i];
    }

    if(mod > 0)
        sum = sum % mod;

    return sum;
}

void printVector(std::vector<std::string> vec) {
    Logger::debug("Printing vector:");

    if(vec.size() <= 0)
        Logger::debug("Vector is empty.");

    int i = 0;
    for (auto const& v : vec){
        std::string str = std::string(v); removeChar(&str, '\n');
        //std::cout << i++ << ". polozka: " << str << std::endl;

        std::string s = ""; s.append(std::to_string(i++));
        s.append(". polozka: "); s.append(str); Logger::debug(s);
    }
}

void printPlayerVector(PlayerVector vec) {
    Logger::debug("Printing vector of players:");

    if(vec.size() <= 0)
        Logger::debug("No player in a vector.");

    int i = 0;
    for (auto const& v : vec){

        std::string str = ""; str.append(std::to_string(i++)); str.append(". ["); str.append(std::to_string(v->getID()));
        str.append(", "); str.append(std::to_string((v->hasRoom() ? v->getRoomID() : -1))); str.append("] ");
        str.append(v->getUsername()); str.append((v->isOnline() ? " (online)" : " (offline)")); Logger::debug(str);
    }

}

void printPlayers(PlayerMap m) {
    Logger::info("Printing a PLAYER MAP:");

    if(m.size() <= 0)
        Logger::info("Map is empty.");

    int i = 0;

    for(auto it = m.cbegin(); it != m.cend(); ++it) {

        std::string str = ""; str.append(std::to_string(i++)); str.append(". ["); str.append(std::to_string(it->first));
        str.append(", "); str.append(std::to_string((it->second->hasRoom() ? it->second->getRoomID() : -1))); str.append("] ");
        str.append(it->second->getUsername()); str.append((it->second->isOnline() ? " (online)" : " (offline)")); Logger::debug(str);
    }
}

void printRooms(RoomMap m) {
    Logger::info("============== Printing rooms ==============");

    if(m.size() <= 0)
        Logger::info("No room.");

    for(auto it = m.cbegin(); it != m.cend(); ++it) {
        Room *r = it->second;


        std::string str = "key: "; str.append(std::to_string(it->first)); str.append(" - ID: "); str.append(std::to_string(r->getID()));
        str.append(", diff: "); str.append(std::to_string((int) r->getDifficulty())); str.append(", dim: ");
        str.append(std::to_string((int) r->getBoardDimension())); str.append(", type: ");
        str.append(std::to_string((int) r->getGameType())); str.append(", players: ");
        str.append(std::to_string(r->getPlayerCount())); str.append("(online: ");
        str.append(std::to_string(r->countOnlinePlayers())); str.append(")");
        Logger::debug(str);
    }

    Logger::info("===========================================");
}

bool checkIfExistsInPlayerVector(PlayerVector &vec, int uid) {
    if(vec.size() <= 0)
        return false;

    int i = 0;
    for (auto const& p : vec){
        if(p->getID() == uid)
            return true;
    }

    return false;
}

void printUsernames(std::map <std::string, int> usernames){
    int i = 0;

    Logger::info("--- printing usernames ---");

    for(auto it = usernames.cbegin(); it != usernames.cend(); ++it) {
        std::string str = ""; str.append(std::to_string((i++)));
        str.append(". "); str.append(it->first);
        str.append(" ("); str.append(std::to_string(it->second));
        str.append(")"); Logger::info(str);
    }

    Logger::info("--------------------------");
}


bool keyExistsInPlayerMap (PlayerMap &players, int uid) {
    return players.find(uid) != players.end();
}