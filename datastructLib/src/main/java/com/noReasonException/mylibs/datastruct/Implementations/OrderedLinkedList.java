package com.noReasonException.mylibs.datastruct.Implementations;

public class OrderedLinkedList<Key extends Comparable<Key>,Value> extends LinkedListSymbolTable<Key,Value> {
    @Override
    public void put(Key key, Value val) {
        size+=1;
        Node<Key,Value>last=head;
        Node<Key,Value>newn=new Node<Key,Value>(key,val);
        if(head==null){
            head=lastEdited=newn;
            return;
        }
        else if((lastEdited=getNode(key))!=null)lastEdited.value=val;
        if(key.compareTo(head.key)==-1){
            head=head.addFrontOf(newn);
            return;
        }
        for(Node<Key,Value> nod=head;nod!=null&&nod.next!=null;last=nod=nod.goNext()) {
            if(nod.key.compareTo(key)<1 && nod.next.key.compareTo(key)>-1){
                nod.addMid(newn);
                return;
            }
        }
        last.addMid(newn);

    }
}
