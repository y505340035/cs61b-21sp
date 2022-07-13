package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxArrayDequeTest {

    @Test
    public void bigMADequeTest() {
        Comparator<Integer> c1 = new Comparator<>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        };
        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<Integer>(c1);
        for (int i = 0; i < 10; i++) {
            mad1.addLast(i);
        }
        assertEquals(9, (int) mad1.max());
    }

    @Test
    public void smallMADequeTest() {
        Comparator<Integer> c1 = new Comparator<>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        };
        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<Integer>(c1);
        for (int i = 0; i < 10; i++) {
            mad1.addLast(i);
        }
        assertEquals(0, (int) mad1.max());
    }
}
