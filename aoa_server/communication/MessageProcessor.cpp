#include <thread>
#include <stdlib.h>

#include "MessageProcessor.h"
#include "../partial/tools.h"
#include "../core/Logger.h"



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

    if(delimPos == std::string::npos) {
        // message contains only type of a message
        msgTypeStr = txt;
        msgBody = "";
    } else {
        // message contains type of a message and its body
        msgTypeStr = txt.substr(0, delimPos);
        msgBody = txt.substr(delimPos + 1);
    }

    if(this->checkHelloPacket(txt)) {
        msg->setType(convertInternalMessageType(0));
        return true;
    }

    if(!Tools::isNumber(msgTypeStr)) {
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
        case WAIT_READY: this->proceedWaitReady(msg); break;
        default: case SIGN_OUT: this->proceedSignOut(); break;
    }
}

/**
 * incoming:    Hey AOA! How are you?
 * outcoming:   Hey Client! I am fine.
 */
void MessageProcessor::proceedHelloPacket() {
    Logger::debug("MSGProcessor, processing: hello.");

    this->sbMsg->append(Message::HELLO_PACKET_RESPONSE);
    this->answerMessageAndClean();
}

/**
 * Task: check username availability. Store
 *
 * incoming:    msg-type ; nick
 *                  1    ;  xxx
 * outcoming:   msg-type ; ack ;  uid ; [0: new / 1: re-joined]
 *                  1    ;  1  ;   5  ;         0
 *                  1    ;  0
 * @param msg
 */
void MessageProcessor::proceedSignIn(Message *msg) {
    bool reJoin = false;
    int uid = msg->getSock();
    std::string username = msg->getMessage();

    // -- log --
    this->log->clear(); this->log->append("MSGProcessor, processing signin: ");
    this->log->append(username); Logger::debug(this->log->getString());

    sbMsg->append(msg->getType());
    sbMsg->append(Message::DELIMITER);

    if(this->app->signInUser(uid, username)) {
        // accepted
        sbMsg->append(Message::ACK);
        sbMsg->append(Message::DELIMITER);
        sbMsg->append(uid); // UID
        sbMsg->append(Message::DELIMITER);

        Player *p = this->app->getPlayer(uid);
        reJoin = p->hasRoom();

        if(reJoin) {
            sbMsg->append("1"); // re-joined
        } else {
            sbMsg->append("0"); // new
        }

    } else {
        // rejected
        sbMsg->append(Message::NACK);
    }

    this->answerMessageAndClean();


    if(reJoin)
        this->handleRejoin();

}

/**
 *
 * incoming:    msg-type
 *                  2
 * outcoming:   msg-type ; ack ; r-id ; p-count ; p-limit ; diff ; dim ; nicks
 *                  2    ;  1  ;   2  ;    0    ;    2    ;   2  ;  4  ; a:b:c
 */
void MessageProcessor::proceedGameList() {
    Logger::debug("MSGProcessor, processing gamelist:");

    RoomMap rooms;

    rooms = this->app->getRooms();
    std::string roomStr = this->serializer->serializeRooms(rooms);

    // -- log --
    this->log->clear(); this->log->append("MSGProcessor, rooms serialized: ");
    this->log->append(roomStr); Logger::debug(this->log->getString());

    this->sbMsg->append(MessageType::GAME_LIST);


    if(!roomStr.empty()) {
        this->sbMsg->append(Message::DELIMITER);
        this->sbMsg->append(roomStr);
    }

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
    Logger::debug("MSGProcessor, processing newgame:");

    Player *player = this->app->getPlayer(this->clientSocket);

    if(player->hasRoom())
        return;


    std::string roomStr;

    Room *room = this->app->createNewRoom();


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
    Logger::debug("MSGProcessor, processing joingame:");

    int rid;
    bool joinResult;

    if(this->app->getPlayer(this->clientSocket)->hasRoom())
        return;

    rid = this->parser->parseJoinRoomRequest(msg->getMessage());
    Room *room = this->app->getRoom(rid);

    if(room == nullptr || !room->isJoinable()) {
        joinResult = false;
    } else {
        joinResult = this->app->joinRoom(msg->getSock(), rid);
    }

    this->proceedJoinGame(room, joinResult);
}

