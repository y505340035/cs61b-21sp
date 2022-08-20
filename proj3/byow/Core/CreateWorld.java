package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

public class CreateWorld {
    private ArrayList<Room> rooms;
    private Point[][] pointsMap;
    private TETile[][] tileWorld;

    class Point {
        int x;
        int y;
        TETile tile;
        Room father;

        Point(int x, int y, TETile tile, Room father) {
            this.x = x;
            this.y = y;
            this.tile = tile;
            this.father = father;
        }

        Point nextPoint(int dx, int dy) {
            int nx = x + dx;
            int ny = x + dy;
            if (nx >= pointsMap.length || ny >= pointsMap[0].length) {
                return null;
            }
            return pointsMap[nx][ny];
        }
    }

    class Room {
        int length;
        int height;
        Position p;
        ArrayList<Room> friends;

        public Room(int length, int height, Position p, ArrayList<Room> friends) {
            this.length = length;
            this.height = height;
            this.p = p;
            this.friends = friends;
        }

    }

    public void randomDrawRooms(TETile[][] world, TETile innerTile, TETile wallTile, Random random, int minNum, int maxNum) {

        final int worldLen = world.length;
        final int worldHei = world[0].length;

        int existRoom = 0;
        int roomNumLimit = minNum + RandomUtils.uniform(random, maxNum - minNum);
        rooms = new ArrayList<>();

        while (existRoom < roomNumLimit) {
            int randomLen = 3 + RandomUtils.uniform(random, 10);
            int randomHei = 3 + RandomUtils.uniform(random, 10);

            int randomX = RandomUtils.uniform(random, worldLen - randomLen);
            int randomY = RandomUtils.uniform(random, worldHei - randomHei);
            Position randomP = new Position(randomX, randomY);

            if (!touch(world, randomP, randomLen, randomHei)) {
                drawRoom(world, innerTile, wallTile, randomP, randomLen, randomHei);
                rooms.add(new Room(randomLen, randomHei, randomP, new ArrayList<>()));
                existRoom ++;
            }
        }

    }

    private void connectRooms(TETile[][] world, TETile innerTile, TETile wallTile, Random random) {

    }

    private boolean touch(TETile[][] world, Position p, int length, int height) {
        int px = p.getX();
        int py = p.getY();
        for (int dx = 0; dx < length; dx++) {
            for (int dy = 0; dy < height; dy++) {
                if (world[px + dx][py + dy] != Tileset.NOTHING) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * The room is rectangle plus outline.
     * So the minimum length and height is 3.
     * Position p in the left bottom conner of the room.
     */
    public void drawRoom(TETile[][] world, TETile innerTile, TETile wallTile, Position p, int length, int height) {
        if (length < 3 || height < 3) { return; }
        drawOutline(world, wallTile, p, length, height);
        Position dp = p.shift(1, 1);
        drawRectangle(world, innerTile, dp, length - 2, height - 2);
    }

    public CreateWorld (TETile[][] world) {
        int len = world.length;
        int hei = world[0].length;
        pointsMap = new Point[len][hei];
        tileWorld = world;

        for (int x = 0; x < len; x++) {
            for (int y = 0; y < hei; y++) {
                pointsMap[x][y] = new Point(x, y, Tileset.NOTHING, null);
            }
        }

    }

    private void registerRect(Point p, int length, int height, Room room) {
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < height; y++) {
                registerP(p, room);
            }
        }
    }

    private void registerP(Point p, Room room) {
        p.father = room;
    }

    private void drawOutline(TETile[][] world, TETile tile, Position p, int length, int height) {
        drawRow(world, tile, p, length);
        drawColumn(world, tile, p, height);

        Position dp = p.shift(length - 1, height - 1);
        drawRow(world, tile, dp, - length);
        drawColumn(world, tile, dp, - height);
    }

    private void drawRectangle(TETile[][] world, TETile tile, Position p, int length, int height) {
        int direction = 1;
        if (height < 0) {
            direction = -1;
            height = height * -1;
        }

        Position dp = p;
        for (int dy = 0; dy < height; dy++) {
            drawRow(world, tile, dp, length);
            dp = dp.shift(0, 1 * direction);
        }
    }

    private void drawRow(TETile[][] world, TETile tile, Position p, int len) {
        drawLine(world, tile, p, len, true);
    }

    private void drawColumn(TETile[][] world, TETile tile, Position p, int len) {
        drawLine(world, tile, p, len, false);
    }

    private void drawLine(TETile[][] world, TETile tile, Position p, int len, boolean isRow) {
        int direction = 1;
        if (len < 0) {
            direction = -1;
            len = len * -1;
        }

        Position dp = p;
        int shape = isRow ? 1 : 0;
        for (int i = 0; i < len; i++) {
            putTile(world, tile, dp);

            // register
//            if (tile == Tileset.FLOOR) {
//                registerP(dp);
//            }

            dp = dp.shift(shape * direction, (1 - shape) * direction);
        }
    }

    private void putTile(TETile[][] world, TETile tile, Position p) {
        world[p.getX()][p.getY()] = tile;
    }
}
