package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        BuggyAList<Integer> b = new BuggyAList<>();
        AListNoResizing<Integer> a = new AListNoResizing<>();
        for(int i=4;i<7;i++){
            b.addLast(i);
            a.addLast(i);
        }
        for(int i=0;i<3;i++){
            assertEquals(a.removeLast(),b.removeLast());
        }
    }
    @Test
    public void randomizedRest(){
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
//                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size1 = L.size();
                int size2 = B.size();
                assertEquals(size1,size2);
//                System.out.println("size: " + size1);
            }else if(L.size()>0){
                if(operationNumber==2) {
                    //getLast
                    int last1 = L.getLast();
                    int last2 = B.getLast();
                    assertEquals(last1,last2);
//                    System.out.println("getLast: " + last1);
                }
                else{
                    //removeLast
                    int last1 = L.removeLast();
                    int last2 = B.removeLast();
                    assertEquals(last1,last2);
//                    System.out.println("removeLast: " + last1);
                }
            }
        }
    }
}
