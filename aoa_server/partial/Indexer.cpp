#include <string>
#include <queue>

#include "Indexer.h"


Indexer::Indexer(int startIndex) {
    this->index = startIndex;
}

int Indexer::take() {
    return this->hasFreedIndex() ? getFreedIndex() : this->index++;
}

void Indexer::free(int index) {
    this->freedIndexes.push(index);
}

bool Indexer::hasFreedIndex() {
    return !this->freedIndexes.empty();
}

int Indexer::getFreedIndex() {
    int index = this->freedIndexes.front();
    this->freedIndexes.pop();
    return index;
}