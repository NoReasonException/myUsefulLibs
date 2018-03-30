package com.noReasonException.mylibs.datastruct.Implementations;

import com.noReasonException.mylibs.datastruct.AbstractSymbolTable;

import java.util.*;

public class BSTSymbolTable<Key extends Comparable<Key>,Value> extends AbstractSymbolTable<Key,Value>{
    BSTNode<Key,Value> root;




    public BSTSymbolTable() {
        super();
    }

    @Override
    public Iterable<Key> keys(Key lo, Key hi) {
        ArrayList<Key> retval=bfs(root);

        return retval.stream().filter(e->{return lo.compareTo(e)>-1 && hi.compareTo(e)<1;})::iterator;
    }

    @Override
    public Value get(Key key) {
        BSTNode<Key,Value>retval=getNode(root,key,0);
        return retval.key.compareTo(key)==0?retval.value:null;
    }
    private BSTNode<Key,Value> getNode(BSTNode<Key,Value>root,Key key,int flags){
        BSTNode<Key,Value> retval;
        if(root!=null){
            if(root.key.compareTo(key)==0){
                return root;
            }
            else if(key.compareTo(root.key)==-1){

                if(root.left==null){
                    retval= root;
                    if(flags==1)
                        root.height+=1;
                }
                else retval=getNode(root.left,key,flags);

            }
            else {
                if(root.right==null){
                    retval= root;
                    if(flags==1)
                        root.height+=1;
                }
                else retval=getNode(root.right,key,flags);

            }
            if(flags==1){
                updateHeight(root);
            }
            return retval;

        }

        return null;
    }
    private void updateHeight(BSTNode<Key,Value>node){
        node.height=(node.right!=null?node.right.height:0) + (node.left!=null?node.left.height:0)+1;
    }
    @Override
    public boolean contains(Key key) {
        BSTNode<Key,Value> retval=getNode(root,key,0);
        if(retval!=null){
            return retval.key.compareTo(key)==0;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return root!=null;
    }

    @Override
    public void put(Key key, Value val) {

        BSTNode<Key,Value> nod=getNode(root,key,1);
        BSTNode<Key,Value> newn=new BSTNode<>(key,val);
        if(root==null){
            root=new BSTNode<Key,Value>(key,val);
            return;
        }
        else if(nod.key.compareTo(key)==0)nod.value=val;
        else{
            if(key.compareTo(nod.key)==-1){
                nod.left=newn;
            }
            else {
                nod.right=newn;
            }
        }
    }
    @Override
    public void delete(Key key) {

    }

    @Override
    public Key max() {
        return max(root);
    }
    private Key max(BSTNode<Key,Value> root){
        BSTNode<Key,Value> v = root;
        while(v.right!=null)v=v.right;
        return v.key;
    }
    @Override
    public Key min() {
        return min(root);
    }

    private Key min(BSTNode<Key,Value> root){
        BSTNode<Key,Value> v = root;
        while(v.left!=null)v=v.left;
        return v.key;
    }
    @Override
    public Key floor(Key key) {
        return floor(root,key);
    }

    private Key floor(BSTNode<Key,Value> tmp,Key floor) {
        Optional<Key> k;
        ArrayList<Key> retval;
        if(floor.compareTo(tmp.key)==0)return tmp.key;
        if(floor.compareTo(tmp.key)==-1&&tmp.left!=null)return floor(tmp.left,floor);
        if(tmp.right==null)return tmp.key;
        retval=bfs(tmp.right);
        if(retval.stream().anyMatch(e->e.compareTo(floor)==0))return floor;
        if((k=retval.stream().filter(e->e.compareTo(tmp.key)==1&&e.compareTo(floor)==-1).max(Comparator.naturalOrder())).isPresent()){
            return k.get();
        }
        return tmp.key;
    }

    @Override
    public Key ceiling(Key key) {
        return ceiling(root,key);
    }
    private Key ceiling(BSTNode<Key,Value>tmp,Key ceiling){
        Optional<Key> k;
        ArrayList<Key> retval;
        if(ceiling.compareTo(tmp.key)==0)return tmp.key;
        if(ceiling.compareTo(tmp.key)==1&&tmp.right!=null)return ceiling(tmp.right,ceiling);
        if(tmp.left==null)return tmp.key;
        retval=bfs(tmp.left);
        if(retval.stream().anyMatch(e->e.compareTo(ceiling)==0))return ceiling;
        if((k=retval.stream().filter(e->e.compareTo(tmp.key)==-1&&e.compareTo(ceiling)==1).min(Comparator.naturalOrder())).isPresent()){
            return k.get();
        }
        return tmp.key;
    }

    @Override
    public Key select(int i) {
        return null;
    }

    @Override
    public int size() {
        return root.height;
    }

    @Override
    public int rank(Key key) {
        return 0;
    }
    public ArrayList<Key> bfs(BSTNode<Key,Value> b){
        ArrayList<Key>retval=new ArrayList<>();
        Queue<BSTNode<Key,Value>> queue=new LinkedList<>();
        queue.add(b);
        BSTNode<Key,Value> val;
        while(!queue.isEmpty()){
            val=queue.poll();
            retval.add(val.key);
            if(val.left!=null)queue.add(val.left);
            if(val.right!=null)queue.add(val.right);

        }
        return retval;
    }

    private class BSTNode<Key extends Comparable<Key>,Value> extends AbstractKeyValueNode<Key,Value>{
        BSTNode<Key,Value> left;
        BSTNode<Key,Value> right;
        int height=0;
        public BSTNode(Key key, Value value) {
            super(key, value);

        }
        @Override
        public String toString(){
            return key+"-"+height+"(l="+(left!=null?left.key:"NONE")+",r="+(right!=null?right.key:"NONE")+")";
        }

    }
}
