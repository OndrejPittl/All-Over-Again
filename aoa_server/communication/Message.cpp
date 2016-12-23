#include <string>

// libraries

// headers
#include "Message.h"
#include "MessageProcessor.h"


Message::Message() {}

Message::Message(int sock, std::string msg) {
	this->sock = sock;
	this->message = msg;
}

Message::Message(int sock, long byteSize, std::string msg) {
    this->sock = sock;
    this->byteSize = byteSize;
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

void Message::setSize(long bytes) {
	this->byteSize = bytes;
}

long Message::getSize() {
    return this->byteSize;
}

