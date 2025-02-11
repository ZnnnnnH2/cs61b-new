package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private class BSTNode {
        public K key;
        public V value;
        public BSTNode leftChild, rightChild;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.leftChild = null;
            this.rightChild = null;
        }
    }

    private int size;
    private BSTNode root;
    private K biggest, smallest;

    public BSTMap() {
        size = 0;
        root = null;
        biggest = null;
        smallest = null;
    }

    @Override
    public void clear() {
        size = 0;
        root = null;
        biggest = null;
        smallest = null;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKeyHelper(key, root);
    }

    private boolean containsKeyHelper(K key, BSTNode head) {
        if (head == null) {
            return false;
        }
        if (key.compareTo(head.key) == 0) {
            return true;
        }
        if (key.compareTo(head.key) < 0) {
            return containsKeyHelper(key, head.leftChild);
        }
        return containsKeyHelper(key, head.rightChild);
    }

    @Override
    public V get(K key) {
        return getHelper(key, root);
    }

    private V getHelper(K key, BSTNode head) {
        if (head == null) {
            return null;
        }
        if (key.compareTo(head.key) == 0) {
            return head.value;
        }
        if (key.compareTo(head.key) < 0) {
            return getHelper(key, head.leftChild);
        }
        return getHelper(key, head.rightChild);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        size++;
        root = putHelper(key, value, root);
        if (biggest == null || key.compareTo(biggest) > 0) {
            biggest = key;
        }
        if (smallest == null || key.compareTo(smallest) < 0) {
            smallest = key;
        }
    }

    private BSTNode putHelper(K key, V value, BSTNode head) {
        if (head == null) {
            return new BSTNode(key, value);
        }
        if (key.compareTo(head.key) < 0) {
            head.leftChild = putHelper(key, value, head.leftChild);
        }
        if (key.compareTo(head.key) > 0) {
            head.rightChild = putHelper(key, value, head.rightChild);
        }
        return head;
    }

    @Override
    public Set<K> keySet() {
//        throw new UnsupportedOperationException();
        Set<K> keySet = new HashSet<>();
        getKeysHelper(root, keySet);
        return keySet;
    }

    private void getKeysHelper(BSTNode head, Set<K> keySet) {
        if (head == null) {
            return;
        }
        keySet.add(head.key);
        getKeysHelper(head.leftChild, keySet);
        getKeysHelper(head.rightChild, keySet);
    }

    @Override
    public V remove(K key) {
        if (!this.containsKey(key)) {
            return null;
        }
        size--;
        V ret = get(key);
        root = removeHelper(key, root);
        return ret;
    }

    private BSTNode removeHelper(K key, BSTNode head) {
        if (key.equals(head.key)) {
            if (head.leftChild == null && head.rightChild == null) {
                return null;
            }
            if (head.leftChild == null) {
                return head.rightChild;
            }
            if (head.rightChild == null) {
                return head.leftChild;
            }
            K newKey = getNext(head.rightChild, head.key);
            head.key = newKey;
            head.rightChild = removeHelper(newKey, head.rightChild);
            return head;
        }
        if (key.compareTo(head.key) < 0) {
            head.leftChild = removeHelper(key, head.leftChild);
        } else {
            head.rightChild = removeHelper(key, head.rightChild);
        }
        return head;
    }

    @Override
    public V remove(K key, V value) {
        if (!this.containsKey(key) || !this.get(key).equals(value)) {
            return null;
        }
        size--;
        return this.remove(key);
    }

    private K getNext(BSTNode head, K key) {
        if (head == null) {
            return null;
        }
        if (key.compareTo(head.key) < 0) {
            K next = getNext(head.leftChild, key);
            if (next == null) {
                return head.key;
            }
            return next;
        }
        return getNext(head.rightChild, key);
    }

    private class BSTMapIterator implements Iterator<K> {
        private K current;

        public BSTMapIterator() {
            current = smallest;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public K next() {
            K ret = current;
            current = getNext(root, current);
            return ret;
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTMapIterator();
    }

    public void printInOrder() {
        printInOrderHelper(root);
    }

    private void printInOrderHelper(BSTNode head) {
        if (head == null) {
            return;
        }
        printInOrderHelper(head.leftChild);
        System.out.println(head.key + " " + head.value);
        printInOrderHelper(head.rightChild);
    }
}
