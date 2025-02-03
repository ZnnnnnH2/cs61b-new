package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int firstIndex;
    private int lastIndex;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        firstIndex = 0;
        lastIndex = 0;
    }

    private int getNextIndex(int index) {
        return (index + 1) % items.length;
    }

    private int getPrevIndex(int index) {
        return (index - 1 + items.length) % items.length;
    }

    private int getTrueIndex(int index) {
        return (firstIndex + index) % items.length;
    }

    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            newItems[i] = items[getTrueIndex(i)];
        }
        items = newItems;
        firstIndex = 0;
        lastIndex = size;
    }

    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        firstIndex = getPrevIndex(firstIndex);
        items[firstIndex] = item;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[lastIndex] = item;
        lastIndex = getNextIndex(lastIndex);
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(items[getTrueIndex(i)] + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        if (size < items.length / 4 && items.length > 8) {
            resize(items.length / 4);
        }
        T item = items[firstIndex];
        items[firstIndex] = null;
        firstIndex = getNextIndex(firstIndex);
        size -= 1;
        return item;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        if (size < items.length / 4 && items.length > 8) {
            resize(items.length / 4);
        }
        lastIndex = getPrevIndex(lastIndex);
        T item = items[lastIndex];
        items[lastIndex] = null;
        size -= 1;
        return item;
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
        int pIndex = firstIndex;
        int k = 0;
        while (pIndex != lastIndex) {
            if (!items[pIndex].equals(other.get(k))) {
                return false;
            }
            pIndex = getNextIndex(pIndex);
            k++;
        }
        return true;
    }

    @Override
    public T get(int index) {
        return items[getTrueIndex(index)];
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int index;

        public ArrayDequeIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public T next() {
            T item = items[getTrueIndex(index)];
            index += 1;
            return item;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }
}
