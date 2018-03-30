package com.noReasonException.mylibs.datastruct.Implementations;

public class AbstractKeyValueNode<Key extends Comparable<Key>,Value> {
    Key key;
    Value value;

    public AbstractKeyValueNode(Key key, Value value) {
        this.key = key;
        this.value = value;
    }
}
