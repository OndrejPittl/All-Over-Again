#include <string>
#include <sstream>

#include "StringBuilder.h"


StringBuilder::StringBuilder() {}


// template <typename type>
// void StringBuilder::append(type arg) {
// 	message << arg;
// }

void StringBuilder::clear() {
	this->message.str("");
	this->message.clear();
}

std::string StringBuilder::getString() {
	return this->message.str();
}