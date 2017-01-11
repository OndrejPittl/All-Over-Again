#ifndef PLAYER_H
#define PLAYER_H

#include <string>


class Player {

    private:
        int id;
        int roomID;
        bool online;
        std::string username;

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

        void setUsername(std::string username);
        std::string getUsername() const;

        void setOnline();
        void setOffline();
        bool isOnline() const;

};


#endif