#ifndef TOOLS_H
#define TOOLS_H

#include <string>


enum CommandType {
	CMD_EXAMPLE = 0,
	CMD_EXAMPLE2
};


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


#endif