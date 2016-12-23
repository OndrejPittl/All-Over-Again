
#include <string>
#include <thread>
#include <iostream>

#include "MessageProcessor.h"
#include "../partial/tools.h"

//const std::string MessageProcessor::MSG_STX = "\u0002";
const char MessageProcessor::MSG_STX = '*';  //'\x02'
const char MessageProcessor::MSG_ETX = '#';  //'\x03'
const char MessageProcessor::MSG_DELIMITER = ';';


MessageProcessor::MessageProcessor(SafeQueue<Message *> *queue, SafeQueue<RawMessage *> *readableMessages) {
    this->messageQueue = queue;
    this->readableMessages = readableMessages;
}

std::thread MessageProcessor::run() {
    return std::thread([=] { this->runProcessing(); });
}

void MessageProcessor::runProcessing() {
    bool stop = false;

    for(;;) {

        if(stop) break; // TODO: Tmp - CLion warn highlight.

        int sock;
        long size;
        RawMessage *rawMsg;
        std::vector<std::string> separatedMessages;

        rawMsg = this->readableMessages->pop();
        sock = rawMsg->getSock();
        size = rawMsg->getSize();


        // separating messages
        separatedMessages = this->separateMessages(*rawMsg);

        for(std::string sMsg: separatedMessages) {
            std::string msgValidText;

            // checksum check
            if(!this->checkMessageChecksum(sMsg, &msgValidText)) {
                continue;
            }

            // msgValidText contains text of a valid message
            Message *m = new Message(sock, size, msgValidText);

            std::string logTxt = msgValidText;
            removeChar(&logTxt, '\n');

            std::cout << "A message (" << logTxt << ") accepted." << std::endl;
            this->messageQueue->push(m);
        }
    }
}

std::vector<std::string> MessageProcessor::separateMessages(RawMessage msg){
    std::vector<std::string> messages;

    std::string txt = msg.getMessage();

    long stxPos = -1,
        etxPos = -1;


    for(std::string::size_type i = 0; i < txt.size(); ++i) {
        int aChar = txt[i],
            aSTX = MSG_STX,
            aETX = MSG_ETX;

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

    printVector(messages);


//    while(stxPosition != std::string::npos) {
//        // STX exists
//
//
//
//
//        break;
//
//    }


    return messages;
}

bool MessageProcessor::checkMessageChecksum(std::string msg, std::string *pureMessage) {
    // "checksum;message"
    // STX + SUM + DELIM + TXT + ETX
    // *608;Ahoooj#

    long checkSum;

    u_long delimPos = msg.find(MSG_DELIMITER);

    std::string checkSumStr = msg.substr(0, delimPos),
                message = msg.substr(delimPos + 1, msg.length() - delimPos);

    if(!isNumber(checkSumStr))
        return false;

    checkSum = stol(checkSumStr);
    (*pureMessage) = message;

    return checkSum == checksum(message);
}
