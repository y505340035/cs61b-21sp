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
        String wrongMsg = new String("\n");

        int N = 500000;
        for (int i = 0; i < N; i++) {
            int randomNum = StdRandom.uniform(0, 7);
            if (randomNum == 0) {
                //addFirst
                studentArrayDeque.addFirst(i);
                arrayDequeSolution.addFirst(i);
                wrongMsg += "addFirst" + "(" + i + ")\n";
            } else if (randomNum == 1  && !studentArrayDeque.isEmpty() && !arrayDequeSolution.isEmpty()) {
                //removeFirst
                Integer s = studentArrayDeque.removeFirst();
                Integer a = arrayDequeSolution.removeFirst();
                wrongMsg += "removeFirst()\n";
                assertEquals(wrongMsg, s, a);
            } else if (randomNum == 2) {
                //size
                wrongMsg += "size()\n";
                assertEquals(wrongMsg, studentArrayDeque.size(), arrayDequeSolution.size());
            } else if (randomNum == 3) {
                //isEmpty
                wrongMsg += "isEmpty()\n";
                assertEquals(wrongMsg, studentArrayDeque.isEmpty(), arrayDequeSolution.isEmpty());
            } else if (randomNum == 4) {
                //addLast
                studentArrayDeque.addLast(i);
                arrayDequeSolution.addLast(i);
                wrongMsg += "addLast" + "(" + i + ")\n";
            } else if (randomNum == 5 && !studentArrayDeque.isEmpty() && !arrayDequeSolution.isEmpty()) {
                //removeLast
                Integer s = studentArrayDeque.removeLast();
                Integer a = arrayDequeSolution.removeLast();
                wrongMsg += "removeLast()\n";
                assertEquals(wrongMsg, s, a);
            } else if (randomNum == 6 && !studentArrayDeque.isEmpty() && !arrayDequeSolution.isEmpty()) {
                //get
                int randomGet = StdRandom.uniform(0, studentArrayDeque.size());
                wrongMsg += "get" + "(" + randomGet + ")\n";
                assertEquals(wrongMsg, studentArrayDeque.get(randomGet), arrayDequeSolution.get(randomGet));
            }
        }
    }
}
