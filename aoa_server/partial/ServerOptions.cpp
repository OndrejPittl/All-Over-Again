#include "ServerOptions.h"
#include "tools.h"
#include "../core/Logger.h"

#include <iostream>



const int ServerOptions::PORT_NUM_LOWER_LIMIT = 1;

const int ServerOptions::PORT_NUM_UPPER_LIMIT = 65535;

const std::string ServerOptions::OPT_PORT = "p";

const std::string ServerOptions::OPT_QUIET = "q";

const std::string ServerOptions::OPT_HELP = "h";

const ServerOptions::Option ServerOptions::OPT_PORT_FLAGS ("-p", "--port");

const ServerOptions::Option ServerOptions::OPT_QUIET_FLAGS ("-q", "--quiet");

const ServerOptions::Option ServerOptions::OPT_HELP_FLAGS ("-h", "--help");



ServerOptions::ServerOptions(int argc, char **argv) {
    this->argc = argc;
    this->argv = argv;
    this->init();
    this->parse();
    //this->print();
}

void ServerOptions::init() {
    this->sb = new StringBuilder();
}

void ServerOptions::parse() {
    uint i = 1;

    this->appName = std::string(argv[0]);

    while (i < argc) {
        bool isOpt = false;
        const std::string opt = std::string(argv[i]);

        if(this->isOption(opt, OPT_PORT_FLAGS)) {
            int p = this->validatePort(argv[++i]);
            if(p > 0) this->options[OPT_PORT] = (uint) p;
            isOpt = true;
        } else if(this->isOption(opt, OPT_QUIET_FLAGS)) {
            this->options[OPT_QUIET] = (uint) true;
            isOpt = true;
        } else if(this->isOption(opt, OPT_HELP_FLAGS)) {
            this->options[OPT_HELP] = (uint) true;
            isOpt = true;
        }

        if(!isOpt) {
            int rs = Logger::printErr(ERR_INVALID_ARGS);
            this->printHelp();
            exit(rs);
        }

        i++;
    }
}

bool ServerOptions::has(const std::string &opt) const {
    return this->options.find(opt)->second > 0;
}

bool ServerOptions::isOption(const std::string &arg, const Option &opt) {
    //std::cout << "checking: " << arg << " (" << opt.first << ", " << opt.second << ")" << std::endl;
    return arg.find(opt.first) != std::string::npos || arg.find(opt.second) != std::string::npos;
}

int ServerOptions::validatePort(std::string port) {
    if(isNumberInRange(port, ServerOptions::PORT_NUM_LOWER_LIMIT, ServerOptions::PORT_NUM_UPPER_LIMIT)) {
        return std::stoi(port);
    }

    Logger::error("Invalid port inserted. DEFAULT is used.");
    return -1;
}

void ServerOptions::print() {
    this->sb->clear();
    this->sb->append("Printing input params:\n");
    for(auto it = this->options.cbegin(); it != this->options.cend(); ++it) {
        this->sb->append(it->first);
        this->sb->append(": ");
        this->sb->append(it->second);
        this->sb->append("\n");
    }
    Logger::info(this->sb->getString());
}

void ServerOptions::printHelp() {
    this->sb->clear();
    this->sb->append("usage: ./aoa_server [options]\n\nOptions:\n");

    // help
    this->sb->append(OPT_HELP_FLAGS.first);
    this->sb->append(", ");
    this->sb->append(OPT_HELP_FLAGS.second);
    this->sb->append("                     Displays help information.\n");

    //port
    this->sb->append(OPT_PORT_FLAGS.first);
    this->sb->append(" <port>, ");
    this->sb->append(OPT_PORT_FLAGS.second);
    this->sb->append(" <port>       Specifies the server to run at <port> port\n");
    this->sb->append("                               in a range ");
    this->sb->append(PORT_NUM_LOWER_LIMIT);
    this->sb->append(" - ");
    this->sb->append(PORT_NUM_UPPER_LIMIT);
    this->sb->append(".\n");

    // quiet
    this->sb->append(OPT_QUIET_FLAGS.first);
    this->sb->append(", ");
    this->sb->append(OPT_QUIET_FLAGS.second);
    this->sb->append("                    Specifies the server to run at quiet mode.\n");

    Logger::print(this->sb->getString());
}

const std::string &ServerOptions::getAppName() const {
    return this->appName;
}

uint ServerOptions::get(const std::string &flag) {
    return this->options[flag];
}
