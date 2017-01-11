
#include <string>
#include <thread>
#include <iostream>

#include "MessageValidator.h"
#include "../partial/tools.h"
#include "../partial/StringBuilder.h"
#include "../core/Logger.h"

//const std::string MessageValidator::STX = "\u0002";


MessageValidator::MessageValidator(SafeQueue<Message *> *queue, SafeQueue<RawMessage *> *rawMessageQueue) {
    this->messageQueue = queue;
    this->rawMessageQueue = rawMessageQueue;
    this->init();
}

void MessageValidator::init() {
    this->log = new StringBuilder();
}

std::thread MessageValidator::run() {
    return std::thread([=] { this->runValidation(); });
}

void MessageValidator::runValidation() {
    for(;;) {

        bool hello;
        int sock;
        long size;
        RawMessage *rawMsg;
        std::vector<std::string> separatedMessages;

        rawMsg = this->rawMessageQueue->pop();
        sock = rawMsg->getSock();
        size = rawMsg->getSize();


        // separating messages
        separatedMessages = this->separateMessages(*rawMsg);

        for(std::string sMsg: separatedMessages) {
            std::string msgValidText;

            hello = this->checkHelloPacket(sMsg, &msgValidText);

            // checksum check
            if(!hello && !this->checkMessageChecksum(sMsg, &msgValidText)) {
                continue;
            }

            // msgValidText contains text of a valid message
            Message *m = new Message(sock, size, msgValidText);

            std::string logTxt = msgValidText;
            removeChar(&logTxt, '\n');

            this->log->clear();
            this->log->append("MSGValidator, a message (");
            this->log->append(logTxt);
            this->log->append(") accepted.");
            Logger::info(this->log->getString());

            this->messageQueue->push(m);
        }
    }
}

std::vector<std::string> MessageValidator::separateMessages(RawMessage msg){
    std::vector<std::string> messages;

    std::string txt = msg.getMessage();

    long stxPos = -1,
        etxPos = -1;


    if(txt.find(Message::STX) == std::string::npos || txt.find(Message::ETX) == std::string::npos)
        return messages;


    for(std::string::size_type i = 0; i < txt.size(); ++i) {
        int aChar = txt[i],
            aSTX = Message::STX,
            aETX = Message::ETX;

        if(aChar == aSTX) {
            stxPos = (long) i;
        } else if(aChar == aETX && stxPos > -1) {
            etxPos = (long) i;
        }

        if(stxPos > -1 && etxPos > -1) {
            messages.push_back(txt.substr(stxPos + 1lu, etxPos - stxPos - 1lu));
            stxPos = etxPos = -1;
        }
    }

    //printVector(messages);
    return messages;
}

bool MessageValidator::checkHelloPacket(std::string msg, std::string *pureMessage) {
    if(msg.find(Message::HELLO_PACKET) == std::string::npos || msg.length() != Message::HELLO_PACKET.length())
        return false;

    (*pureMessage) = msg;
    return true;
}

bool MessageValidator::checkMessageChecksum(std::string msg, std::string *pureMessage) {
    // "checksum;message"
    // STX + SUM + DELIM + TXT + ETX
    // *608;Ahoooj#

    long checkSum;

    size_t msgLen = msg.length();

    u_long delimPos = msg.find(Message::DELIMITER);

    std::string checkSumStr = msg.substr(0, delimPos),
                message = msg.substr(delimPos + 1, msgLen - delimPos);

    if(!isNumber(checkSumStr))
        return false;

    checkSum = stol(checkSumStr);
    (*pureMessage) = message;

    return checkSum == checksum(message, Message::MSG_CHECKSUM_MODULO);
}


