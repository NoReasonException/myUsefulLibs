package com.noReasonException.mylibs.datastruct.Implementations;

import java.util.ArrayList;
import java.util.Comparator;

public class BinarySearchLinkedListSymbolTable <Key extends Comparable<Key>,Value>
        extends OrderedLinkedList<Key,Value>  {
    protected Value binarySearch(Key key,int low,int high,int mid){
        high-=1;
        ArrayList<Node<Key,Value>> data=getAsCollection();
        data.sort((n1,n2)->{return n1.key.compareTo(n2.key);});
        Node nod=data.get(mid);
        while(low<=high){
            System.out.println(mid+">"+data.get(mid).key);
            if(data.get(mid).key.compareTo(key)<0){
                System.out.println("high");
                high=mid-1;
            }
            else if(data.get(mid).key.compareTo(key)>0){
                System.out.println("low");

                low=mid+1;
            }
            else return data.get(mid).value;
            mid=((low+high)/2);
        }
        return null;
    }
    @Override
    public Value get(Key key) {
        return binarySearch(key,0,size,size/2);
    }
}
