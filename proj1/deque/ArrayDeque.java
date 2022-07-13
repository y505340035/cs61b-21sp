package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T>{
    T[] items;
    int realSize;
    int allSize;
    int start;
    int end;

    public ArrayDeque(){
        items = (T[]) new Object[8];
        realSize = 0;
        allSize = 8;
        start = 3;
        end = 4;
    }

    private void resize(int size){
        int newStart = (size - realSize) / 2;
        T[] newItems = (T[]) new Object[size];
        System.arraycopy(items, start+1, newItems, newStart, realSize);
        allSize = size;
        start = newStart-1;
        end = newStart + realSize;
        items = newItems;
    }


    @Override
    public void addFirst(T t) {
        if (start == 0){
            resize((int) (allSize * 1.6));
        }
        items[start] = t;
        start -= 1;
        realSize += 1;
    }

    @Override
    public void addLast(T t) {
        if (end == allSize-1){
            resize((int) (allSize * 1.6));
        }
        items[end] = t;
        end += 1;
        realSize += 1;
    }

    @Override
    public int size() {
        return realSize;
    }

    @Override
    public void printDeque() {
        for(int i=start+1;i<end;i++){
            System.out.print(items[i] + " ");
        }
        System.out.println("");
    }

    @Override
    public T removeFirst() {
        if (realSize == 0)
            return null;
        start += 1;
        T result = items[start];
        realSize -= 1;
        if ((allSize > 100) && (allSize / realSize >= 4))
            resize(allSize / ((allSize / realSize)/2));
        return result;
    }

    @Override
    public T removeLast() {
        if (realSize == 0)
            return null;
        end -= 1;
        T result = items[end];
        realSize -= 1;
        if ((allSize > 100) && (allSize / realSize >= 4))
            resize(allSize / ((allSize / realSize)/2));
        return result;
    }

    @Override
    public T get(int index) {
        if (index >= realSize)
            return null;
        return items[start+1+index];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T>{
        private int count;

        public ArrayDequeIterator(){
            int count = -1;
        }

        @Override
        public boolean hasNext() {
            return count+1 < realSize;
        }

        @Override
        public T next() {
            count += 1;
            return items[start + 1 + count];
        }
    }
}