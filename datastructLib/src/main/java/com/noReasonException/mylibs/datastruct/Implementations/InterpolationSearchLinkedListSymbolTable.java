package com.noReasonException.mylibs.datastruct.Implementations;

public class InterpolationSearchLinkedListSymbolTable<Value>
        extends BinarySearchLinkedListSymbolTable <Integer,Value>{
    @Override
    public Value get(Integer key) {
        return binarySearch(key,0,size,((key-min())/(max()-min())));
    }
}
