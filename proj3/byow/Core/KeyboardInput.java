package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

public class KeyboardInput implements Input {
    KeyboardInput() {
        StdDraw.setPenColor(StdDraw.WHITE);
//        Font font = new Font("Monaco", Font.BOLD, 30);
//        StdDraw.setFont(font);
        StdDraw.text(40, 50, "remained flower: ");
    }

    @Override
    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                return c;
            }
        }
    }

    @Override
    public boolean possibleNextInput() {
        return true;
    }
}
