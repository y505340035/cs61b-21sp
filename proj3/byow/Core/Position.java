package byow.Core;

import java.io.Serializable;

public class Position implements Serializable {
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position shift(int dx, int dy) {
        return new Position(x + dx, y + dy);
    }

    public boolean inTheMap(int mapLen, int mapHei) {
        if (x < 0 || x >= mapLen || y < 0 || y >= mapHei) {
            return false;
        }
        return true;
    }
}
