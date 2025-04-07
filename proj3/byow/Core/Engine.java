package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

import static java.lang.Math.min;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 70;
    public static final int HEIGHT = 40;
    TERenderer ter = new TERenderer();
    static final double COVERRATE = 0.6;
    static final double exp = 1e-6;
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    private void fillEmptyTiles(TETile[][] tiles) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        //initialize the ter
        int seed = Integer.parseInt(input.substring(1, input.length() - 1));
        RandomNumberHelper RANDOM = new RandomNumberHelper(seed);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        ter.initialize(WIDTH, HEIGHT);
        fillEmptyTiles(finalWorldFrame);
        //generate the room
        double coverRate = 0.0;
        int totCover = 0;
        while(COVERRATE - coverRate >exp){
            int cover = engerateRoom(RANDOM, finalWorldFrame);
            totCover += cover;
            coverRate = (double) totCover / (WIDTH * HEIGHT);
        }

        //final operation
        ter.renderFrame(finalWorldFrame);
        return finalWorldFrame;
    }
    private void wallBuildHelper(int x,int y, TETile[][] finalWorldFrame) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            return;
        }
        if (finalWorldFrame[x][y] == Tileset.NOTHING) {
            finalWorldFrame[x][y] = Tileset.WALL;
        }
    }
    private int engerateRoom(RandomNumberHelper RANDOM, TETile[][] finalWorldFrame) {
        int x1 = RANDOM.nextInt(WIDTH - 2);
        int y1 = RANDOM.nextInt(HEIGHT - 2);
        int width = RANDOM.nextInt(10) + 2;
        int height = RANDOM.nextInt(10) + 2;
        int x2 = min(WIDTH - 1, x1 + width);
        int y2 = min(HEIGHT - 1, y1 + height);
        for (int i = x1; i <= x2; i++) {
            wallBuildHelper(i, y1, finalWorldFrame);
            wallBuildHelper(i, y2, finalWorldFrame);
        }
        for (int j = y1; j <= y2; j++) {
            wallBuildHelper(x1, j, finalWorldFrame);
            wallBuildHelper(x2, j, finalWorldFrame);
        }
        for (int i = x1 + 1; i < x2; i++) {
            for (int j = y1 + 1; j < y2; j++) {
                finalWorldFrame[i][j] = Tileset.FLOOR;
            }
        }
        return (width * height);
    }
}
