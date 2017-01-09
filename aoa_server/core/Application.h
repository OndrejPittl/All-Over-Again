#ifndef APP_H
#define APP_H


#include <string>
#include <vector>
#include <map>
#include <queue>

#include "../game/Player.h"
#include "../game/Room.h"
#include "../partial/StringBuilder.h"
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
        //std::map <int, Player*> onlineUsers;
        std::map<int, Player> onlineUsers;

        /**
         *  Users previously connected to a server however without a response, BUT not signed out!
         */
        // std::vector<Player>
        std::map<int, Player> offlineUsers;

        /**
         * Existing rooms.
         */
        std::map<int, Room> rooms;


        int roomIndex;

        std::queue<int> freedRoomIndexeQueue;




        StringBuilder *log;



        void init();

        bool checkUsernameAvailability(std::string username);

        void deregisterUserFrom(Player& p, std::map<int, Player>& users);


    public:

        static const std::string USERNAME_VALIDATION_REGEX;

        Application();

        Application(ConnectionManager *conn, CommunicationManager *comm);

        bool validateUsername(std::string basic_string);

        bool registerUser(int uid, std::string username);

        void deregisterOnlineUser(int uid);

        //void setDependencies(ConnectionManager *conn, CommunicationManager *comm);

        std::map<int, Room> getRooms();

        int createNewRoom(Room *room);

        int getFreeRoomIndex();

        void assignPlayer(int uid, int roomID);

        Room& getRoom(int rid);

        void setRoom(Room& r);

        //void startGameIfReady(int rid);

        bool joinRoom(int uid, int rid);

        Player getPlayer(int uid);

    bool startGameIfReady(int rid);

    void checkRoomCancel(int rid);

    void deregisterUserCompletely(Player &player);

    bool proceedTurn(int rid, const std::queue<int> &progress);

    void cancelRoom(int rid);

    void removePlayer(int uid);

    Player& getOnlinePlayer(int uid);

    Player& getOfflinePlayer(int uid);

    void setOnlinePlayer(Player &p);

    void setOfflinePlayer(Player &p);

    void deregisterUserFromRoom(Player &player);

    void deregisterUser(int uid);
};


#endif
