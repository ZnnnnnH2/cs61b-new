package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class History implements Serializable {
    public Tuple userPosition;
    public RandomNumberHelper RANDOM;
    public TETile[][] finalWorldFrame;
    public int munberOfFlawer;
    public Input keyboard;

    History(TETile[][] finalWorldFrame, int munberOfFlawer, RandomNumberHelper RANDOM, Tuple userPosition) {
        this.finalWorldFrame = finalWorldFrame;
        this.munberOfFlawer = munberOfFlawer;
        this.RANDOM = RANDOM;
        this.userPosition = userPosition;
    }
}
