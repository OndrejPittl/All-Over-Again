#ifndef INDEXER_H
#define INDEXER_H


class Indexer {
    private:
        int index;
        std::queue<int> freedIndexes;
        bool hasFreedIndex();
        int getFreedIndex();
    public:
        Indexer(int startIndex = 0);
        int take();
        void free(int index);
};


#endif