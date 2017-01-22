#ifndef TOOLS_H
#define TOOLS_H

#include <string>
#include <vector>
#include <map>
#include "../game/Player.h"
#include "../game/Room.h"



class Tools {

    private:
        static const std::string VALIDATION_USERNAME_REGEX;

        static StringBuilder *sb;

//        static const std::string LOG_PADDING_LEFT;

    public:
        static void init();

        static bool validate(std::string str, std::string regexp);

        static bool validateUsername(std::string username);

        /**
        *	Checks whether a string given is a number or not.
        */
        static bool isNumber(std::string str);

        /**
        *	Checks whether a string given is a number and in range or not.
        */
        static bool isNumberInRange(std::string str, int lowerLimit, int upperLimit);

        /**
        *	Prints a line of a text.
        */
        static void println(std::string str);

        static void printTrueFalse();

        static void removeChar(std::string *str, char c);

        static long checksum(std::string str, int modulo = -1);

        static void printVector(std::vector<std::string> vec);

        static void printPlayerVector(PlayerVector vec);

        static void  printPlayers(PlayerMap &m);

        static void printRooms(RoomMap &m);

        static bool checkIfExistsInPlayerVector(PlayerVector &v, int uid);

        static void printUsernames (std::map <std::string, int> usernames);

        static bool keyExistsInPlayerMap (PlayerMap &players, int uid);



        template<typename T> static std::string buildColumn(T str, int width, char fillChar = ' ') {
            int len = (int) str.length();

            if(width < len) {
                std::string out = str.substr(0, width - 2);
                out.append("..");
                return out;
            }


            int diff = width - len,
                    pad1 = diff/2,
                    pad2 = diff - pad1;

            return std::string(pad1, fillChar) + str + std::string(pad2, fillChar);
        };

        static void printOnlineOfflineUsers(PlayerMap online, PlayerMap offline);

    static void buildPlayers(PlayerMap &m);

    static void buildRooms(RoomMap &m);

    static void buildUsernames(std::map<std::string, int> usernames);
};






#endif