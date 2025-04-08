package byow.Core;

import java.io.*;
import java.util.Random;

public class RandomNumberHelper implements Serializable {
    private Random RANDOM;
    private long seed;

    public RandomNumberHelper(long seed) {
        this.seed = seed;
        RANDOM = new Random(seed);
    }

    public int nextInt(int bound) {
        return RANDOM.nextInt(bound);
    }

    public int nextInt(int min, int max) {
        int ans = 0;
        while (ans <= min) {
            ans = RANDOM.nextInt(max - ans);
        }
        return ans;
    }

    public long getSeed() {
        return seed;
    }

    public Random getRandom() {
        return RANDOM;
    }

    public void setRANDOM(Random random) {
        this.RANDOM = random;
    }
}
