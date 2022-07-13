package deque;

import java.util.Iterator;

public interface Deque<T> {
    public void addFirst(T t);

    public void addLast(T t);

    public default boolean isEmpty(){
        return size() == 0;
    };

    public int size();

    public void printDeque();

    public T removeFirst();

    public T removeLast();

    public T get(int index);

    public Iterator<T> iterator();

    public boolean equals(Object o);
}
