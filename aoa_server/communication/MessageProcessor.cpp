#include <thread>
#include <stdlib.h>


#include "MessageProcessor.h"
#include "MessageSerializer.h"
#include "MessageParser.h"
#include "../partial/tools.h"
#include "../core/Logger.h"
#include "../partial/StringBuilder.h"
#include "../core/Application.h"

MessageProcessor::MessageProcessor(SafeQueue<Message *> *messageQueue, SafeQueue<Message *> *sendMessageQueue) {
    this->messageQueue = messageQueue;
    this->sendMessageQueue = sendMessageQueue;
    this->init();
}

void MessageProcessor::init() {
    this->sbMsg = new StringBuilder();
    this->log = new StringBuilder();
    this->serializer = new MessageSerializer();
    this->parser = new MessageParser();
}

std::thread MessageProcessor::run(){
    return std::thread([=] { this->runProcessing(); });
}

void MessageProcessor::runProcessing(){

    bool stop = false;

    for(;;) {
        if(stop) break;

        Message *msg = this->messageQueue->pop();
        Logger::info("MSGProcessor: got a message.");

        if(!this->handleMessageType(msg))
            continue;

        this->perform(msg);
    }
}

bool MessageProcessor::handleMessageType(Message *msg) {
    MessageType msgType;

    std::string txt,
                msgTypeStr,
                msgBody;

    u_long delimPos;

    txt = msg->getMessage();
    delimPos = txt.find(Message::DELIMITER);
    msgTypeStr = txt.substr(0, delimPos);
    msgBody = txt.substr(delimPos + 1);

    if(this->checkHelloPacket(txt)) {
        msg->setType(convertInternalMessageType(0));
        return true;
    }

    if(!isNumber(msgTypeStr)) {
        return false;
    }

    msgType = convertInternalMessageType(atoi(msgTypeStr.c_str()));
    msg->setType(msgType);
    msg->setMessage(msgBody);

    return true;
}


bool MessageProcessor::checkHelloPacket(std::string msg) {
    return msg.find(Message::HELLO_PACKET) == 0l && msg.length() == Message::HELLO_PACKET.length();
}

void MessageProcessor::perform(Message *msg){

    this->clientSocket = msg->getSock();

    switch(msg->getType()) {
        case HELLO: this->proceedHelloPacket(); break;
        case SIGN_IN: this->proceedSignIn(msg); break;
        case GAME_LIST: this->proceedGameList(); break;
        case GAME_NEW: this->proceedNewGame(msg); break;
        case GAME_JOIN: this->proceedJoinGame(msg); break;
        case GAME_START: this->proceedStartGame(); break;
        case TURN_DATA: this->proceedTurnData(msg); break;
        case GAME_LEAVE: this->proceedLeaveGame(msg); break;
        default: case SIGN_OUT: this->proceedSignOut(msg); break;
    }
}

void MessageProcessor::answerMessage(){
    Message *m = new Message(this->clientSocket, this->sbMsg->getString());
    this->sendMessageQueue->push(m);
}

void MessageProcessor::clearMsg() {
    this->sbMsg->clear();
}

void MessageProcessor::answerMessageAndClean() {
    this->answerMessage();
    this->clearMsg();
}

void MessageProcessor::prepare(Player &player, Room *room) {
    player = this->app->getPlayer(this->clientSocket);
    (*room) = this->app->getRoom(player.getRoomID());
}


void MessageProcessor::proceedHelloPacket() {
    this->log->clear();
    this->log->append("MSGProcessor, processing: hello.");
    Logger::info(this->log->getString());

    this->sbMsg->append(Message::HELLO_PACKET_RESPONSE);
    this->answerMessageAndClean();
}

/**
 * Task: check username availability. Store
 * @param msg
 */
void MessageProcessor::proceedSignIn(Message *msg) {

    int uID = msg->getSock();
    std::string username = msg->getMessage();

    this->log->clear();
    this->log->append("MSGProcessor, processing signin: ");
    this->log->append(username);
    Logger::info(this->log->getString());

    sbMsg->append(msg->getType());
    sbMsg->append(Message::DELIMITER);

    if(this->app->registerUser(uID, username)) {
        // accepted
        sbMsg->append(Message::ACK);
        sbMsg->append(Message::DELIMITER);
        sbMsg->append(uID); // UID
    } else {
        // rejected
        sbMsg->append(Message::NACK);
    }

    this->answerMessageAndClean();
}

