// libraries
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <string>
#include <regex>

// headers
#include "tools.h"



/**
*	Checks whether a string given is a number or not.
*/
bool isNumber(std::string str){
	std::regex regexInteger("^(0|[1-9][0-9]*)$");
	return regex_match(str, regexInteger);
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
//	printf("---%s---\n", str.c_str());
	printf("%s\n", str.c_str());
}

void printTrueFalse(){
	std::cout << "true: " << true << std::endl;		//1
	std::cout << "false: " << false << std::endl;	//0
}