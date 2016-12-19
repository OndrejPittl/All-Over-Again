CC=g++
FILES = *.cpp
FILES_b = core/*.cpp gameplay/*.cpp network/*.cpp partial/*.cpp
FILES_a = */*.cpp
FILES_a = $(wildcard *.cpp) $(wildcard */*.cpp)
FLAGS = -lpthread -std=c++11
# FLAGS = -lpthread -v
SRV = aoa_server


all:	clean build run

clean:
	@ clear
	@ clear
	@ if [ -a $(SRV) ]; then rm $(SRV); echo 'Cleaning.....DONE!'; fi;

build: srv.cpp
	$(CC) -o $(SRV) $(FILES_a) $(FLAGS)
	@ echo 'Building.....DONE!';

run:
	./$(SRV) 23456
