package byow.Core;

import java.io.*;
import java.util.Random;

public class RandomNumberHelper implements Serializable {
    public Random RANDOM;
    public long seed;

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

    public RandomNumberHelper deepCopy() throws IOException, ClassNotFoundException {
        // 利用序列化实现深拷贝
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (RandomNumberHelper) ois.readObject();
    }
}
