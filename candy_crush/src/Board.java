import java.util.List;
import java.util.Random;

public class Board {
    private final int rows;
    private final int cols;
    private final Candy[][] grid;
    private final Random rand = new Random();

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new Candy[rows][cols];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = randomCandy();
            }
        }
        // Optional: remove any initial matches
        while (removeMatches()) {
            applyGravity();
            refillBoard();
        }
    }

    private Candy randomCandy() {
        int color = rand.nextInt(6); // 6 possible colors
        return new Candy(color);
    }

    public void display() {
        for (Candy[] row : grid) {
            for (Candy c : row) {
                if (c == null) {
                    System.out.print(". ");
                } else {
                    System.out.print(c.getColor() + " ");
                }
            }
            System.out.println();
        }
    }

    public boolean isValidMove(int r1, int c1, int r2, int c2) {
        if (!areAdjacent(r1, c1, r2, c2)) return false;

        swap(r1, c1, r2, c2);
        boolean valid = !ComboDetector.findMatches(grid).isEmpty();
        swap(r1, c1, r2, c2); // swap back
        return valid;
    }

    public void makeMove(int r1, int c1, int r2, int c2) {
        swap(r1, c1, r2, c2);
    }

    private void swap(int r1, int c1, int r2, int c2) {
        Candy temp = grid[r1][c1];
        grid[r1][c1] = grid[r2][c2];
        grid[r2][c2] = temp;
    }

    private boolean areAdjacent(int r1, int c1, int r2, int c2) {
        return (Math.abs(r1 - r2) == 1 && c1 == c2) || (r1 == r2 && Math.abs(c1 - c2) == 1);
    }

    public boolean removeMatches() {
        List<Position> matched = ComboDetector.findMatches(grid);
        if (matched.isEmpty()) return false;

        for (Position p : matched) {
            grid[p.row][p.col] = null;
        }
        return true;
    }

    public void applyGravity() {
        for (int j = 0; j < cols; j++) {
            int emptyRow = rows - 1;
            for (int i = rows - 1; i >= 0; i--) {
                if (grid[i][j] != null) {
                    grid[emptyRow][j] = grid[i][j];
                    if (emptyRow != i) grid[i][j] = null;
                    emptyRow--;
                }
            }
        }
    }

    public void refillBoard() {
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                if (grid[i][j] == null) {
                    grid[i][j] = randomCandy();
                }
            }
        }
    }

    public Candy[][] getGrid() {
        return grid;
    }
}
