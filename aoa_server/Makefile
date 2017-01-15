CC=g++
FILES_a = $(wildcard *.cpp) $(wildcard */*.cpp)
FLAGS = -lpthread -std=c++11
# FLAGS = -lpthread -v
SRV = aoa_server


all:	clean build run

clean:
	@ clear
	@ if [ -a $(SRV) ]; then rm $(SRV); echo 'Cleaning.....DONE!'; fi;

build: srv.cpp
	$(CC) -o $(SRV) $(FILES_a) $(FLAGS)
	@ echo 'Building.....DONE!';

run:
	./$(SRV) -p 23456
