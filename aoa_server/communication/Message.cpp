#include <string>

// libraries

// headers
#include "Message.h"
#include "MessageValidator.h"


const char Message::STX = '*';  //'\x02'
const char Message::ETX = '#';  //'\x03'
const char Message::DELIMITER = ';';
const char Message::SUBDELIMITER = ':';
const int Message::MSG_CHECKSUM_MODULO = 235;
const std::string Message::ACK = std::string("1");
const std::string Message::NACK = std::string("0");
const std::string Message::HELLO_PACKET = "Hey AOA! How are you?";
const std::string Message::HELLO_PACKET_RESPONSE = "Hey Client! I am fine.";


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

std::string Message::getMessage() {
	return this->message;
}

void Message::setMessage(std::string msg) {
	this->message = msg;
}

void Message::setSize(long bytes) {
	this->byteSize = bytes;
}

long Message::getSize() {
    return this->byteSize;
}

MessageType Message::getType() {
    return this->type;
}

void Message::setType(MessageType type) {
    this->type = type;
}



