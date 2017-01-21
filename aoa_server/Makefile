CC=g++
FILES = $(wildcard *.cpp) $(wildcard */*.cpp)
FLAGS = -lpthread -std=c++11
SRV = aoa_server


all:	clean build run

clean:
	@ clear
	@ if [ -f $(SRV) ]; then rm $(SRV); echo 'Cleaning.....DONE!'; fi;

build: srv.cpp
	$(CC) -o $(SRV) $(FILES) $(FLAGS)
	@ echo 'Building.....DONE!';

run:
	./$(SRV) -p 23456
