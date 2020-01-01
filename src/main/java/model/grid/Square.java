package model.grid;

import java.util.Objects;

public class Square implements Comparable<Square> {
    public int x; // rowNumber
    public int y; // columnNumber

    public Square(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Square)) {
            return false;
        }

        var square = (Square) obj;
        return square.x == x && square.y == y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public int compareTo(Square o) {
        return x == o.x ?
                Integer.compare(y, o.y) :
                Integer.compare(x, o.x);
    }
}