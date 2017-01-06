
#include <cstddef>
#include <string>

#include "RawMessage.h"

RawMessage::RawMessage(int sock, size_t size, std::string msg) {
    this->sock = sock;
    this->size = size;
    this->message = msg;
}

int RawMessage::getSock() {
    return this->sock;
}

size_t RawMessage::getSize() {
    return this->size;
}

std::string RawMessage::getMessage() {
    return this->message;
}


