
#include <cstring>
#include <string>

#include "GameStatus.h"


std::string translateGameStatus(GameStatus s) {
    switch (s) {
        case GameStatus::CONNECTING: return std::string("connecting");
        case GameStatus::READY: return std::string("ready");
        case GameStatus::STARTED: return std::string("started");
        case GameStatus::PLAYING: return std::string("playing");
        case GameStatus::WAITING: return std::string("waiting");
        case GameStatus::FINISHED: return std::string("finished");
        case GameStatus::FINISHED_REPLAY: return std::string("fin_repl");
        case GameStatus::FINISHED_END: return std::string("fin_end");
        case GameStatus::ENDED: return std::string("ended");
    }
}
