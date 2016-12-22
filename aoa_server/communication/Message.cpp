#include <string>

// libraries

// headers
#include "Message.h"
#include "../partial/SafeQueue.h"
#include "Receiver.h"


Message::Message() {}

Message::Message(int sock, std::string msg) {
	this->sock = sock;
	this->message = msg;
}

int Message::getSock(){
	return this->sock;
}

void Message::setSock(int sock) {
	this->sock = sock;
}

std::string Message::getMsg() {
	return this->message;
}

void Message::setMsg(std::string msg) {
	this->message = msg;
}

void Message::setSize(int bytes) {
	this->byteSize = bytes;
}

int Message::getSize() {
    return this->byteSize;
}