void MessageProcessor::proceedGameList() {
    std::map<int, Room> rooms;

    this->log->clear();
    this->log->append("MSGProcessor, processing gamelist:");
    Logger::info(this->log->getString());

    rooms = this->app->getRooms();
    std::string roomStr = this->serializer->serializeRooms(rooms);

    // tmp
    this->log->clear();
    this->log->append("MSGProcessor, rooms serialized: ");
    this->log->append(roomStr);
    Logger::info(this->log->getString());

    this->sbMsg->append(MessageType::GAME_LIST);
    this->sbMsg->append(Message::DELIMITER);
    this->sbMsg->append(roomStr);

    // type ; r-id ; p-count ; p-limit ; diff ; dim ; nicks
    //sbMsg->append("2;1;1;2;1;3;marty;2;2;2;2;5;dendasda:gabin");
                   //2;0;1;2;0;2;Marty;1;2;2;2;4;dendasda:gabin
    this->answerMessageAndClean();
}

/**
 * incoming:    msg-type ; p-limit ; diff ; dim
 *                  3    ;     2   ;   0  ;  3
 * outcoming:   msg-type ; (N)ACK ; r-id ; p-count ; p-limit ; diff ; dim ; nicks
 *                  3    ;    1   ;   2  ;    0    ;    1    ;   2  ;  4  ; a:b:c
 * @param msg
 */
void MessageProcessor::proceedNewGame(Message *msg) {
    int rid, uid;
    std::string roomStr;
    Room *room;

    this->log->clear();
    this->log->append("MSGProcessor, processing newgame:");
    Logger::info(this->log->getString());

    room = this->parser->parseNewRoomRequest(msg);
    rid = this->app->createNewRoom(room);
    uid = msg->getSock();

    this->app->assignPlayer(uid, rid);
    (*room) = this->app->getRoom(rid);

    this->sbMsg->append(MessageType::GAME_NEW);
    this->sbMsg->append(Message::DELIMITER);
    this->sbMsg->append(Message::ACK);
    this->sbMsg->append(Message::DELIMITER);

    // type ; (N)ACK ; r-id ; p-count ; p-limit ; diff ; dim ; nicks
    //  3   ;    1   ;   2  ;    0    ;    1    ;   2  ;  4  ; a:b:c
    roomStr = this->serializer->serializeRoom(*room);
    sbMsg->append(roomStr);
    this->answerMessageAndClean();


    if(this->app->startGameIfReady(rid)) {
        this->proceedStartGame(*room);
    }
}


/**
 * incoming:    msg-type ; r-id
 *                  4    ;   2
 * outcoming:   msg-type ; ack ; r-id ; p-count ; p-limit ; diff ; dim ; nicks
 *                  4    ;  1  ;   2  ;    0    ;    1    ;   2  ;  4  ; a:b:c
 *                  4    ;  0
 * @param msg
 */
void MessageProcessor::proceedJoinGame(Message *msg) {
    int rid;
    bool joinResult;
    Room room;
    std::string roomStr;

    this->log->clear();
    this->log->append("MSGProcessor, processing joingame:");
    Logger::info(this->log->getString());


    this->sbMsg->clear();
    this->sbMsg->append(MessageType::GAME_JOIN);
    this->sbMsg->append(Message::DELIMITER);

    rid = this->parser->parseJoinRoomRequest(msg->getMessage());
    joinResult = this->app->joinRoom(msg->getSock(), rid);

    if(joinResult) {

        // player was joined
        room = this->app->getRoom(rid);
        roomStr = this->serializer->serializeRoom(room);

        this->sbMsg->append(Message::ACK);
        this->sbMsg->append(Message::DELIMITER);
        this->sbMsg->append(roomStr);

    } else {

        // room is already full
        this->sbMsg->append(Message::NACK);

    }

    this->answerMessageAndClean();

    if(joinResult && this->app->startGameIfReady(rid)) {
        this->proceedStartGame(room);
    }
}

void MessageProcessor::proceedStartGame() {
    this->log->clear();
    this->log->append("MSGProcessor, processing startgame:");
    Logger::info(this->log->getString());

    sbMsg->append(MessageType::GAME_START);
    sbMsg->append(Message::DELIMITER);
    sbMsg->append(Message::ACK);
}

