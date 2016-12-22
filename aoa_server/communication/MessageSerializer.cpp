#include <iostream>
#include <string>
#include <vector>

#include "MessageSerializer.h"
#include "Message.h"


////const std::string MessageSerializer::MSG_STX = "\u0002";
//const std::string MessageSerializer::MSG_STX = "\x02";
//const std::string MessageSerializer::MSG_ETX = "\x03";
//
//std::vector<std::string> MessageSerializer::separateMessages(std::string input){
//    std::vector<std::string> messages;
//
////    messages.push_back(std::string("ahoj"));
////
////    size_t stxPosition;
////
////    std::cout << "BAF" << input.find(MSG_STX) << std::endl;
////
////    while((stxPosition = input.find(MSG_STX)) != std::string::npos) {
////        // STX exists
////
////        std::cout << "position: " << stxPosition << std::endl;
////
////        break;
////
////    }
//
//
//    return messages;
//}