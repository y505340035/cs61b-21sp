package tester;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;
import student.StudentArrayDeque;

public class TestArrayDequeEC {

    @Test
    public void randomizedTest() {
        StudentArrayDeque<Integer> studentArrayDeque = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> arrayDequeSolution = new ArrayDequeSolution<>();

        int N = 500000;
        for (int i = 0; i < N; i++) {
            int randomNum = StdRandom.uniform(0, 7);
            if (randomNum == 0) {
                //addFirst
                studentArrayDeque.addFirst(i);
                arrayDequeSolution.addFirst(i);
            } else if (randomNum == 1  && !studentArrayDeque.isEmpty() && !arrayDequeSolution.isEmpty()) {
                //removeFirst
                Integer s = studentArrayDeque.removeFirst();
                Integer a = arrayDequeSolution.removeFirst();
                assertEquals(s, a);
            } else if (randomNum == 2) {
                //size
                assertEquals(studentArrayDeque.size(), arrayDequeSolution.size());
            } else if (randomNum == 3) {
                //isEmpty
                assertEquals(studentArrayDeque.isEmpty(), arrayDequeSolution.isEmpty());
            } else if (randomNum == 4) {
                //addLast
                studentArrayDeque.addLast(i);
                arrayDequeSolution.addLast(i);
            } else if (randomNum == 5 && !studentArrayDeque.isEmpty() && !arrayDequeSolution.isEmpty()) {
                //removeLast
                Integer s = studentArrayDeque.removeLast();
                Integer a = arrayDequeSolution.removeLast();
                assertEquals(s, a);
            } else if (randomNum == 6 && !studentArrayDeque.isEmpty() && !arrayDequeSolution.isEmpty()) {
                //get
                int randomGet = StdRandom.uniform(0, studentArrayDeque.size());
                assertEquals(studentArrayDeque.get(randomGet), arrayDequeSolution.get(randomGet));
            }
        }
    }
}
