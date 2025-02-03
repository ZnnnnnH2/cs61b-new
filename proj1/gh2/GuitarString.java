package gh2;

import deque.Deque;

//Note: This file will not compile until you complete the Deque implementations
public class GuitarString {
    /**
     * Constants. Do not change. In case you're curious, the keyword final
     * means the values cannot be changed at runtime. We'll discuss this and
     * other topics in lecture on Friday.
     */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor

    /* Buffer for storing sound data. */
    private Deque<Double> buffer;
    private int capacity;
    /* Create a guitar string of the given frequency.  */
    GuitarString(double frequency) {
        capacity = (int) Math.round((double) SR / frequency);
        buffer = new deque.ArrayDeque<>();
        for(int i=0;i<capacity;i++){
            buffer.addLast(0.0);
        }
    }


    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        for(int i=0;i<capacity;i++){
            buffer.removeFirst();
            buffer.addLast(Math.random()-0.5);
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {
        double front = buffer.removeFirst();
        double newSample = DECAY * 0.5 * (front + buffer.get(0));
        buffer.addLast(newSample);
    }

    /* Return the double at the front of the buffer. */
    public double sample() {
        return buffer.get(0);
    }
}
