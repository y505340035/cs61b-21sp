package deque;

import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        comparator = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        T max = get(0);
        for (Iterator<T> it = iterator(); it.hasNext(); ) {
            T item = it.next();
            if (comparator.compare(item, max) > 0) {
                max = item;
            }
        }
        return max;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T max = get(0);
        for (Iterator<T> it = iterator(); it.hasNext(); ) {
            T item = it.next();
            if (c.compare(item, max) > 0) {
                max = item;
            }
        }
        return max;
    }
}
