package byow.lab13;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.util.Random;

public class MemoryGame {
    /**
     * The characters we generate random Strings from.
     */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /**
     * Encouraging phrases. Used in the last section of the spec, 'Helpful UI'.
     */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
            "You got this!", "You're a star!", "Go Bears!",
            "Too easy for you!", "Wow, so impressive!"};
    /**
     * The width of the window of this game.
     */
    private int width;
    /**
     * The height of the window of this game.
     */
    private int height;
    /**
     * The current round the user is on.
     */
    private int round;
    /**
     * The Random object used to randomly generate Strings.
     */
    private Random rand;
    /**
     * Whether or not the game is over.
     */
    private boolean gameOver;
    /**
     * Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'.
     */
    private boolean playerTurn;
    private StringBuilder sb;
    private int sbLength;


    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.enableDoubleBuffering();

        rand = new Random(seed);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public String generateRandomString(int n) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < n; i++) {
            s.append(CHARACTERS[rand.nextInt(CHARACTERS.length)]);
        }
        return s.toString();
    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        if (!gameOver) {
            flashSequence(s);
            clearScreen();
        } else {
            StdDraw.text((double) width / 2, (double) height / 2, s);
            StdDraw.show();
        }
        //TODO: If game is not over, display relevant game information at the top of the screen
    }

    private void pauseBetweenLetters() {
        StdDraw.pause(1000);
        clearScreen();
        StdDraw.pause(500);
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        int len = letters.length();
        for (int i = 0; i < len; i++) {
            showALLThings(String.valueOf(letters.charAt(i)));
            StdDraw.pause(500);
        }
    }

    private void clearScreen() {
        StdDraw.clear(Color.BLACK);
        StdDraw.show();
    }

    public String solicitNCharsInput(int n) {
        while(sbLength<n){
            if(StdDraw.hasNextKeyTyped()){
                getInput(n);
                if(sbLength>=n){
                    break;
                }
            }
        }
        return sb.toString();
    }
    private void getInput(int n) {
        while(StdDraw.hasNextKeyTyped()){
            sb.append(StdDraw.nextKeyTyped());
            sbLength++;
            if(sbLength>=n){
                break;
            }
        }
    }
    private void showRound() {
        showALLThings("Round: " + round);
        while (StdDraw.hasNextKeyTyped()) {
            StdDraw.nextKeyTyped();
        }
    }

    private void showALLThings(String middleString) {
        getInput(round);
        String lowerString = sb.toString();
        clearScreen();
        if (!middleString.isEmpty()) {
            StdDraw.text((double) width * 0.5, (double) height * 0.5, middleString);
        }
        if (!lowerString.isEmpty()) {
            StdDraw.text((double) width * 0.5, (double) height * 0.25, lowerString);
        }
        StdDraw.show();
        StdDraw.pause(1000);
        clearScreen();
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        round = 1;
        gameOver = false;
        //TODO: Establish Engine loop
        while (!gameOver) {
            sb = new StringBuilder();
            sbLength = 0;
            showRound();
            String newString = generateRandomString(round);
            drawFrame(newString);
            String userInput = solicitNCharsInput(round);
            showALLThings("");
            if (!userInput.equals(newString)) {
                gameOver = true;
            } else {
                round++;
                StdDraw.pause(1000);
            }
        }
        if (gameOver) {
            drawFrame("Game Over! You made it to round: " + round);
        }
    }

}
