package deque;

public class LinkedListDeque<T> {
    private class Node {
        public T item;
        public Node prev;
        public Node next;

        public Node(T i) {
            prev = null;
            next = null;
            item = i;
        }
    }

    private int size;
    private Node sentinel;

    public LinkedListDeque() {
        size = 0;
        sentinel = new Node(null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }

    public void addFirst(T item) {
        Node newNode = new Node(item);
        newNode.next = sentinel.next;
        newNode.prev = sentinel;
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        size += 1;
    }

    public void addLast(T item) {
        Node newNode = new Node(item);
        newNode.next = sentinel;
        newNode.prev = sentinel.prev;
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        size += 1;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node p = sentinel.next;
        while (p != sentinel) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.println();
    }

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

    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }

        LinkedListDeque<?> other = (LinkedListDeque<?>) o;
        if (size != other.size) {
            return false;
        }
        Node p = sentinel.next;
        while(p != sentinel) {
            if (!p.item.equals(other.removeFirst())) {
                return false;
            }
            p = p.next;
        }
        return true;
    }
}
