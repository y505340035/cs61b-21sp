package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

public class CreateWorld {
    private ArrayList<Room> rooms;
    private Point[][] pointsMap;
    private ArrayList<ArrayList<Room>> disjointSets;
    private TETile[][] tileWorld;
    private Random random;

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
            int nx = this.x + dx;
            int ny = this.y + dy;
            if (nx >= pointsMap.length || ny >= pointsMap[0].length || nx < 0 || ny < 0) {
                return null;
            }
            return pointsMap[nx][ny];
        }
    }

    class Room {
        int length;
        int height;
        Point p;
        ArrayList<Room> disjointSet;

        Room(int length, int height, Point p, ArrayList<Room> friends) {
            this.length = length;
            this.height = height;
            this.p = p;
            this.disjointSet = friends;
        }

        public boolean randomDetect() {

            for (int i = 0; i < 99; i++) {
                // down right up left
                int direction = RandomUtils.uniform(random, 4);
                int selection;
                int dx = 0;
                int dy = 0;
                Point selP = null;
                if (direction % 2 == 0) {
                    selection = RandomUtils.uniform(random, length);
                } else {
                    selection = RandomUtils.uniform(random, height);
                }
                switch (direction) {
                    case 0:
                        selP = getPoint(this.p.x + selection, this.p.y);
                        dy = -1;
                        break;
                    case 1:
                        selP = getPoint(this.p.x + length - 1, this.p.y + selection);
                        ray(selP, 1, 0);
                        dx = 1;
                        break;
                    case 2:
                        selP = getPoint(this.p.x + selection, this.p.y + height - 1);
                        ray(selP, 0, 1);
                        dy = 1;
                        break;
                    case 3:
                        selP = getPoint(this.p.x, this.p.y + selection);
                        ray(selP, -1, 0);
                        dx = -1;
                        break;
                    default:
                        break;
                }
                if (ray(selP, dx, dy)) {
                    return true;
                }
            }
            return false;
        }

        private boolean ray(Point selP, int dx, int dy) {
//            final int LENGTH = pointsMap.length;
//            final int HEIGHT = pointsMap[0].length;
            Set<Point> setPoints = new HashSet<>();
//            Point dp = selP;
//            selP.tile = Tileset.TREE;
            while (true) {
                selP = selP.nextPoint(dx, dy);
                if (selP == null) {
                    return false;
                }
                if (selP.father != null) {
                    if (selP.father != null && selP.father.disjointSet != this.disjointSet) {
                        for (Point tmp: setPoints) {
                            tmp.tile = Tileset.FLOOR;
                        }
                        this.disjointSet.addAll(selP.father.disjointSet);
                        disjointSets.remove(selP.father.disjointSet);
                        selP.father.disjointSet = this.disjointSet;
                        return true;
                    } else {
                        return false;
                    }
                }
                setPoints.add(selP);
            }

        }

    }

    private Point getPoint(int x, int y) {
        if ((x >= pointsMap.length || y >= pointsMap[0].length || x < 0 || y < 0)) {
            return null;
        }
        return pointsMap[x][y];
    }

    public CreateWorld(TETile[][] world, Random random) {
        int len = world.length;
        int hei = world[0].length;
        pointsMap = new Point[len][hei];
        tileWorld = world;
        this.random = random;
        disjointSets = new ArrayList<>();

        for (int x = 0; x < len; x++) {
            for (int y = 0; y < hei; y++) {
                pointsMap[x][y] = new Point(x, y, Tileset.NOTHING, null);
            }
        }

    }

    public Position createRandomWorld(TETile innerTile, TETile wallTile) {
        randomDrawRooms(innerTile, wallTile, 20, 40);
        connectRooms(innerTile, wallTile);
        createWalls(wallTile);
        Position p = createTheBoy(Tileset.AVATAR);
        makeChangeDone();
        return p;
    }

    private Position createTheBoy(TETile tile) {
        final int worldLen = tileWorld.length;
        final int worldHei = tileWorld[0].length;

        while (true) {
            int x = RandomUtils.uniform(random, worldLen);
            int y = RandomUtils.uniform(random, worldHei);

            Point p = getPoint(x, y);
            if (p.tile == Tileset.FLOOR) {
                p.tile = tile;
                return new Position(x, y);
            }
        }
    }

    private void randomDrawRooms(TETile innerTile, TETile wallTile, int minNum, int maxNum) {

        final int worldLen = tileWorld.length;
        final int worldHei = tileWorld[0].length;

        int existRoom = 0;
        int roomNumLimit = minNum + RandomUtils.uniform(random, maxNum - minNum);
        rooms = new ArrayList<>();

        while (existRoom < roomNumLimit) {
            int randomLen = 1 + RandomUtils.uniform(random, 6);
            int randomHei = 1 + RandomUtils.uniform(random, 6);

            int randomX = 1 + RandomUtils.uniform(random, worldLen - randomLen - 1);
            int randomY = 1 + RandomUtils.uniform(random, worldHei - randomHei - 1);
            Point randomP = getPoint(randomX, randomY);

            if (!touch(randomP, randomLen, randomHei)) {
//                drawRoom(innerTile, wallTile, randomP, randomLen, randomHei);
                drawRectangle(innerTile, randomP, randomLen, randomHei);
                ArrayList<Room> roomSet = new ArrayList<>();
                Room room = new Room(randomLen, randomHei, randomP, roomSet);
                roomSet.add(room);
                disjointSets.add(roomSet);
                registerRect(randomP, randomLen, randomHei, room);
                rooms.add(room);
                existRoom++;
            }
        }

//        connectRooms(innerTile, wallTile);
    }

    private void connectRooms(TETile innerTile, TETile wallTile) {
        int i = 0;
        while (disjointSets.size() > 1 && i < 3333) {
            int selFatherArray = RandomUtils.uniform(random, disjointSets.size());
            ArrayList<Room> childSet = disjointSets.get(selFatherArray);
            int selection = RandomUtils.uniform(random, childSet.size());
            Room selRoom = childSet.get(selection);
            for (int j = 0; j < 20; j++) {
                if (selRoom.randomDetect()) {
                    break;
                }
            }
            System.out.println("set number " + disjointSets.size() + "\nrun times: " + i);
            i++;
        }

//        makeChangeDone();
    }

    private void createWalls(TETile wallTile) {
        for (int x = 0; x < tileWorld.length; x++) {
            for (int y = 0; y < tileWorld[0].length; y++) {
                Point p = getPoint(x, y);
                if (p.tile == Tileset.FLOOR) {
                    onePointCreateWall(getPoint(x, y), wallTile);
                }
            }
        }

        makeChangeDone();
    }

    private void onePointCreateWall(Point p, TETile wallTile) {
        int[] dxArray = {1, 1, 0, -1, -1, -1,  0,  1};
        int[] dyArray = {0, 1, 1,  1,  0, -1, -1, -1};
        for (int i = 0; i < dxArray.length; i++) {
            int dx = dxArray[i];
            int dy = dyArray[i];
            Point dp = p.nextPoint(dx, dy);
            if (dp != null && dp.tile == Tileset.NOTHING) {
                dp.tile = wallTile;
            }
        }
    }

    private boolean touch(Point p, int length, int height) {
        int px = p.x;
        int py = p.y;
        for (int dx = 0; dx < length; dx++) {
            for (int dy = 0; dy < height; dy++) {
                if (getPoint(px + dx, py + dy).tile != Tileset.NOTHING) {
                    return true;
                }
            }
        }
        return false;
    }

    // draw a room or rectangle
    /**
     * The room is rectangle plus outline.
     * So the minimum length and height is 3.
     * Position p in the left bottom conner of the room.
     */
    public void drawRoom(TETile innerTile, TETile wallTile, Point p, int length, int height) {
        if (length < 3 || height < 3) {
            return;
        }
        drawOutline(wallTile, p, length, height);
        p = p.nextPoint(1, 1);
        drawRectangle(innerTile, p, length - 2, height - 2);
    }

    private void makeChangeDone() {
        for (int x = 0; x < tileWorld.length; x++) {
            for (int y = 0; y < tileWorld[0].length; y++) {
                tileWorld[x][y] = getPoint(x, y).tile;
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

    private void drawOutline(TETile tile, Point p, int length, int height) {
        drawRow(tile, p, length);
        drawColumn(tile, p, height);

        p = p.nextPoint(length - 1, height - 1);
        drawRow(tile, p, -length);
        drawColumn(tile, p, -height);
    }

    private void drawRectangle(TETile tile, Point p, int length, int height) {
        int direction = 1;
        if (height < 0) {
            direction = -1;
            height = height * -1;
        }

        for (int dy = 0; dy < height; dy++) {
            drawRow(tile, p, length);
            p = p.nextPoint(0, 1 * direction);
        }
    }

    private void drawRow(TETile tile, Point p, int len) {
        drawLine(tile, p, len, true);
    }

    private void drawColumn(TETile tile, Point p, int len) {
        drawLine(tile, p, len, false);
    }

    private void drawLine(TETile tile, Point p, int len, boolean isRow) {
        int direction = 1;
        if (len < 0) {
            direction = -1;
            len = len * -1;
        }

        int shape = isRow ? 1 : 0;
        for (int i = 0; i < len; i++) {
            putTile(tile, p);
            p = p.nextPoint(shape * direction, (1 - shape) * direction);
        }
    }

    private void putTile(TETile tile, Point p) {
        p.tile = tile;
    }

    //--- test code
    public void testLine(int x, int y, TETile innerTile, TETile outsideTile, int len, int hei) {
        Point p = getPoint(x, y);
//        drawRoom(innerTile, outsideTile, p, len, hei);
//        drawLine(outsideTile, p, len, true);
        drawRectangle(innerTile, p, len, hei);
        makeChangeDone();
    }
}
