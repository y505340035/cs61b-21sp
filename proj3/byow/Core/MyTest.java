package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class MyTest {
    static final int length = 100;
    static final int width = 60;
    static Random random = new Random(111);

    public static void main(String[] args) {
        TETile[][] world = new TETile[length][width];
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < width; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        TERenderer teRenderer = new TERenderer();
        teRenderer.initialize(length, width);

        // my test code below this
        CreateWorld createWorld = new CreateWorld(world);
//        Position p = new Position(10, 10);
//        createWorld.drawRoom(world, Tileset.FLOOR, Tileset.WALL, p, 20, 20);
        createWorld.randomDrawRooms(world, Tileset.FLOOR, Tileset.WALL, random, 30, 40);
        // my test code end

        teRenderer.renderFrame(world);
    }
}
