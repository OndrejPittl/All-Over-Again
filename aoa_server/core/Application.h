#ifndef APP_H
#define APP_H


#include <string>
#include <vector>
#include <map>
#include <queue>

#include "../game/Player.h"
#include "../game/Room.h"
#include "../partial/StringBuilder.h"
#include "../partial/Indexer.h"
#include "../game/Game.h"


class ConnectionManager;
class CommunicationManager;


class Application {
    private:

        Game *game;

        ConnectionManager *conn;

        CommunicationManager *comm;

        /**
         * Used for assigning an existing user to a relevant game.
         */
        std::map <std::string, int> usernames;

        /**
         * Users connected to a server and are interacting with an app.
         */
        PlayerMap onlinePlayers;

        /**
         *  Users previously connected to a server however without a response, BUT not signed out!
         */
        //PlayerVector offlinePlayers;
        PlayerMap offlinePlayers;

        Indexer *offlinePlayerIndexer;

        /**
         * This vector holds socket values/ids of clients being suspicious
         * of hacking with sending invalid messages.
         */
        std::vector<int> suspiciousClients;

        /**
         * Existing rooms.
         */
        RoomMap rooms;

        Indexer *roomIndexer;

        StringBuilder *log;


        void init();

        void fillMockRooms();

        bool checkUsernameAvailability(std::string username);


    public:

        Application();

        Application(ConnectionManager *conn, CommunicationManager *comm);

        void registerUser(int uid);

        bool signInUser(int uid, std::string username);

        void signOutUser(int uid);

        RoomMap &getRooms();

        Room *createNewRoom();

        void assignPlayer(Player *player, Room *room);

        void assignPlayer(int uid, int roomID);

        Room *getRoom(int rid);

        bool joinRoom(int uid, int rid);

        bool startGameIfReady(Room *room);

        void checkRoomCancel(int rid);

        void removeOnlineUser(int uid);

        void removeOfflineUser(int uid);

        bool proceedTurn(int rid, const std::queue<int> &progress);

        Player *getPlayer(int uid);

        Player *getOfflinePlayer(int uid);

        void storePlayer(Player *p);

        int storeOfflinePlayer(Player *p);

        void leaveRoomCheckCancel(Player *player);

        void deregisterUser(int uid);

        void cancelRoom(Room *room);

        void disbandRoom(Room *room);

        void registerSuspiciousBehaviour(int uid);

        void handleSuspiciousClients();

        void reassignPlayer(Player *player);

        void leaveRoom(Player *player);

        void freeUsername(Player *player);

    void removeRoom(Room *room);
};


#endif
