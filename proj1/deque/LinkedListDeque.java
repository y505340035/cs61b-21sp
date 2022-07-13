package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {

    public class Node<T> {
        public T value;
        public Node n;
        public Node p;

        public Node(T item, Node pri, Node next){
            value = item;
            n = next;
            p = pri;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public LinkedListDeque() {
        head = new Node(null, null, null);
        tail = new Node(null, head, null);
        head.n = tail;
        size = 0;
    }

    @Override
    public void addFirst(T t){
        Node first = new Node(t, head, head.n);
        head.n = first;
        first.n.p = first;
        size += 1;
    }

    @Override
    public void addLast(T t){
        Node first = new Node(t, tail.p, tail);
        tail.p = first;
        first.p.n = first;
        size += 1;
    }

    @Override
    public int size(){
        return size;
    }

    @Override
    public void printDeque(){
        Node first = head.n;
        while (first != tail) {
            System.out.print(first.value + " ");
            first = first.n;
        }
        System.out.println("");
    }

    @Override
    public T removeFirst(){
        if (size == 0) {
            return null;
        }
        Node result = head.n;
        head.n = result.n;
        result.n.p = head;
        size -= 1;
        return (T) result.value;
    }

    @Override
    public T removeLast(){
        if (size == 0) {
            return null;
        }
        Node result = tail.p;
        tail.p = result.p;
        result.p.n = tail;
        size -= 1;
        return (T) result.value;
    }

    @Override
    public T get(int index){
        if (index >= size) {
            return null;
        }
        Node tmp = head.n;
        for (int i=0;i<index;i++) {
            tmp = tmp.n;
        }
        return (T) tmp.value;
    }

    public T getRecursive(int index){
        if (index >= size) {
            return null;
        }
        Node tmp = head.n;
        return (T) helpGet(index, tmp).value;
    }

    private Node helpGet(int i, Node node){
        if (i == 0) {
            return node;
        }
        return helpGet(i-1, node.n);
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T>{
        private Node node;
        private int count;

        public LinkedListDequeIterator(){
            count = 0;
            node = head;
        }

        @Override
        public boolean hasNext() {
            return count < size;
        }

        @Override
        public T next() {
            count += 1;
            node = node.n;
            return (T) node.value;
        }
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        Node nodeThis = head.n;
        Node nodeO = ((LinkedListDeque<T>) o).head.n;
        while ((nodeThis != tail) && (nodeO != ((LinkedListDeque<T>) o).tail)){
            if (!(nodeThis.value.equals(nodeO.value))) {
                return false;
            }
            nodeThis = nodeThis.n;
            nodeO = nodeO.n;
        }
        return true;
    }
}
