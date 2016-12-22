
#include <string>
#include <thread>
#include <iostream>
#include <sys/types.h>
#include <sys/socket.h>

#include "Receiver.h"
#include "../core/Logger.h"
#include "../partial/Semaphore.h"
#include "MessageSerializer.h"




Receiver::Receiver(SafeQueue<Message *> *queue, SafeQueue<Message *> *readableMessages) {
    this->messageQueue = queue;
    this->readableMessages = readableMessages;
    this->serializer = new MessageSerializer();
}

std::thread Receiver::run() {
    return std::thread([=] { this->runReceiving(); });
}

void Receiver::runReceiving() {

    bool stop = false;

    for(;;) {

        // TODO: Tmp - CLion warn highlight.
        if(stop) break;

        int sock, size;
        Message *m;
        std::string strMsg;

        m = this->readableMessages->pop();
        std::cout << "popping a message" << std::endl;
        sock = m->getSock();
        size = m->getSize();

        strMsg = this->recvMsg(sock, size);
        std::cout << "---------------------" << std::endl;
        std::cout << size << " bytes received in a message: " << strMsg;


        // vypocty a kontroly
        //this->serializer->separateMessages(strMsg);




        // inicializace msg


        // nacpani do fronty s hotovkami
        //this->messageQueue->push(m);


//        for (int i = 0; i < 10; ++i) {
//            std::cout << "Pozdrav z vlakna: " << i << std::endl;
//        }

        std::cout << "A message was accepted." << std::endl << "---------------------" << std::endl;
    }
}

//void CommunicationManager::recvMsg(int sock, int byteCount, std::string *buff) {
std::string Receiver::recvMsg(int sock, int byteCount) {

    // result of an operation
    ssize_t result;

    // message length in bytes
    size_t msgLen = byteCount * sizeof(char);

    // buffer
    char buffer[msgLen];

    // clear memory
    memset(buffer, 0, msgLen + 1);

    // receive data
    result = recv(sock, buffer, msgLen, 0);
    //result = read(sock, msgBuffer, BUFF_LEN);

    // an error during receiving data
    if(result < 0) {
        Logger::printErr(ERR_MSG_RECEIVE);
    }

    return std::string(buffer);
}

void Receiver::dumpFunction(){

}