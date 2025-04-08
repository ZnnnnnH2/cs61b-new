package byow.Core;

import byow.TileEngine.TETile;

import java.io.Serializable;

public class History implements Serializable {
    private Tuple userPosition;
    private RandomNumberHelper historyRandom;
    private TETile[][] finalWorldFrame;
    private int munberOfFlawer;

    History(TETile[][] finalWorldFrame, int munberOfFlawer,
            RandomNumberHelper RANDOM, Tuple userPosition) {
        this.userPosition = new Tuple(userPosition.getFirst(), userPosition.getSecond());
        this.historyRandom = new RandomNumberHelper(RANDOM.getSeed());
        this.historyRandom.setRANDOM(RANDOM.getRandom());
        this.finalWorldFrame = new TETile[finalWorldFrame.length][finalWorldFrame[0].length];
        for (int i = 0; i < finalWorldFrame.length; i++) {
            System.arraycopy(finalWorldFrame[i], 0, this.finalWorldFrame[i],
                    0, finalWorldFrame[0].length);
        }
        this.munberOfFlawer = munberOfFlawer;
    }

    public Tuple getUserPosition() {
        return userPosition;
    }

    public RandomNumberHelper getHistoryRandom() {
        return historyRandom;
    }

    public TETile[][] getFinalWorldFrame() {
        return finalWorldFrame;
    }

    public int getMunberOfFlawer() {
        return munberOfFlawer;
    }
}
