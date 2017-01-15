#ifndef MESSAGE_SERIALIZER_H
#define MESSAGE_SERIALIZER_H

#include <string>
#include <map>

#include "../game/Room.h"
#include "../partial/StringBuilder.h"
#include "Message.h"


class MessageSerializer {

    private:
        StringBuilder *sb;
        StringBuilder *log;
        void serializeRoomAndJoin(Room *r);

    public:
        MessageSerializer();
        void init();
        std::string serializeRooms(RoomMap &rooms);
        std::string serializeRoom(Room *r);

};


#endif