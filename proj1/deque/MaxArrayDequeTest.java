package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest {
    private static class IntegerComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }

    private static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

    @Test
    public void maxTest() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new IntegerComparator());
        mad.addLast(1);
        mad.addLast(3);
        mad.addLast(2);
        assertEquals((Integer) 3, mad.max());
    }

    @Test
    public void maxTest2() {
        MaxArrayDeque<String> mad = new MaxArrayDeque<>(new StringComparator());
        mad.addLast("a");
        mad.addLast("c");
        mad.addLast("b");
        assertEquals("c", mad.max());
    }
}
