package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private class BSTNode<K extends Comparable<K>, V> {
        K key;
        V val;
        BSTNode<K, V> left;
        BSTNode<K, V> right;

        BSTNode(K k, V v) {
            key = k;
            val = v;
            left = null;
            right = null;
        }
    }

    private BSTNode<K, V> root;
    private int size;

    public BSTMap () {
        root = null;
        size = 0;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return helpContains(root, key);
    }

    private boolean helpContains(BSTNode n, K key) {
        if (n == null) {
            return false;
        }
        if (n.key.equals(key)) {
            return true;
        } else if (n.key.compareTo(key) > 0) {
            return helpContains(n.right, key);
        } else {
            return helpContains(n.left, key);
        }
    }

    @Override
    public V get(K key) {
        return helpGet(root, key);
    }

    private V helpGet(BSTNode n, K key) {
        if (n == null) {
            return null;
        }
        if (n.key.equals(key)) {
            return (V) n.val;
        } else if (n.key.compareTo(key) > 0) {
            return (V) helpGet(n.right, key);
        } else {
            return (V) helpGet(n.left, key);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        root = helpPut(root, key, value);
    }

    private BSTNode helpPut(BSTNode n, Object key, Object value) {
        if (n == null) {
            size ++;
            return new BSTNode((K) key, (V) value);
        }
        if (n.key.equals(key)) {
            n.val = value;
        } else if (n.key.compareTo(key) > 0) {
            n.right = helpPut(n.right, key, value);
        } else {
            n.left = helpPut(n.left, key, value);
        }
        return n;
    }

    @Override
    public Set keySet() {
        throw new UnsupportedOperationException();
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
    public Iterator iterator() {
        throw new UnsupportedOperationException();
    }

    public void printInOrder() {
        if (root == null) {
            System.out.println("(There is an empty tree!)");
        }
        helpPrintInOrder(root);
        System.out.println();
    }

    private void helpPrintInOrder(BSTNode n) {
        if (n == null) {
            return ;
        }
        System.out.print(n.val + " ");
        helpPrintInOrder(n.left);
        helpPrintInOrder(n.right);
    }
}
