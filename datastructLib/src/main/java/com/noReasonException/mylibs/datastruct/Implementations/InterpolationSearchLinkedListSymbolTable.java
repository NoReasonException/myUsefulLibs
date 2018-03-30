package com.noReasonException.mylibs.datastruct.Implementations;

public class InterpolationSearchLinkedListSymbolTable<Value>
        extends BinarySearchLinkedListSymbolTable <Integer,Value>{
    /***
     * This overriden version of get , uses the interpolation formula to accomplish better results !
     * Exersise 3.1.24 (p 341)
     */
    @Override
    public Value get(Integer key) {
        return binarySearch(key,0,size,((key-min())/(max()-min())));
    }
}
