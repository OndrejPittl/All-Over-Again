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
        //std::vector <Player> onlinePlayers;
        //std::map<int, Player> onlinePlayers;
        PlayerMap onlinePlayers;

        /**
         *  Users previously connected to a server however without a response, BUT not signed out!
         */
        //std::map<int, Player> offlineUsers;
        //std::vector<Player> offlinePlayers;
        PlayerVector offlinePlayers;

        /**
         * Existing rooms.
         */
        //std::map<int, Room*> rooms;
        RoomMap rooms;

        Indexer *roomIndexer;



//        int roomIndex;
//        std::queue<int> freedRoomIndexeQueue;




        StringBuilder *log;



        void init();

        bool checkUsernameAvailability(std::string username);

        //void deregisterUserFrom(Player& p, std::vector<Player>& users);


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

        int registerRoom(Room *r);

        bool joinRoom(int uid, int rid);

        bool startGameIfReady(Room *room);

        void checkRoomCancel(int rid);

        void removeUser(Player *player);

        bool proceedTurn(int rid, const std::queue<int> &progress);

        void cancelRoom(int rid);

        void removePlayer(int uid);

        Player *getPlayer(int uid);

        Player *getOfflinePlayer(int uid);

        void storePlayer(Player *p);

        int storeOfflinePlayer(Player *p);

        void leaveRoom(Player *player);

        void deregisterUser(int uid);

        void fillMockRooms();

        void cancelRoom(Room *room);

        void cancelRoomKick(Room *room);

        void disbandRoom(Room *room);
};


#endif
