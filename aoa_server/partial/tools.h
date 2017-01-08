#ifndef TOOLS_H
#define TOOLS_H

#include <string>
#include <vector>
#include <map>
#include "../game/Player.h"
#include "../game/Room.h"


enum CommandType {
	CMD_EXAMPLE = 0,
	CMD_EXAMPLE2
};


bool validate(std::string str, std::string regexp);

/**
*	Checks whether a string given is a number or not.
*/
bool isNumber(std::string str);

/**
*	Checks whether a string given is a number and in range or not.
*/
bool isNumberInRange(std::string str, int lowerLimit, int upperLimit);

/**
*	Prints a line of a text.
*/
void println(std::string str);

/**
*
*/
void printTrueFalse();

void removeChar(std::string *str, char c);

long checksum(std::string str, int modulo = -1);

void printVector(std::vector<std::string> vec);

void printMap(std::map<int, Player> m);

void printRooms(std::map<int, Room> m);


#endif