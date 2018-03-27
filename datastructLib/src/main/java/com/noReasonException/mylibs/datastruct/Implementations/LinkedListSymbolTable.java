package com.noReasonException.mylibs.datastruct.Implementations;

import com.noReasonException.mylibs.datastruct.AbstractSymbolTable;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;

public class LinkedListSymbolTable <Key extends Comparable<Key>,Value> extends AbstractSymbolTable<Key,Value> {
    Node<Key,Value> head;
    Node<Key,Value> lastEdited;
    int  size=0;


    public Node<Key, Value> getHead() {
        return head;
    }
    /***
     * Utillity method , gets the key , return the node of the list!
     * @param k the key to search
     * @return the node found , null for nothing
     */
    @Nullable
    protected Node<Key,Value> getNode(Key k){
        for(Node<Key,Value> nod=head;nod!=null;nod=nod.goNext())
            if(nod.key.compareTo(k)==0)return nod;
        return null;
    }

    /***
     * Utillity method , we convert the Linked list to Standard ArrayList
     * @return a new ArrayList of nodes
     */
    protected ArrayList<Node<Key,Value>> getAsCollection(){
        ArrayList<Node<Key,Value>> retval=new ArrayList<>();
        for(Node<Key,Value> nod=head;nod!=null;nod=nod.goNext()){
            retval.add(nod);
        }
        return retval;

    }

    /***
     * gets a stream of nodes between the range given
     * @param lo the low key
     * @param hi the last key
     * @return the stream with the nodes between
     */
    protected Stream<Node<Key ,Value>> rangeOf(Key lo, Key hi){
        return getAsCollection().stream().filter(node -> {return node.key.compareTo(lo)>-1&&node.key.compareTo(hi)<1;});

    }
    @Override
    public Value get (Key key){
        Node<Key,Value> retval;
        if((lastEdited=retval=getNode(key))==null)return null;
        return retval.value;


    }
    @Override
    public boolean contains(Key key){
        return this.getNode(key)!=null;
    }

    @Override
    public void put (Key key, Value val){
        size+=1;

        if(head==null){
            head=lastEdited=new Node<Key,Value>(key,val);
            return;
        }
        else if((lastEdited=getNode(key))!=null)lastEdited.value=val;
        else head=head.addFrontOf(lastEdited=new Node<Key,Value>(key,val));

    }
    @Override
    public void delete (Key key){
        Node<Key,Value> retval=getNode(key);
        if(retval!=null){
            if((lastEdited=retval).deleteMe())head=null;

        }
    }
    @Override
    public Key max (){ return getAsCollection().stream().map(n->{return n.key;}).max(Comparator.naturalOrder()).get(); }
    @Override
    public Key min (){return getAsCollection().stream().map(n->{return n.key;}).min(Comparator.naturalOrder()).get(); }
    @Override
    public Key floor   (Key key){throw new RuntimeException();}
    @Override
    public Key select  (int i){return getAsCollection().get(i).key;}
    @Override
    public Key ceiling (Key key){throw new RuntimeException();}
    @Override
    public int size (){return this.size;}
    @Override
    public int rank (Key key){return getAsCollection().indexOf(getNode(key));}
    @Override
    public Iterable<Key>  keys    (Key lo,Key hi){
        return rangeOf(lo,hi).map((a)->{return a.key;})::iterator;
    }



}



