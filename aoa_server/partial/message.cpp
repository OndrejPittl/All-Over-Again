#include <string>

// libraries
#include <string>

// headers
#include "message.h"



Message::Message(int sock, std::string msg) {
	this->sock = sock;
	this->msg = msg;
}

int Message::getSock(){
	return this->sock;
}

void Message::setSock(int sock) {
	this->sock = sock;
}

std::string Message::getMsg() {
	return this->msg;
}

void Message::setMsg(std::string msg) {
	this->msg = msg;
}

