#ifndef PLAYER_H
#define PLAYER_H

#include <string>
#include <map>
#include <vector>


class Player {
    private:

        /**
         * The same value as a sock connection index.
         */
        int ID;

        /**
         * ID of a room/session.
         */
        int roomID;

        /**
         *  A flag whether or not is a user online.
         */
        bool online;

        /**
         * Username of a user.
         */
        std::string username;

        int incorrectMsgCount;


        void init();

        void setStatus(bool online);

    public:
        Player();

        Player(int id);

        Player(int id, std::string username);

        void setID(int id);

        int getID() const;

        void setRoomID(int id);

        int getRoomID() const;

        bool hasRoom();

        void leaveRoom();

        void setUsername(std::string username);

        std::string getUsername() const;

        void setOnline();

        void setOffline();

        bool isOnline() const;

        int getIncorrectMsgCount() const;

        void clearIncorrectMsgCount();

        void registerIncorrectMsgCount();

        bool hasUsername() const;

        void merge(Player *p);
};

typedef std::map<int, Player*> PlayerMap;

typedef std::vector<Player*> PlayerVector;


#endif