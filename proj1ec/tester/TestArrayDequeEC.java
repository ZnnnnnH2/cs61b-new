package tester;

import afu.org.checkerframework.checker.igj.qual.I;
import org.junit.Test;
import static org.junit.Assert.*;
import student.StudentArrayDeque;
import edu.princeton.cs.introcs.StdRandom;

public class TestArrayDequeEC {
    @Test
    public void randomTestFinal(){
        String message = "";
        StudentArrayDeque<Integer> sad1 = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> ads1 = new ArrayDequeSolution<>();
        int N = 10000;
        for(int i=0;i<N;i++){
            int method = StdRandom.uniform(4);
            switch (method){
                case 0:
                    sad1.addFirst(i);
                    ads1.addFirst(i);
                    message += "addFirst("+i+")\n";
                    break;
                case 1:
                    sad1.addLast(i);
                    ads1.addLast(i);
                    message += "addLast("+i+")\n";
                    break;
                case 2:
                    if(!sad1.isEmpty()){
                        Integer ans1 = sad1.removeFirst();
                        Integer ans2 = ads1.removeFirst();
                        message += "removeFirst()\n";
                        assertEquals(message,ans1,ans2);
                    }
                    break;
                case 3:
                    if(!sad1.isEmpty()){
                        Integer ans1 = sad1.removeLast();
                        Integer ans2 = ads1.removeLast();
                        message += "removeLast()\n";
                        assertEquals(message,ans1,ans2);
                    }
                    break;
            }
        }
    }
    @Test
    public void randomTestRemoveFirst(){
        String message = "";
        StudentArrayDeque<Integer> sad = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> ans = new ArrayDequeSolution<>();
        int N = 10000;
        int M = 6666;
        for(int i=0;i<N;i++){
            int insert = (int) StdRandom.uniform(54321);
            int method = (int) StdRandom.uniform(2);
            switch (method){
                case 0:
                    sad.addFirst(insert);
                    ans.addFirst(insert);
                    message += "addFirst("+insert+")\n";
                    break;
                case 1:
                    sad.addLast(insert);
                    ans.addLast(insert);
                    message += "addLast("+insert+")\n";
                    break;
            }
        }
        for(int i=0;i<M;i++){
            message += "removeFirst()\n";
            assertEquals(message,ans.removeFirst(),sad.removeFirst());
        }
    }
    @Test
    public void randomTestRemoveLast(){
        String message = "";
        StudentArrayDeque<Integer> sad = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> ans = new ArrayDequeSolution<>();
        int N = 10000;
        int M = 6666;
        for(int i=0;i<N;i++){
            int insert = (int) StdRandom.uniform(54321);
            int method = (int) StdRandom.uniform(2);
            switch (method){
                case 0:
                    sad.addFirst(insert);
                    ans.addFirst(insert);
                    message += "addFirst("+insert+")\n";
                    break;
                case 1:
                    sad.addLast(insert);
                    ans.addLast(insert);
                    message += "addLast("+insert+")\n";
                    break;
            }
        }
        for(int i=0;i<M;i++){
            message += "removeLast()\n";
            assertEquals(message,ans.removeLast(),sad.removeLast());
        }
    }
}
