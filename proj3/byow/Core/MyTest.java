package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class MyTest {
    static final int length = 120;
    static final int width = 60;
    static Random random = new Random(11);

    public static void main(String[] args) {
        TETile[][] world = new TETile[length][width];
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < width; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        TERenderer teRenderer = new TERenderer();
        teRenderer.initialize(length, width);

        // my test code below
        CreateWorld createWorld = new CreateWorld(world, random);
        createWorld.createRandomWorld(Tileset.FLOOR, Tileset.WALL);
//        createWorld.testLine(10, 10,Tileset.FLOOR, Tileset.WALL, 3, 3);
//        createWorld.randomDrawRooms(Tileset.FLOOR, Tileset.WALL, 30, 40);
        // my test code end

        teRenderer.renderFrame(world);

//        createWorld.connectRooms(Tileset.FLOOR, Tileset.WALL);
//        createWorld.createWalls(Tileset.WALL);
//        teRenderer.renderFrame(world);
    }
}
