package deque;

import java.util.Iterator;

public class LinkedListDeque<Item> implements Iterable<Item>, Deque<Item> {

    public class Node<Item> {
        public Item value;
        public Node n;
        public Node p;

        public Node(Item item, Node pri, Node next){
            value = item;
            n = next;
            p = pri;
        }
    }

    private Node<Item> head;
    private Node<Item> tail;
    private int size;

    public LinkedListDeque() {
        head = new Node(null, null, null);
        tail = new Node(null, head, null);
        head.n = tail;
        size = 0;
    }

    public void addFirst(Item item){
        Node first = new Node(item, head, head.n);
        head.n = first;
        first.n.p = first;
        size += 1;
    }

    public void addLast(Item item){
        Node first = new Node(item, tail.p, tail);
        tail.p = first;
        first.p.n = first;
        size += 1;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        Node first = head.n;
        while (first != tail){
            System.out.print(first.value + " ");
            first = first.n;
        }
        System.out.println("");
    }

    public Item removeFirst(){
        if (size == 0)
            return null;
        Node result = head.n;
        head.n = result.n;
        result.n.p = head;
        size -= 1;
        return (Item) result.value;
    }

    public Item removeLast(){
        if (size == 0)
            return null;
        Node result = tail.p;
        tail.p = result.p;
        result.p.n = tail;
        size -= 1;
        return (Item) result.value;
    }

    public Item get(int index){
        if (index >= size)
            return null;
        Node tmp = head.n;
        for (int i=0;i<index;i++){
            tmp = tmp.n;
        }
        return (Item) tmp.value;
    }

    public Item getRecursive(int index){
        if (index >= size)
            return null;
        Node tmp = head.n;
        return (Item) helpGet(index, tmp).value;
    }

    private Node helpGet(int i, Node node){
        if (i == 0)
            return node;
        return helpGet(i-1, node.n);
    }

    @Override
    public Iterator<Item> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<Item>{
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
        public Item next() {
            count += 1;
            node = node.n;
            return (Item) node.value;
        }
    }

    public boolean equals(Object o){
        if (!(o instanceof LinkedListDeque))
            return false;
        Node nodeThis = head.n;
        Node nodeO = ((LinkedListDeque<Item>) o).head.n;
        while ((nodeThis != tail) && (nodeO != ((LinkedListDeque<Item>) o).tail)){
            if (!(nodeThis.value.equals(nodeO.value)))
                return false;
            nodeThis = nodeThis.n;
            nodeO = nodeO.n;
        }
        return true;
    }
}
