package com.noReasonException.mylibs.datastruct;

import com.sun.istack.internal.Nullable;

abstract public class AbstractSymbolTable<Key extends Comparable<Key>,Value> {
    /***
     * com.noReasonException.mylibs.datastruct.AbstractSymbolTable , as given in book 'Algorithms'(978-0321573513)
     */
    public AbstractSymbolTable() {
    }

    /***
     * Get the keys in range lo-hi
     * @param lo the first key
     * @param hi the last key
     * @return all the nodes inside this range
     */
    @Nullable
    public abstract Iterable<Key> keys(Key lo, Key hi);

    /***
     * return all keys
     */
    @Nullable
    public Iterable<Key> keys() {
        return keys(min(), max());
    }

    /***
     * gets a value by key given
     * @param key the key to search
     * @return the value , may return null
     */
    @Nullable
    public abstract Value get(Key key);

    /***
     * Searches if a key exist in structure
     * @param key the key to search
     * @return true if exist , false otherwise
     */
    public abstract boolean contains(Key key);

    /***
     * @return true if the data structure is empty
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /***
     * Inserts or replaces a new value , specified by key
     * @param key the key to search
     * @param val the new value to insert in
     */
    public abstract void put(Key key, Value val);

    /***
     * Delete the value , specified by the key
     * @param key the key to delete , in case of contains(key)==false , nothing will happen
     */
    public abstract void delete(Key key);

    /***
     * @return the maximum key
     */
    public abstract Key max();

    /***
     *
     * @return the minimum key
     */
    public abstract Key min();

    /***
     * @param key the key to compare
     * @return the biggest key possible , with returnKey.compareTo(Key)==-1
     */
    public abstract Key floor(Key key);

    /***
     * @param key the key to compare
     * @return the lowest key possible , with returnKey.compareTo(key)==1
     */
    public abstract Key ceiling(Key key);

    /***
     * selects the key with natural ordering equal with @param i
     * @param i the key ordering..
     * @return
     */
    public abstract Key select(int i);

    /***
     *
     * @return the number of elements inside data structure
     */
    public abstract int size();

    /***
     *
     * @param key the key to search
     * @return the natural ordering of key given
     */
    public abstract int rank(Key key);

    /***
     * @param lo the start key
     * @param hi the final key
     * @return the number of elements inside the lo-hi range

     */
    public int size(Key lo, Key hi) {
        return contains(hi) ? rank(hi) - rank(lo) + 1 : rank(hi) - rank(lo);
    }

    /***
     * Delete the key-value pair with the lowest key based on natural ordering
     */
    public void delMin() {
        delete(min());
    }

    /***
     * Delete the key-value pair with the highest key based on natural ordering
     */
    public void delMax() {
        delete(max());
    }

}
