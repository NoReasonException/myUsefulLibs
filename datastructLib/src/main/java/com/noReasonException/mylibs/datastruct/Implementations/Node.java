package com.noReasonException.mylibs.datastruct.Implementations;

public class Node<Key extends Comparable<Key>,Value>{
    Key     key;
    Value   value;

    public Node<Key,Value> setKey(Key key) {
        this.key = key;
        return this;
    }

    public Node<Key,Value> setValue(Value value) {
        this.value = value;
        return this;
    }

    Node<Key,Value>    next;
    Node <Key,Value>   prev;
    public Node(Key key, Value value) {
        this.key = key;
        this.value = value;

    }
    public Node<Key,Value> goNext(){return this.next;}
    public Node<Key,Value> goPrev(){return this.prev;}

    public Node<Key,Value> addFrontOf(Node<Key,Value> node) {

        this.prev=node;
        node.next=this;
        return node;
    }
    public Node<Key,Value> addMid(Node<Key,Value> node) {

        Node<Key,Value> m = this.next;
        this.next=node;
        node.prev=this;
        node.next=m;
        if(m!=null){
            node.next=m;
            m.prev=node;
        }
        return node;
    }
    public boolean deleteMe(){
        if(this.next!=null){
            this.next.prev=this.prev;
        }
        else if(this.prev!=null){
            this.prev.next=this.next;
        }
        else {
            return true;
        }
        return false;
    }
}