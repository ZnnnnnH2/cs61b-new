package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class History implements Serializable {
    public Tuple userPosition;
    public RandomNumberHelper RANDOM;
    public TETile[][] finalWorldFrame;
    public int munberOfFlawer;

    History(TETile[][] finalWorldFrame, int munberOfFlawer, RandomNumberHelper RANDOM, Tuple userPosition){
        this.userPosition = new Tuple(userPosition.first,userPosition.second);
        this.RANDOM = new RandomNumberHelper(RANDOM.seed);
        this.RANDOM.RANDOM = RANDOM.RANDOM;
        this.finalWorldFrame = new TETile[finalWorldFrame.length][finalWorldFrame[0].length];
        for (int i = 0; i < finalWorldFrame.length; i++) {
            System.arraycopy(finalWorldFrame[i], 0, this.finalWorldFrame[i], 0, finalWorldFrame[0].length);
        }
        this.munberOfFlawer = munberOfFlawer;
    }
}
