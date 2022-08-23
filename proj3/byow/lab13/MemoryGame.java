package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

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
        StdDraw.enableDoubleBuffering();

        // Initialize random number generator
        this.rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        // Generate random string of letters of length n
        char[] c = new char[n];
        for (int i = 0; i < n; i++) {
            int selection = RandomUtils.uniform(rand, 26);
            c[i] = (char) ('a' + selection);
        }
        return new String(c);
    }

    public void drawFrame(String s) {
        // Take the string and display it in the center of the screen
        StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
        Font font = new Font("myFont", Font.BOLD, 30);
        StdDraw.setFont(font);

        StdDraw.clear(StdDraw.BLACK);
        StdDraw.text(20, 20, s);

        // If game is not over, display relevant game information at the top of the screen
        if (!gameOver) {
            StdDraw.setFont(new Font("myFont", Font.CENTER_BASELINE, 18));
            StdDraw.textLeft(1, 39, "Round: " + round);

            String playStatus;
            if (playerTurn) {
                playStatus = "Type!";
            } else {
                playStatus = "Watch!";
            }
            StdDraw.text(20, 39, playStatus);

            int selection = RandomUtils.uniform(rand, ENCOURAGEMENT.length);
            StdDraw.textRight(39, 39, ENCOURAGEMENT[selection]);
            StdDraw.line(0, 38, 40, 38);
        }
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        // Display each character in letters, making sure to blank the screen between letters
        for (int i = 0; i < letters.length(); i++) {
            String s = letters.substring(i, i + 1);
            drawFrame(s);
            StdDraw.pause(1000);
            drawFrame("");
            StdDraw.pause(500);
        }
        playerTurn = true;
        drawFrame("");
    }

    public String solicitNCharsInput(int n) {
        // Read n letters of player input
        String s = new String();
        int i = 0;
//        playerTurn = true;
        //clean the input
        while (StdDraw.hasNextKeyTyped()) { StdDraw.nextKeyTyped(); }

        while (i < n) {
            if (StdDraw.hasNextKeyTyped()) {
                s = s + StdDraw.nextKeyTyped();
                drawFrame(s);
                i++;
            }
        }
        return s;
    }

    public void startGame() {
        // Set any relevant variables before the game starts
        round = 1;
        gameOver = false;

        // Establish Engine loop
        while (true) {
            playerTurn = false;
            drawFrame("Round:" + round);
            StdDraw.pause(1000);
            String s = generateRandomString(round);
            flashSequence(s);
            String input = solicitNCharsInput(s.length());
            if (!input.equals(s)) {
                gameOver = true;
                drawFrame("Game Over! You made it to round:" + round);
                break;
            }
            StdDraw.pause(500);
            round++;
        }
    }

}
