package deque;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArrayDequeTest {
    @Test
    public void addIsEmptySizeTest() {
        ArrayDeque<String> ad = new ArrayDeque<>();
        ad.addFirst("front");
        ad.addLast("middle");
        ad.addLast("back");
        ad.printDeque();
    }

    @Test
    public void multipleAddRemoveTest() {
        ArrayDeque<String> ad = new ArrayDeque<>();
        ad.addFirst("a");
        ad.addFirst("b");
        ad.addLast("c");
        ad.addLast("d");
        assertEquals("b", ad.removeFirst());
        assertEquals("a", ad.removeFirst());
        assertEquals("d", ad.removeLast());
        assertEquals("c", ad.removeLast());
    }

    @Test
    public void getTest() {
        ArrayDeque<String> ad = new ArrayDeque<>();
        ad.addFirst("a");
        ad.addLast("b");
        ad.addLast("c");
        assertEquals("a", ad.get(0));
        assertEquals("b", ad.get(1));
        assertEquals("c", ad.get(2));
    }

    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        LinkedListDeque<Integer> linkedListDeque = new LinkedListDeque<>();
        Random rand = new Random();

        for (int i = 0; i < 10000; i++) {
            int operation = rand.nextInt(4);
            int value = rand.nextInt(1000);

            switch (operation) {
                case 0:
                    arrayDeque.addFirst(value);
                    linkedListDeque.addFirst(value);
                    break;
                case 1:
                    arrayDeque.addLast(value);
                    linkedListDeque.addLast(value);
                    break;
                case 2:
                    if (!arrayDeque.isEmpty() && !linkedListDeque.isEmpty()) {
                        assertEquals(linkedListDeque.removeFirst(), arrayDeque.removeFirst());
                    }
                    break;
                case 3:
                    if (!arrayDeque.isEmpty() && !linkedListDeque.isEmpty()) {
                        assertEquals(linkedListDeque.removeLast(), arrayDeque.removeLast());
                    }
                    break;
            }

            assertEquals(linkedListDeque.size(), arrayDeque.size());
        }
    }

    @Test
    public void testEqual() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        LinkedListDeque<Integer> ad2 = new LinkedListDeque<>();
        ad1.addFirst(1);
        ad1.addFirst(2);
        ad1.addFirst(3);
        ad2.addFirst(1);
        ad2.addFirst(2);
        ad2.addFirst(3);
        assertTrue(ad1.equals(ad2));
        assertTrue(ad2.equals(ad1));
    }
}
