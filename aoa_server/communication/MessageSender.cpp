#include "MessageSender.h"
#include "../connection/ConnectionManager.h"
#include "../partial/tools.h"
#include "MessageValidator.h"
#include "../core/Logger.h"


MessageSender::MessageSender(SafeQueue<Message *> *messageQueue) {
    this->messageQueue = messageQueue;
    this->sb = new StringBuilder();
    this->log = new StringBuilder();
}

std::thread MessageSender::run(){
    return std::thread([=] { this->runSending(); });
}

void MessageSender::runSending(){
    bool stop = false;

    for(;;) {
        if(stop) break;

        Message *msg = this->messageQueue->pop();

        this->log->clear();
        this->log->append("\n");
        this->log->append(">>>>>>> sending a message: ");
        this->log->append(msg->getMessage());
        this->log->append("\n");
        Logger::info(this->log->getString());

        this->sb->append(Message::STX);
        this->sb->append(checksum(msg->getMessage(), Message::MSG_CHECKSUM_MODULO));
        this->sb->append(Message::DELIMITER);
        this->sb->append(msg->getMessage());
        this->sb->append(Message::ETX);

        this->sendMsg(msg->getSock(), this->sb->getString());
        this->sb->clear();
    }
}


bool MessageSender::sendMsg(int sock, std::string txt) {
    // return send(sock, txt.c_str(), txt.length(), MSG_NOSIGNAL) >= 0;
    return send(sock, txt.c_str(), txt.length(), 0) >= 0;
}

void MessageSender::sendMsg(fd_set *socks, std::string txt) {
    int fd;
    for(fd = ConnectionManager::CLIENT_FD_OFFSET; fd < FD_SETSIZE; fd++) {
        if (FD_ISSET(fd, socks)) {
            sendMsg(fd, txt);
        }
    }
}