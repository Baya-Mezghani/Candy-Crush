public class Position {
    public int row, col;
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position p)
            return this.row == p.row && this.col == p.col;
        return false;
    }

    @Override
    public int hashCode() {
        return row * 31 + col;
    }
}
