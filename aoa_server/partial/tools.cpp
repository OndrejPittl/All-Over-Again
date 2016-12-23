// libraries
#include <iostream>
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
	printf("%s\n", str.c_str());
}

void printTrueFalse(){
	std::cout << "true: " << true << std::endl;		//1
	std::cout << "false: " << false << std::endl;	//0
}

void removeChar(std::string *str, char c) {
	(*str).erase (std::remove((*str).begin(), (*str).end(), c), (*str).end());
}

long checksum(std::string str) {
    long sum = 0;

    for(std::string::size_type i = 0; i < str.size(); ++i) {
        sum += (long) str[i];
    }

    std::cout << "checksum: " << sum << std::endl;
    return sum;
}

void printVector(std::vector<std::string> vec) {

    std::cout << "Printing vector:" << std::endl;


    if(vec.size() <= 0)
        std::cout << "Vector is empty." << std::endl;

    int i = 0;
    for (auto const& v : vec){
        std::string str = std::string(v);
        removeChar(&str, '\n');
        std::cout << i++ << ". polozka: " << str << std::endl;
    }

}