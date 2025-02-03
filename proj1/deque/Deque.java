package deque;

import java.util.Iterator;

public interface Deque<T> {
    void addFirst(T item);

    void addLast(T item);

    //    public boolean isEmpty();
    int size();

    void printDeque();

    T removeFirst();

    T removeLast();

    T get(int index);

    boolean equals(Object o);

    Iterator<T> iterator();

    default boolean isEmpty() {
        return size() == 0;
    }
}
