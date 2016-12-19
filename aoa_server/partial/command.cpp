#include <string>

#include "command.h"


Command::Command(int id) {
	this->id = id;
	this->params = "";
}

Command::Command(int id, std::string params) {
	this->id = id;
	this->params = params;
}

int Command::getID(){
	return this->id;
}

void Command::setID(int id) {
	this->id = id;
}

std::string Command::getParams() {
	return this->params;
}

void Command::setParams(std::string params) {
	this->params = params;
}