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
    // fill the board with random candies (0 to 6 possible candies)
    private void initializeBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = randomCandy();
            }
        }
        // remove any initial matches
        while (removeMatches()) {
            applyGravity();
            refillBoard();
        }
    }
    // random candy creation
    private Candy randomCandy() {
        int color = rand.nextInt(6); // 6 possible colors
        return new Candy(color);
    }
    // to display the grid
    public void display() {
        for (Candy[] row : grid) { // looping through each line of the grid
            for (Candy c : row) { // looping through each candy in that row
                if (c == null) {
                    System.out.print(". ");
                } else {
                    System.out.print(c.getColor() + " ");
                }
            }
            System.out.println();
        }
    }
    // to verify if valid move or not
    public boolean isValidMove(int r1, int c1, int r2, int c2) {
        if (!areAdjacent(r1, c1, r2, c2)) return false;

        swap(r1, c1, r2, c2);
        boolean valid = !ComboDetector.findMatches(grid).isEmpty();
        swap(r1, c1, r2, c2); // swap back
        return valid;
    }
    // we need to fix this ***
    public void makeMove(int r1, int c1, int r2, int c2) {
        swap(r1, c1, r2, c2);
    }
    // swap the candies (permutation)
    private void swap(int r1, int c1, int r2, int c2) {
        Candy temp = grid[r1][c1];
        grid[r1][c1] = grid[r2][c2];
        grid[r2][c2] = temp;
    }
    // to verify adjacence
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
        for (int j = 0; j < cols; j++) {             // Loop through each column
            int emptyRow = rows - 1;                 // Start from the bottom row

            for (int i = rows - 1; i >= 0; i--) {    // Go from bottom to top
                if (grid[i][j] != null) {            // If there's a candy at (i, j)
                    grid[emptyRow][j] = grid[i][j];  // Move it to the current emptyRow
                    if (emptyRow != i) {
                        grid[i][j] = null;           // Clear the old position
                    }
                    emptyRow--;                      // Go one row up for next placement
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
