package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    private class Node {
        private T item;
        private Node prev;
        private Node next;

        Node(T i) {
            prev = null;
            next = null;
            item = i;
        }
    }

    private int size;
    private Node sentinel;

    LinkedListDeque() {
        size = 0;
        sentinel = new Node(null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }

    @Override
    public void addFirst(T item) {
        Node newNode = new Node(item);
        newNode.next = sentinel.next;
        newNode.prev = sentinel;
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        Node newNode = new Node(item);
        newNode.next = sentinel;
        newNode.prev = sentinel.prev;
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node p = sentinel.next;
        while (p != sentinel) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        Node first = sentinel.next;
        sentinel.next = first.next;
        first.next.prev = sentinel;
        size -= 1;
        return first.item;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        Node last = sentinel.prev;
        sentinel.prev = last.prev;
        last.prev.next = sentinel;
        size -= 1;
        return last.item;
    }

    @Override
    public T get(int index) { //必须是迭代
        if (index >= size) {
            return null;
        }
        Node p = sentinel.next;
        while (index > 0) {
            p = p.next;
            index -= 1;
        }
        return p.item;
    }

    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        }
        return getRecursiveHelper(index, sentinel.next);
    }

    private T getRecursiveHelper(int index, Node p) {
        if (index == 0) {
            return p.item;
        }
        return getRecursiveHelper(index - 1, p.next);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        }

        Deque<?> other = (Deque<?>) o;
        if (size != other.size()) {
            return false;
        }
        Node p = sentinel.next;
        int k = 0;
        while (p != sentinel) {
            if (!p.item.equals(other.get(k))) {
                return false;
            }
            p = p.next;
            k++;
        }
        return true;
    }

    private class LinkedListdequeIterator implements Iterator<T> {
        private Node spy;

        LinkedListdequeIterator() {
            spy = sentinel;
        }

        @Override
        public boolean hasNext() {
            return spy.next != sentinel;
        }

        @Override
        public T next() {
            if (hasNext()) {
                spy = spy.next;
                return spy.item;
            }
            return null;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListdequeIterator();
    }
}
