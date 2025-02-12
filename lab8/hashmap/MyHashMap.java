package hashmap;

import java.util.*;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private final int INICIALSIZE = 16;
    private final double INICIALLOADFACTOR = 0.75;
    private int sizeOfBuckets;
    private double loadFactor;
    private int size;

    /**
     * Constructors
     */
    public MyHashMap() {
        sizeOfBuckets = INICIALSIZE;
        loadFactor = INICIALLOADFACTOR;
        size = 0;
        buckets = createTable(sizeOfBuckets);
        for(int i = 0; i < sizeOfBuckets; i++) {
            buckets[i] = createBucket();
        }
    }

    public MyHashMap(int initialSize) {
        sizeOfBuckets = initialSize;
        loadFactor = INICIALLOADFACTOR;
        size = 0;
        buckets = createTable(sizeOfBuckets);
        for(int i = 0; i < sizeOfBuckets; i++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial sizeOfBuckets of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        sizeOfBuckets = initialSize;
        loadFactor = maxLoad;
        size = 0;
        buckets = createTable(sizeOfBuckets);
        for(int i = 0; i < sizeOfBuckets; i++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the sizeOfBuckets of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    /**
     * Removes all of the mappings from this map.
     */
    @Override
    public void clear() {
        for (int i = 0; i < sizeOfBuckets; i++) {
            buckets[i].clear();
        }
        size = 0;
    }

    private int getIndex(int mun) {
        return Math.floorMod(mun, sizeOfBuckets);
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     *
     * @param key
     */
    @Override
    public boolean containsKey(K key) {
        int index = getIndex(key.hashCode());
        for (Node i : buckets[index]) {
            if (i.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     *
     * @param key
     */
    @Override
    public V get(K key) {
        int index = getIndex(key.hashCode());
        for (Node i : buckets[index]) {
            if (i.key.equals(key)) {
                return i.value;
            }
        }
        return null;
    }

    /**
     * Returns the number of key-value mappings in this map.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     *
     * @param key
     * @param value
     */
    @Override
    public void put(K key, V value) {
        if(!this.containsKey(key)){
            size++;
            if ((double) size / sizeOfBuckets > loadFactor) {
                resize();
            }
            int index = getIndex(key.hashCode());
            buckets[index].add(createNode(key, value));
        }
        else{
            int index = getIndex(key.hashCode());
            for (Node i : buckets[index]) {
                if (i.key.equals(key)) {
                    i.value = value;
                }
            }
        }
    }

    /**
     * Returns a Set view of the keys contained in this map.
     */
    @Override
    public Set<K> keySet() {
        Set<K> keysSet = new HashSet<>();
        for (int i = 0; i < sizeOfBuckets; i++) {
            for (Node j : buckets[i]) {
                keysSet.add(j.key);
            }
        }
        return keysSet;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     *
     * @param key
     */
    @Override
    public V remove(K key) {
        int index = getIndex(key.hashCode());
        for (Node i : buckets[index]) {
            if (i.key.equals(key)) {
                V value = i.value;
                buckets[index].remove(i);
                return value;
            }
        }
        return null;
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     *
     * @param key
     * @param value
     */
    @Override
    public V remove(K key, V value) {
        int index = getIndex(key.hashCode());
        for (Node i : buckets[index]) {
            if (i.key.equals(key) && i.value.equals(value)) {
                buckets[index].remove(i);
                return value;
            }
        }
        return null;
    }

    private class iteratorOfMyHashMap implements Iterator<K> {
        private int index;
        private Iterator<Node> iterator;

        public iteratorOfMyHashMap() {
            index = 0;
            iterator = buckets[index].iterator();
        }

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            while (index < sizeOfBuckets) {
                if (iterator.hasNext()) {
                    return true;
                }
                index++;
                iterator = buckets[index].iterator();
            }
            return false;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public K next() {
            while (index < sizeOfBuckets) {
                if (iterator.hasNext()) {
                    return iterator.next().key;
                }
                index++;
                iterator = buckets[index].iterator();
            }
            throw new NoSuchElementException();
        }
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<K> iterator() {
        return null;
    }

    private void resize() {
        sizeOfBuckets = sizeOfBuckets * 2;
        Collection<Node>[] newBuckets = createTable(sizeOfBuckets);
        for(int i = 0; i < sizeOfBuckets; i++) {
            newBuckets[i] = createBucket();
        }
        for (int i = 0; i < sizeOfBuckets / 2; i++) {
            for (Node j : buckets[i]) {
                int index = getIndex(j.key.hashCode());
                newBuckets[index].add(j);
            }
        }
        buckets = newBuckets;
    }
}
