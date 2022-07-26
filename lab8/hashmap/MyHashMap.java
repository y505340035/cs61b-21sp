package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
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
    private int sizeM;
    private int sizeN;
    private double loadFactor;
    private Set<K> keySet;
    private final int defaultInitialSize = 16;
    private final double defaultLoadFactor = 0.75;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(defaultInitialSize);
        sizeN = 0;
        sizeM = defaultInitialSize;
        for (int i = 0; i < sizeM; i++) {
            buckets[i] = createBucket();
        }
        loadFactor = defaultLoadFactor;
        keySet = new HashSet<>();
    }

    public MyHashMap(int initialSize) {
        buckets = createTable(initialSize);
        sizeN = 0;
        sizeM = initialSize;
        for (int i = 0; i < sizeM; i++) {
            buckets[i] = createBucket();
        }
        loadFactor = defaultLoadFactor;
        keySet = new HashSet<>();
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        sizeN = 0;
        sizeM = initialSize;
        for (int i = 0; i < sizeM; i++) {
            buckets[i] = createBucket();
        }
        loadFactor = maxLoad;
        keySet = new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear() {
        buckets = createTable(sizeM);
        for (int i = 0; i < sizeM; i++) {
            buckets[i] = createBucket();
        }
        keySet = new HashSet<>();
        sizeN = 0;
    }

    @Override
    public boolean containsKey(K key) {
        int hc = key.hashCode();
        Collection<Node> targetBucket = buckets[Math.floorMod(hc, sizeM)];
        for (Node n: targetBucket) {
            if (key.equals(n.key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int hc = key.hashCode();
        Collection<Node> targetBucket = buckets[Math.floorMod(hc, sizeM)];
        for (Node n: targetBucket) {
            if (key.equals(n.key)) {
                return n.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return sizeN;
    }

    @Override
    public void put(K key, V value) {
        keySet.add(key);
        sizeN += helpPut(key, value);

        if ((0.0 + sizeN) / (sizeM) > loadFactor) {
            MyHashMap<K, V> newMHM = new MyHashMap<>(sizeM * 2, loadFactor);
            for (K ikey: this) {
                V iv = get(ikey);
                newMHM.helpPut(ikey, iv);
            }
            buckets = newMHM.buckets;
            sizeM *= 2;
        }
    }

    private int helpPut(K key, V value) {
        int hc = key.hashCode();
        Collection<Node> targetBucket = buckets[Math.floorMod(hc, sizeM)];
        for (Node n: targetBucket) {
            if (key.equals(n.key)) {
                n.value = value;
                return 0;
            }
        }
        targetBucket.add(new Node(key, value));
        return 1;
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return keySet.iterator();
    }

}
