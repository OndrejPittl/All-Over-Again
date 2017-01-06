// libraries
#include <iostream>
#include <regex>

#include "tools.h"



// headers


bool validate(std::string str, std::string regexp) {
    std::regex regex(regexp);
    return regex_match(str, regex);
}

/**
*	Checks whether a string given is a number or not.
*/
bool isNumber(std::string str){
	return validate(str, "^([0-9]*)$");
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

void printMap(std::map<int, Player> m) {
    std::cout << "Printing a map:" << std::endl;

    if(m.size() <= 0)
        std::cout << "Map is empty." << std::endl;

    for(auto it = m.cbegin(); it != m.cend(); ++it) {
        std::cout << "key: " << it->first << " - " << it->second.getUsername() << std::endl;
    }
}
