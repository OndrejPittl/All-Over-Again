#include <string>
#include <sstream>

#include "StringBuilder.h"


StringBuilder::StringBuilder() {}

void StringBuilder::clear() {
	this->message.str("");
	this->message.clear();
}

std::string StringBuilder::getString() {
	return this->message.str();
}