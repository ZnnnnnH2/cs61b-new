package byow.Core;

import java.util.Random;

public class RandomNumberHelper {
    Random RANDOM;
    int seed;

    public RandomNumberHelper(int seed) {
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
}