void MessageProcessor::proceedFirstTurnData() {
    Room r;
    Player p;

    this->log->clear();
    this->log->append("MSGProcessor, processing FIRST turndata:");
    Logger::info(this->log->getString());

    this->prepare(p, &r);
    this->proceedTurnDataBase(true);
}

void MessageProcessor::proceedTurnDataBase(bool ack) {
    int turn, turnTime;
    Room r;
    Player p;

    sbMsg->append(MessageType::TURN_DATA);
    sbMsg->append(Message::DELIMITER);

    if(!ack) {
        //NACK
        sbMsg->append(Message::NACK);
        return;
    }

    this->prepare(p, &r);

    turn = r.getTurn();
    turnTime = turn * Game::MOVE_TIME;
    if(turn == 1)
        turnTime += Game::FIRST_TURN_RESERVE;


    sbMsg->append(Message::ACK);
    sbMsg->append(Message::DELIMITER);
    sbMsg->append(r.getActivePlayerID());
    sbMsg->append(Message::DELIMITER);
    sbMsg->append(turn);
    sbMsg->append(Message::DELIMITER);
    sbMsg->append(turnTime);
}


/**
 * incoming: 0;0;0; 4;1;1; 8;3;4
 * @param msg
 */
void MessageProcessor::proceedTurnData(Message *msg) {
    std::string progressStr;
    std::queue<int> progress;


    int rid;
    bool result;

    this->log->clear();
    this->log->append("MSGProcessor, processing turndata:");
    Logger::info(this->log->getString());


    Player &p = this->app->getOnlinePlayer(this->clientSocket);
    Room &r = this->app->getRoom(p.getRoomID());

    rid = r.getID();
    progressStr = msg->getMessage();

    this->parser->parseTurn(progressStr, progress);
    result = this->app->proceedTurn(rid, progress);


    // 4 s / tah?
    // msg-type;(N)ACK;active-player-id;turn;time;move-pos;move-col;move-shape;move-pos;move-col;move-shape;...
    //    6    ;   1  ;       1        ;  5 ; 20 ;    0   ;    1   ;     1    ;    2   ;    2   ;    3     ;    3;4;1;  3;5;0;  0;7;2
    //sbMsg->append("6;1;5;20;0;1;1;2;2;3;3;4;1;3;5;0;0;7;2;2");


    r.startTurn();

    this->proceedTurnDataBase(result);

    if(result) {
        this->sbMsg->append(Message::DELIMITER);
        this->sbMsg->append(progressStr);
    }

    Logger::info("--------- sending a new progress: ");
    Logger::info(this->sbMsg->getString());

    this->answerRoomAndClean(r, &MessageProcessor::answerMessage);


    if(!result) {
        this->proceedEndGame(r);
    }
}

void MessageProcessor::proceedEndGame(Room &room) {
    this->log->clear();
    this->log->append("MSGProcessor, processing endgame:");
    Logger::info(this->log->getString());

    this->sbMsg->clear();
    this->sbMsg->append(MessageType::GAME_END);
    this->sbMsg->append(Message::DELIMITER);
    this->sbMsg->append(room.getWinnerID());

    this->answerRoomAndClean(room, &MessageProcessor::answerMessage);
}

void MessageProcessor::proceedLeaveGame(Message *msg) {
    this->log->clear();
    this->log->append("MSGProcessor, processing leavegame:");
    Logger::info(this->log->getString());

}

void MessageProcessor::proceedSignOut(Message *msg) {
    this->log->clear();
    this->log->append("MSGProcessor, processing signout:");
    Logger::info(this->log->getString());

}

void MessageProcessor::setApp(Application *app) {
    this->app = app;
}

void MessageProcessor::proceedStartGame(Room& r) {
    this->proceedStartGame();

    this->answerRoomAndClean(r, &MessageProcessor::answerMessage);

    this->proceedFirstTurnData();

    this->answerRoomAndClean(r, &MessageProcessor::answerMessage);

}

void MessageProcessor::answerRoomAndClean(const Room &r, void (MessageProcessor::*callback)()) {
    std::queue<int> socks;

    socks = r.getPlayerSockets();

    while(!socks.empty()) {
        this->clientSocket = socks.front();
        (this->*callback)();
        socks.pop();
    }

    this->clearMsg();
}












