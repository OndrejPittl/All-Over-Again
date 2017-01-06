#ifndef PLAYER_H
#define PLAYER_H

#include <string>


class Player {

    private:
        int id;
        int roomID;
        bool isConnected;
        std::string username;

        void init();

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

        void setIsConnected(bool connected);
        bool getIsConnected() const;

};


#endif