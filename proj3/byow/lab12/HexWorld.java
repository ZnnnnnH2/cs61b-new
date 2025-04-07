package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    static final int WIDTH = 50;
    static final int HEIGHT = 50;

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] tiles = new TETile[WIDTH][HEIGHT];
        fillEmpty(tiles);
        printHexagon(10, 10, 4, tiles, Tileset.FLOWER);

        ter.renderFrame(tiles);
    }

    private static void fillEmpty(TETile[][] tiles) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    private static void printHexagon(int x, int y, int size, TETile[][] tiles, TETile tile) {
        for (int layer = 0; layer < size; layer++) {
            int layerY = y + layer;
            if (layerY >= HEIGHT) {
                break;
            }
            for (int lX = max(0, x - layer); lX < min(WIDTH, x + size + layer); lX++) {
                tiles[lX][layerY] = tile;
            }
        }
        for (int layer = 0; layer < size; layer++) {
            int layerY = y + layer + size;
            if (layerY >= HEIGHT) {
                break;
            }
            for (int lX = max(0, x - (size - layer) + 1); lX < min(WIDTH, x + 2*size - layer - 1); lX++) {
                tiles[lX][layerY] = tile;
            }
        }
    }
}
