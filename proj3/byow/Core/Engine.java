package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    TERenderer teRenderer;
    InputSource inputDevice;
    Position avatar;
    String seed;

    public Engine() {
        teRenderer = null;
    }

    public Engine(boolean graph) {
        if (graph) {
            teRenderer = new TERenderer();
            teRenderer.initialize(WIDTH, HEIGHT);
        }
    }
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public TETile[][] interactWithKeyboard() {
        inputDevice = new KeyboardInputSource();
        drawFirstView();
        return startMenu();
    }

    private TETile[][] startMenu() {
//        drawFirstView();
        boolean flag = true;
        TETile[][] world;

        while (flag) {
            char c = inputDevice.getNextKey();
            System.out.println(c);
            switch (c) {
                case 'n':
                    world = newGame();
                    return continueGame(world);
                case 'l':
                    world = newGame();
                    load(world, seed);
                    return continueGame(world);
                case 'q':
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }
        return null;

    }

    private TETile[][] newGame() {
        String s = "n";
        if (teRenderer != null) {
            drawFrame(s);
        }

        while (true) {
            char c = inputDevice.getNextKey();
            if (Character.isDigit(c)) {
                s = s + c;
                if (teRenderer != null) {
                    drawFrame(s);
                }
            } else if (c == 's') {
                s = s + c;
//                interactWithInputString(s);
                TETile[][] world = getWorldWithString(s);
                return world;
            }
        }


    }

    private void drawFirstView() {
        StdDraw.setCanvasSize(this.WIDTH * 16, this.HEIGHT * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.WIDTH);
        StdDraw.setYscale(0, this.HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
        StdDraw.text(WIDTH / 2, HEIGHT / 6 * 5, "CS61B: THE GAME");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 5, "Quit (Q)");
        StdDraw.show();
    }

    private void drawFrame(String s) {
        StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
//        Font font = new Font("myFont", Font.BOLD, 30);
//        StdDraw.setFont(font);
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);
        StdDraw.show();
    }




    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        input.toLowerCase();
        inputDevice = new StringInputDevice(input);
        TETile[][] finalWorld = startMenu();
        save(seed);
        return finalWorld;
    }

    private void save(String s) {
        File saveFolder = new File("./.byow");
        if (!saveFolder.exists()) {
            saveFolder.mkdir();
        }
        File saveFile = Utils.join(saveFolder, s + ".txt");
        Utils.writeObject(saveFile, avatar);
        System.out.println("\n\nX: " + avatar.getX() + "\nY: " + avatar.getY());
    }

    private void load(TETile[][] world, String s) {
        File loadFolder = new File("./.byow");
        File loadFile = Utils.join(loadFolder, s + ".txt");
        if (loadFile.exists()) {
            setPoint(world, avatar, Tileset.FLOOR);
            avatar = Utils.readObject(loadFile, Position.class);
            System.out.println("\n\nX: " + avatar.getX() + "\nY: " + avatar.getY());
            setPoint(world, avatar, Tileset.AVATAR);
            if (teRenderer != null) {
                teRenderer.renderFrame(world);
            }
        }
    }

    private TETile[][] getWorldWithString(String input) {
        input.toLowerCase();
        seed = input;
        input = input.substring(1, input.length() - 1);
        System.out.println(input);
        Random random = new Random(Long.parseLong(input));
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        CreateWorld createWorld = new CreateWorld(finalWorldFrame, random);
        avatar = createWorld.createRandomWorld(Tileset.FLOOR, Tileset.WALL);
        if (teRenderer != null) {
            teRenderer.renderFrame(finalWorldFrame);
        }

        return finalWorldFrame;
    }


    private TETile[][] continueGame(TETile[][] world) {
        while (inputDevice.possibleNextInput()) {
            char c = Character.toLowerCase(inputDevice.getNextKey());
            System.out.println("Press: " + c);
            switch (c) {
                case 'w':
                    move(world, avatar, 0, 1);
                    break;
                case 'a':
                    move(world, avatar, -1, 0);
                    break;
                case 's':
                    move(world, avatar, 0, -1);
                    break;
                case 'd':
                    move(world, avatar, 1, 0);
                    break;
                default:
                    break;
            }
        }
        return world;
    }

    private void move(TETile[][] world, Position p, int dx, int dy) {
        Position dp = p.shift(dx, dy);
        if (dp.inTheMap(WIDTH, HEIGHT) && getPoint(world, dp) == Tileset.FLOOR) {
            setPoint(world, dp, Tileset.AVATAR);
            setPoint(world, p, Tileset.FLOOR);
            avatar = dp;
            System.out.println("\n\nX: " + avatar.getX() + "\nY: " + avatar.getY());
            if (teRenderer != null) {
                teRenderer.renderFrame(world);
            }
        }
    }

    private TETile getPoint(TETile[][] world, Position p) {
        return world[p.getX()][p.getY()];
    }

    private void setPoint(TETile[][] world, Position p, TETile tile) {
        world[p.getX()][p.getY()] = tile;
    }
}
