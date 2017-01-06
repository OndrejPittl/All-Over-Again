#ifndef APP_H
#define APP_H


#include <string>
#include <vector>
#include <map>

#include "../game/Player.h"
#include "../game/Room.h"
#include "../partial/StringBuilder.h"


class ConnectionManager;
class CommunicationManager;


class Application {
    private:

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
        std::vector<Room*> rooms;



        StringBuilder *log;



        void init();

        bool checkUsernameAvailability(std::string username);

    public:

        static const std::string USERNAME_VALIDATION_REGEX;

        Application();

        Application(ConnectionManager *conn, CommunicationManager *comm);

        bool validateUsername(std::string basic_string);

        bool registerUser(int uid, std::string username);

        void deregisterUser(int uid);

        //void setDependencies(ConnectionManager *conn, CommunicationManager *comm);
};


#endif
