package com.noReasonException.mylibs.datastruct.Implementations;

import java.util.ArrayList;

public class BinarySearchLinkedListSymbolTable <Key extends Comparable<Key>,Value>
        extends LinkedListSymbolTable<Key,Value>  {
    @Override
    public Value get(Key key) {
        ArrayList<Node<Key,Value>> data=getAsCollection();
        Node nod=data.get(size/2);
        int low=0,high=size,mid=(low+high)/2;
        while(low<high){
            if(data.get(mid).key.compareTo(key)<0)high=mid;
            else if(data.get(mid).key.compareTo(key)>0)low=mid;
            else return data.get(mid).value;
            mid=(low+high/2);
        }
        return null;

    }
}
