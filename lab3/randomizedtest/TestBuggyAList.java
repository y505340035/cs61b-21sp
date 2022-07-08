package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  //YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        Integer[] testNum = {4, 5, 6};

        AListNoResizing<Integer> aListNoResizing = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();
        for(int i=0;i<testNum.length;i++){
            aListNoResizing.addLast(testNum[i]);
            buggyAList.addLast(testNum[i]);
        }

        for(int i=0;i<testNum.length;i++){
            assertEquals(aListNoResizing.removeLast(), buggyAList.removeLast());
        }
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int Bsize = B.size();
                assertEquals(size, Bsize);
            } else if (L.size() != 0) {
                if (operationNumber == 2) {
                    //getLast
                    int last = L.getLast();
                    int Blast = B.getLast();
                    assertEquals(last, Blast);
                } else if (operationNumber == 3) {
                    //removeLast
                    int last = L.removeLast();
                    int Blast = B.removeLast();
                    assertEquals(last, Blast);
                }
            }
        }
    }
}
