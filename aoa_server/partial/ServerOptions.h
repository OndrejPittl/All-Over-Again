#ifndef SERVER_OPTIONS_H
#define SERVER_OPTIONS_H

#include <string>
#include <map>
#include "StringBuilder.h"


class ServerOptions {
    private:

        /**
         *
         */
        static const int PORT_NUM_LOWER_LIMIT;

        /**
         *
         */
        static const int PORT_NUM_UPPER_LIMIT;

        /**
         *
         */
        typedef std::pair<std::string, std::string> Option;

        /**
         * Port possible flags.
         */
        static const ServerOptions::Option OPT_PORT_FLAGS;

        /**
         * Quiet mode possible flags.
         */
        static const ServerOptions::Option OPT_QUIET_FLAGS;

        /**
         * Help possible flags.
         */
        static const ServerOptions::Option OPT_HELP_FLAGS;

        /**
         *
         */
        std::map<std::string, uint> options = {{OPT_PORT, 0}, {OPT_QUIET, 0}, {OPT_HELP, 0}};

        /**
         *  Output logs.
         */
        StringBuilder *sb;

        /**
         *
         */
        int argc;

        /**
         *
         */
        char **argv;

        /**
         * aoa_server
         */
        std::string appName;


        bool isOption(const std::string &arg, const ServerOptions::Option &opt);


    public:

        static const std::string OPT_PORT;

        static const std::string OPT_QUIET;

        static const std::string OPT_HELP;


        ServerOptions(int argc, char **argv);

        void init();

        bool has(const std::string &opt) const;

        void parse();

        int validatePort(std::string port);

        void printHelp();

        void print();

        const std::string &getAppName() const;

        uint get(const std::string &flag);
};


#endif
