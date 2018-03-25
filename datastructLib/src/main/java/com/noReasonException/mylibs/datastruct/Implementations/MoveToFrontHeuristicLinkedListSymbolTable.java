package com.noReasonException.mylibs.datastruct.Implementations;

import java.util.Optional;

public class MoveToFrontHeuristicLinkedListSymbolTable<Key extends Comparable<Key>,Value>
        extends LinkedListSymbolTable<Key,Value> {

    @Override
    public Value get(Key key) {
        Value v=super.get(key);
        if(lastEdited!=null){
            delete(lastEdited.key);
            put(lastEdited.key,lastEdited.value);
        }

        return v;
    }

}
