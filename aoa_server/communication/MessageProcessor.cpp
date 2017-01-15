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
    for(;;) {
        Message *msg = this->messageQueue->pop();
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

void MessageProcessor::perform(Message *msg){
    this->clientSocket = msg->getSock();

    switch(msg->getType()) {
        case HELLO: this->proceedHelloPacket(); break;
        case SIGN_IN: this->proceedSignIn(msg); break;
        case GAME_LIST: this->proceedGameList(); break;
        case GAME_NEW: this->proceedNewGame(msg); break;
        case GAME_JOIN: this->proceedJoinGame(msg); break;
        case GAME_START: this->proceedRestartGame(); break;
        case TURN_DATA: this->proceedTurnData(msg); break;
        case GAME_LEAVE: this->proceedLeaveGame(); break;
        default: case SIGN_OUT: this->proceedSignOut(msg); break;
    }
}

/**
 * incoming:    Hey AOA! How are you?
 * outcoming:   Hey Client! I am fine.
 */
void MessageProcessor::proceedHelloPacket() {
    this->log->clear();
    this->log->append("MSGProcessor, processing: hello.");
    Logger::info(this->log->getString());

    this->sbMsg->append(Message::HELLO_PACKET_RESPONSE);
    this->answerMessageAndClean();
}

/**
 * Task: check username availability. Store
 *
 * incoming:    msg-type ; nick
 *                  1    ;  xxx
 * outcoming:   msg-type ; ack ;  uid
 *                  1    ;  1  ;   5
 *                  1    ;  0
 * @param msg
 */
void MessageProcessor::proceedSignIn(Message *msg) {
    int uid = msg->getSock();
    std::string username = msg->getMessage();

    this->log->clear();
    this->log->append("MSGProcessor, processing signin: ");
    this->log->append(username);
    Logger::info(this->log->getString());

    sbMsg->append(msg->getType());
    sbMsg->append(Message::DELIMITER);

    if(this->app->signInUser(uid, username)) {
        // accepted
        sbMsg->append(Message::ACK);
        sbMsg->append(Message::DELIMITER);
        sbMsg->append(uid); // UID
    } else {
        // rejected
        sbMsg->append(Message::NACK);
    }

    this->answerMessageAndClean();
}

/**
 *
 * incoming:    msg-type
 *                  2
 * outcoming:   msg-type ; ack ; r-id ; p-count ; p-limit ; diff ; dim ; nicks
 *                  2    ;  1  ;   2  ;    0    ;    2    ;   2  ;  4  ; a:b:c
 */
void MessageProcessor::proceedGameList() {
    RoomMap rooms;

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

    // type ; r-ID ; p-count ; p-limit ; diff ; dim ; nicks
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
    this->log->clear();
    this->log->append("MSGProcessor, processing newgame:");
    Logger::info(this->log->getString());

    std::string roomStr;
    Room *room = this->app->createNewRoom();
    Player *player = this->app->getPlayer(this->clientSocket);

    this->parser->parseNewRoomRequest(msg, room);
    this->app->assignPlayer(player, room);


    // type ; (N)ACK ; r-ID ; p-count ; p-limit ; diff ; dim ; nicks
    //  3   ;    1   ;   2  ;    0    ;    1    ;   2  ;  4  ; a:b:c
    roomStr = this->serializer->serializeRoom(room);

    this->sbMsg->append(MessageType::GAME_NEW);
    this->sbMsg->append(Message::DELIMITER);
    this->sbMsg->append(Message::ACK);
    this->sbMsg->append(Message::DELIMITER);
    this->sbMsg->append(roomStr);
    this->answerMessageAndClean();

    if(this->app->startGameIfReady(room)) {
        this->proceedStartGame(room, true);
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
    std::string roomStr;

    this->log->clear();
    this->log->append("MSGProcessor, processing joingame:");
    Logger::info(this->log->getString());


    this->sbMsg->clear();
    this->sbMsg->append(MessageType::GAME_JOIN);
    this->sbMsg->append(Message::DELIMITER);

    rid = this->parser->parseJoinRoomRequest(msg->getMessage());
    joinResult = this->app->joinRoom(msg->getSock(), rid);
    Room *room = this->app->getRoom(rid);

    if(joinResult) {

        // player was joined
        roomStr = this->serializer->serializeRoom(room);

        this->sbMsg->append(Message::ACK);
        this->sbMsg->append(Message::DELIMITER);
        this->sbMsg->append(roomStr);

    } else {

        // room is already full
        this->sbMsg->append(Message::NACK);

    }

    this->answerMessageAndClean();

    if(joinResult && this->app->startGameIfReady(room)) {
        this->proceedStartGame(room, true);
    }
}

/**
 *
 * incoming:    msg-type
 *                  5
 * outcoming:   msg-type ; ack
 *                  5    ;  1
 *                  5    ;  0
 */
void MessageProcessor::proceedRestartGame() {
    this->log->clear();
    this->log->append("MSGProcessor, processing restartgame:");
    Logger::info(this->log->getString());

    Player *p = this->app->getPlayer(this->clientSocket);
    Room *r = this->app->getRoom(p->getRoomID());


    // request on a game endGame

    // waits until all players are ready to play again
    if(!r->checkReadyToContinue(true))
        return;


    // everybody sent a response

    if(r->isReplayReady()) {

        r->restart();
        r->startTurn();

        // everybody wants to replay a game -> start game


    }

//    else {
//
//        // end game
//        this->proceedEndGame(r);
//
//    }

    this->proceedStartGame(r, r->isReplayReady());
    if(r->hasGameEnded()) this->proceedLeaveGame();
}


/**
 *
 */
void MessageProcessor::proceedFirstTurnData() {
    this->log->clear();
    this->log->append("MSGProcessor, processing FIRST turndata:");
    Logger::info(this->log->getString());

    this->proceedTurnDataBase(true);
}

void MessageProcessor::proceedTurnDataBase(bool ack) {
    int turn, turnTime;

    Player *p = this->app->getPlayer(this->clientSocket);
    Room *r = this->app->getRoom(p->getRoomID());

    sbMsg->append(MessageType::TURN_DATA);
    sbMsg->append(Message::DELIMITER);

    if(!ack) {
        //NACK
        sbMsg->append(Message::NACK);
        return;
    }

    turn = r->getTurn();
    turnTime = r->getTime();

    sbMsg->append(Message::ACK);
    sbMsg->append(Message::DELIMITER);
    sbMsg->append(r->getActivePlayerID());
    sbMsg->append(Message::DELIMITER);
    sbMsg->append(turn);
    sbMsg->append(Message::DELIMITER);
    sbMsg->append(turnTime);
}

/**
 * incoming: 0;0;0; 4;1;1; 8;3;4
 *
 * incoming:    msg-type ; move-pos ; move-col ; move-sym ; move-pos ; move-col ; move-sym ; ...
 *                  6    ;    1     ;     1    ;    2     ;    3     ;     3    ;    8     ; ...
 * outcoming:   msg-type ;   ack    ;    uid   ;   turn   ;   time   ; move-pos ; move-col ; move-sym ; ...
 *                  6    ;    1     ;     5    ;    2     ;    8     ;     1    ;    1     ;     2    ; ...
 *                  6    ;  0
 *
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


    Player *p = this->app->getPlayer(this->clientSocket);
    rid = p->getRoomID();
    Room *r = this->app->getRoom(rid);
    progressStr = msg->getMessage();

    this->parser->parseTurn(progressStr, progress);
    result = this->app->proceedTurn(rid, progress);


    r->startTurn();

    this->proceedTurnDataBase(result);

    if(result) {
        this->sbMsg->append(Message::DELIMITER);
        this->sbMsg->append(progressStr);
    }

    Logger::info("--------- sending a new progress: ");
    Logger::info(this->sbMsg->getString());

    this->answerRoomAndClean(r, &MessageProcessor::answerMessage);

    Logger::info("--------- sent to all");

    if(!result) {
        Logger::info("--------- send END game:");
        this->proceedEndGame(r);
    }
}

/**
 *
 * incoming:    msg-type
 *                  7
 * outcoming:   msg-type ; uid
 *                  7    ;  5
 * @param room
 */
void MessageProcessor::proceedEndGame(Room *room) {
    this->log->clear();
    this->log->append("MSGProcessor, processing endgame:");
    Logger::info(this->log->getString());

    this->sbMsg->clear();
    this->sbMsg->append(MessageType::GAME_END);
    this->sbMsg->append(Message::DELIMITER);
    this->sbMsg->append(room->getWinnerID());


    this->answerRoomAndClean(room, &MessageProcessor::answerMessage);
    room->endGame();
}

/**
 *
 * incoming:    msg-type
 *                  8
 * @param msg
 */
void MessageProcessor::proceedLeaveGame() {
    this->log->clear();
    this->log->append("MSGProcessor, processing leavegame:");
    Logger::info(this->log->getString());

    Player *p = this->app->getPlayer(this->clientSocket);
    Room *r = this->app->getRoom(p->getRoomID());

    this->app->leaveRoom(p);

    if(r->checkReadyToContinue(false)) {
        // a second player replies: does not wont to play again
        // -> send game start NACK

        this->proceedStartGame(r, false);
        this->app->disbandRoom(r);
    }
}

/**
 *
 * incoming:    msg-type
 *                  9
 * @param msg
 */
void MessageProcessor::proceedSignOut(Message *msg) {
    this->log->clear();
    this->log->append("MSGProcessor, processing signout:");
    Logger::info(this->log->getString());

    Player *p = this->app->getPlayer(this->clientSocket);
    this->app->removeUser(p);
}

void MessageProcessor::setApp(Application *app) {
    this->app = app;
}

void MessageProcessor::proceedStartGame(Room *r, bool ack) {
    this->log->clear();
    this->log->append("MSGProcessor, processing startgame:");
    Logger::info(this->log->getString());

    this->sbMsg->append(MessageType::GAME_START);
    this->sbMsg->append(Message::DELIMITER);


    if(!ack) {
        this->sbMsg->append(Message::NACK);
        this->answerRoomAndClean(r, &MessageProcessor::answerMessage);
        //this->proceedLeaveGame();
        return;
    }

    this->sbMsg->append(Message::ACK);
    this->answerRoomAndClean(r, &MessageProcessor::answerMessage);
    r->changeStatus(GameStatus::STARTED);

    this->proceedFirstTurnData();
    this->answerRoomAndClean(r, &MessageProcessor::answerMessage);
    r->changeStatus(GameStatus::PLAYING);
}

void MessageProcessor::answerRoomAndClean(const Room *r, void (MessageProcessor::*callback)()) {
    std::queue<int> socks;

    socks = r->getPlayerSockets();

    while(!socks.empty()) {
        this->clientSocket = socks.front();
        (this->*callback)();

        this->log->clear();
        this->log->append("sending to: ");
        this->log->append(std::to_string(this->clientSocket));
        Logger::info(this->log->getString());

        socks.pop();
    }

    this->clearMsg();
}

void MessageProcessor::answerMessage(){
    Message *m = new Message(this->clientSocket, this->sbMsg->getString());
    this->sendMessageQueue->push(m);
}

void MessageProcessor::answerMessageAndClean() {
    this->answerMessage();
    this->clearMsg();
}

void MessageProcessor::clearMsg() {
    this->sbMsg->clear();
}

bool MessageProcessor::checkHelloPacket(std::string msg) {
    return msg.find(Message::HELLO_PACKET) == 0l && msg.length() == Message::HELLO_PACKET.length();
}



