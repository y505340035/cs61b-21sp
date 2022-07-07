package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }

    @Test
    public void testSquarePrimesSimple2(){
        IntList lst = IntList.of(2, 4, 6, 8, 10);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 4 -> 6 -> 8 -> 10", lst.toString());
        assertTrue(changed);
    }

    @Test
    public void testSquarePrimesSimple3(){
        IntList lst = IntList.of(12, 4, 6, 8, 10);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("12 -> 4 -> 6 -> 8 -> 10", lst.toString());
        assertTrue(!changed);
    }

    @Test
    public void testSquarePrimesSimple4(){
        IntList lst = IntList.of(2, 2, 2, 2, 2);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("4 -> 4 -> 4 -> 4 -> 4", lst.toString());
        assertTrue(changed);
    }
}
