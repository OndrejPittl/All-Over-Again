#include "MessageSender.h"
#include "../connection/ConnectionManager.h"


MessageSender::MessageSender(SafeQueue<Message *> *messageQueue) {
    this->messageQueue = messageQueue;
    this->sb = new StringBuilder();
}

std::thread MessageSender::run(){
    return std::thread([=] { this->runSending(); });
}

void MessageSender::runSending(){
    bool stop = false;

    for(;;) {
        if(stop) break;

        Message *msg = this->messageQueue->pop();

        std::cout << "sending a message: " << msg->getMessage() << std::endl;

        this->sb->append(Message::STX);
        this->sb->append(msg->getMessage());
        this->sb->append(Message::ETX);

        this->sendMsg(msg->getSock(), this->sb->getString());
        this->sb->clear();
    }
}


bool MessageSender::sendMsg(int sock, std::string txt) {
    // std::cout << "---> Sending " << txt << " to: " << sock << "." << std::endl;
    // return send(sock, txt.c_str(), txt.length(), MSG_NOSIGNAL) >= 0;
    return send(sock, txt.c_str(), txt.length(), 0) >= 0;
}

void MessageSender::sendMsg(fd_set *socks, std::string txt) {
    int fd;

    // std::cout << "---> Sending " << txt << " to all." << std::endl;
    for(fd = ConnectionManager::CLIENT_FD_OFFSET; fd < FD_SETSIZE; fd++) {
        if (FD_ISSET(fd, socks)) {
            sendMsg(fd, txt);
        }
    }
}