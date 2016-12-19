#ifndef STRING_BUILDER_H
#define STRING_BUILDER_H

#include <string>
#include <sstream>


class StringBuilder {
	private:
		std::ostringstream message;

	public:
		StringBuilder();

		template <typename type> void append(type arg){
			this->message << arg;
		}

		void clear();

		std::string getString();
};

#endif