package deque;

public class ArrayDeque<T> implements Deque<T> {
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
    private int getNextIndex(int index){
        return (index + 1) % items.length;
    }
    private int getPrevIndex(int index){
        return (index - 1 + items.length) % items.length;
    }
    private int getTrueIndex(int index){
        return (firstIndex + index) % items.length;
    }
    private void resize(int capacity){
        T[] newItems = (T[]) new Object[capacity];
        for(int i = 0; i < size; i++){
            newItems[i] = items[getTrueIndex(i)];
        }
        items = newItems;
        firstIndex = 0;
        lastIndex = size;
    }
    public void addFirst(T item){
        if(size == items.length){
            resize(size * 2);
        }
        firstIndex = getPrevIndex(firstIndex);
        items[firstIndex] = item;
        size += 1;
    }
    public void addLast(T item){
        if(size == items.length){
            resize(size * 2);
        }
        items[lastIndex] = item;
        lastIndex = getNextIndex(lastIndex);
        size += 1;
    }
    public boolean isEmpty(){
        return size == 0;
    }
    public int size(){
        return size;
    }
    public void printDeque(){
        for(int i = 0; i < size; i++){
            System.out.print(items[getTrueIndex(i)] + " ");
        }
        System.out.println();
    }
    public T removeFirst(){
        if(size == 0){
            return null;
        }
        if(size<items.length/4 && items.length>8){
            resize(items.length/4);
        }
        T item = items[firstIndex];
        items[firstIndex] = null;
        firstIndex = getNextIndex(firstIndex);
        size -= 1;
        return item;
    }
    public T removeLast(){
        if(size == 0){
            return null;
        }
        if(size<items.length/4 && items.length>8){
            resize(items.length/4);
        }
        lastIndex = getPrevIndex(lastIndex);
        T item = items[lastIndex];
        items[lastIndex] = null;
        size -= 1;
        return item;
    }
    public T get(int index){
        return items[getTrueIndex(index)];
    }
}
