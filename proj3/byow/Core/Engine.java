package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 70;
    public static final int HEIGHT = 40;
    private static final double COVERRATE = 0.4;
    private static final double exp = 1e-6;
    TERenderer ter = new TERenderer();
    List<Tuple> roomList = new ArrayList<>();

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
        long seed = Long.parseLong(input.substring(1, input.length() - 1));
        RandomNumberHelper RANDOM = new RandomNumberHelper(seed);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
//        ter.initialize(WIDTH, HEIGHT);
        fillEmptyTiles(finalWorldFrame);
        //generate the room
        double coverRate = 0.0;
        int totCover = 0;
        while (COVERRATE - coverRate > exp) {
            int cover = generateRoom(RANDOM, finalWorldFrame);
            totCover += cover;
            coverRate = (double) totCover / (WIDTH * HEIGHT);
        }
        //generate the hallway
        roomList.sort(Tuple::compareTo);
        generateHallways(RANDOM, finalWorldFrame);
        //final operation
//        ter.renderFrame(finalWorldFrame);
        return finalWorldFrame;
    }

    private void generateHallways(RandomNumberHelper RANDOM, TETile[][] worldFrame) {
        int len = roomList.size();
        for (int i = 0; i < len - 1; i++) {
            generateSignalHallways(RANDOM, worldFrame, roomList.get(i), roomList.get(i + 1));
        }
    }

    private void generateSignalHallways(RandomNumberHelper RANDOM, TETile[][] worldFrame, Tuple a, Tuple b) {
        int x1 = a.getFirst();
        int x2 = b.getFirst();
        int y1 = a.getSecond();
        int y2 = b.getSecond();
        int width = RANDOM.nextInt(2) + 1;
        if (y1 <= y2) {
            for (int i = x1; i <= x2; i++) {
                wallBuildHelper(i, y1 - 1, worldFrame);
                wallBuildHelper(i, y1 + width, worldFrame);
                for (int j = 0; j < width; j++) {
                    worldFrame[i][y1 + j] = Tileset.FLOOR;
                }
            }
            for (int i = y1 + width - 1; i <= y2; i++) {
                wallBuildHelper(x2 + 1, i, worldFrame);
                wallBuildHelper(x2 - width, i, worldFrame);
                for (int j = 0; j < width; j++) {
                    worldFrame[x2 - j][i] = Tileset.FLOOR;
                }
            }
        } else {
            for (int i = x1; i <= x2; i++) {
                wallBuildHelper(i, y1 - 1, worldFrame);
                wallBuildHelper(i, y1 + width, worldFrame);
                for (int j = 0; j < width; j++) {
                    worldFrame[i][y1 + j] = Tileset.FLOOR;
                }
            }
            for (int i = y2; i <= y1 + width - 1; i++) {
                wallBuildHelper(x2 + 1, i, worldFrame);
                wallBuildHelper(x2 - width, i, worldFrame);
                for (int j = 0; j < width; j++) {
                    worldFrame[x2 - j][i] = Tileset.FLOOR;
                }
            }
        }
    }

    private void wallBuildHelper(int x, int y, TETile[][] finalWorldFrame) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            return;
        }
        if (finalWorldFrame[x][y] == Tileset.NOTHING) {
            finalWorldFrame[x][y] = Tileset.WALL;
        }
    }

    private int generateRoom(RandomNumberHelper RANDOM, TETile[][] finalWorldFrame) {
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
        int midX = (x1 + x2) / 2;
        int midY = (y1 + y2) / 2;
        roomList.add(new Tuple(midX, midY));
        return (width * height);
    }
}
