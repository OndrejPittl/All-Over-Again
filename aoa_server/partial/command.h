#ifndef COMMAND_H
#define COMMAND_H

#include <string>


enum CommandType {
	CMD_EXAMPLE = 0,
	CMD_EXAMPLE2
};


class Command {
	private:
		int id;
		std::string params;

	public:
		Command(int id);
		Command(int id, std::string params);
		int getID();
		void setID(int id);
		std::string getParams();
		void setParams(std::string params);
};




#endif