void MessageProcessor::proceedJoinGame(Room *room, bool joinResult) {

    std::string roomStr;

    this->sbMsg->clear();
    this->sbMsg->append(MessageType::GAME_JOIN);
    this->sbMsg->append(Message::DELIMITER);

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



    bool readyToRejoin = this->reJoining
            && room->checkReadyToContinueAfterWait()
            && room->isReplayReadyAfterWait();

    bool startReady = joinResult && this->app->startGameIfReady(room);


    if((!this->reJoining && startReady) || readyToRejoin) {
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
    Logger::debug("MSGProcessor, processing restartgame:");

    Player *p = this->app->getPlayer(this->clientSocket);
    Room *r = this->app->getRoom(p->getRoomID());

    if(r == nullptr)
        return;


    // request a game endGame

    // waits until all players are ready to play again
    if(!r->checkReadyToContinue(true))
        return;

    // everybody sent a response

    if(r->isReplayReady()) {

        r->restart();

        // everybody wants to replay a game -> start game
    }

    this->proceedStartGame(r, r->isReplayReady());
    if(r->hasGameEnded()) this->proceedLeaveGame();
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
    Logger::debug("MSGProcessor, processing turndata:");

    int rid;
    bool result;
    std::string progressStr;
    std::queue<int> progress;

    Player *p = this->app->getPlayer(this->clientSocket);
    rid = p->getRoomID();
    Room *r = this->app->getRoom(rid);


    if(r == nullptr)
        return;


    if(msg == nullptr) {

        // FIRST turn || while WAITING for a player

        result = true;

    } else {

        // NOT first turn
        // ack dependent on progress (result var)

        if(r->isEverybodyOnline()) {
            // everybody online -> validate
            this->parser->parseTurn(msg->getMessage(), progress);
            result = this->app->proceedTurn(rid, progress);

        } else {

            // a response was received during waiting on a player
            // -> ignore progress but NOT ignore game end request

            // -- log --
            this->log->clear(); this->log->append("response while WAITING:");
            this->log->append(msg->getMessage());
            this->log->append(std::to_string(msg->getMessage().length()));
            Logger::debug(this->log->getString());


            // end game detection:
            if(msg->getMessage().empty()) {

                // END GAME!
                result = false;

            } else {
                result = true;
            }
        }
    }

    if(!this->reJoining && r->isEverybodyOnline())
        r->startTurn();

    progressStr = this->serializer->serializeRoomProgress(r);

    this->proceedTurnDataBase(result);

    // not first turn && progress ok
    if(r->hasProgress() && result) {

        this->sbMsg->append(Message::DELIMITER);
        this->sbMsg->append(progressStr);
    }

    this->answerRoomAndClean(r, &MessageProcessor::answerMessage);

    if(!result) {
        Logger::debug("--------- sending END game:");
        this->proceedEndGame(r);
    }
}

void MessageProcessor::proceedTurnDataBase(bool ack) {
    int turn, turnTime;

    Player *p = this->app->getPlayer(this->clientSocket);
    Room *r = this->app->getRoom(p->getRoomID());


    // ----- broadcast: player info
    this->proceedPlayerInfo();


    sbMsg->clear();
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
 *
 * incoming:    msg-type
 *                  7
 * outcoming:   msg-type ; uid
 *                  7    ;  5
 * @param room
 */
void MessageProcessor::proceedEndGame(Room *room) {
    Logger::debug("MSGProcessor, processing endgame:");

    this->sbMsg->clear();
    this->sbMsg->append(MessageType::GAME_END);
    this->sbMsg->append(Message::DELIMITER);
    this->sbMsg->append(room->getWinnerID());

    this->answerRoomAndClean(room, &MessageProcessor::answerMessage);
    room->endGame();
}

/**
 * incoming:    msg-type
 *                  8
 */
void MessageProcessor::proceedLeaveGame() {
    Logger::debug("MSGProcessor, processing leavegame:");

    Player *p = this->app->getPlayer(this->clientSocket);
    Room *r = this->app->getRoom(p->getRoomID());

    if(r == nullptr)
        return;


    if(r->checkReadyToContinue(false)) {
        // a second player replies: does not wont to play again
        // -> send game start NACK

        this->proceedStartGame(r, false);
        this->app->disbandRoom(r);
        //this->app->leaveRoomCheckCancel(p);
    }

    // WANTS to leave the room
    this->app->leaveRoomCheckCancel(p);
}

/**
 *
 * incoming:    msg-type
 *                  9
 * @param msg
 */
void MessageProcessor::proceedSignOut() {
    Logger::debug("MSGProcessor, processing signout:");
    this->app->deregisterUser(this->clientSocket);
}


/**
 * outcoming:    [uid] ; [username] ; [0: offline / 1: online]; [0: not active / 1: active];  ...
 *                 5   ;   ondra    ;             1           ;                1           ;  ...
 */
void MessageProcessor::proceedPlayerInfo(){
    Player *p = this->app->getPlayer(this->clientSocket);
    Room *r = this->app->getRoom(p->getRoomID());
    this->proceedPlayerInfo(r);
}

/**
 * incoming:    msg-type ; (N)ACK
 *                 11    ;   1
 */
void MessageProcessor::proceedWaitReady(Message *msg) {

    Player *p = this->app->getPlayer(this->clientSocket);
    Room *r = this->app->getRoom(p->getRoomID());

    // 1: wanna continue, 0: don't
     bool result = this->parser->isWaitReady(msg->getMessage());

     if(result) {

        // game continues
         if(!r->checkReadyToContinueAfterWait())
             return;

         // everybody checked
         if(r->isReplayReadyAfterWait()) {

             // continue game
             this->proceedStartGame(r, true);
             return;

         } // else: end game
     }


    // end game
    this->proceedStartGame(r, false);
    //this->app->leaveRoomCheckCancel(p);
    this->app->disbandRoom(r);
    this->reJoining = false;

}


/**
 * outcoming:    msg-type ; [uid] ; [username] ; [0: offline, 1: online] ; [0: not active, 1: active]
 *                  10    ;   1   ;     aaa    ;            1            ;              0
 */
void MessageProcessor::proceedPlayerInfo(Room *r){
    Logger::debug("MSGProcessor, processing playerinfo:");

    std::string playerInfo = this->serializer->serializeRoomPlayers(r);

    this->sbMsg->clear();
    this->sbMsg->append(MessageType::PLAYER_INFO);
    this->sbMsg->append(Message::DELIMITER);
    this->sbMsg->append(playerInfo);

    this->answerRoomAndClean(r, &MessageProcessor::answerMessage);
}

void MessageProcessor::handleRejoin() {
    Player *player = this->app->getPlayer(this->clientSocket);
    Room *room = this->app->getRoom(player->getRoomID());

    this->reJoining = true;
    this->proceedJoinGame(room, true);
}


void MessageProcessor::setApp(Application *app) {
    this->app = app;
}

void MessageProcessor::proceedStartGame(Room *r, bool ack) {
    Logger::debug("MSGProcessor, processing startgame:");

    this->sbMsg->append(MessageType::GAME_START);
    this->sbMsg->append(Message::DELIMITER);

    if(!ack) {
        this->sbMsg->append(Message::NACK);
        this->answerRoomAndClean(r, &MessageProcessor::answerMessage);
        return;
    }

    r->resetReadyToContinueAfterWait();

    this->sbMsg->append(Message::ACK);
    this->answerRoomAndClean(r, &MessageProcessor::answerMessage);
    r->changeStatus(GameStatus::STARTED);

    Tools::printRooms(this->app->getRooms());

    // new game with no previous progress
    // or rejoined
    this->proceedTurnData(nullptr);

    r->changeStatus(GameStatus::PLAYING);
    this->reJoining = false;
}

void MessageProcessor::answerRoomAndClean(const Room *r, void (MessageProcessor::*callback)()) {
    std::queue<int> socks;

    socks = r->getPlayerSockets();

    while(!socks.empty()) {
        this->clientSocket = socks.front();
        (this->*callback)();

        // -- log --
        this->log->clear(); this->log->append("sending (to all) to: ");
        this->log->append(std::to_string(this->clientSocket));
        Logger::debug(this->log->getString());

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

void MessageProcessor::handleUserGoneOffline(Room *r) {

    if(r->getStatus() == GameStatus::WAITING && r->hasGameFinished()) {
        // exit while another player is waiting for playing AGAIN after game has FINISHED
        this->proceedStartGame(r, false);
        this->app->disbandRoom(r);
        return;
    }

    // a user has left a room DURING PLAYING -> inform other players
    this->proceedPlayerInfo(r);
}





