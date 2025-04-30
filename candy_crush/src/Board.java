import java.util.Random;

public class Board {
    private Candy[][] grid;
    private final int rows;
    private final int cols;
    private final int colorCount = 5;
    private Random rand = new Random();

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new Candy[rows][cols];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Candy(rand.nextInt(colorCount));
            }
        }
    }

    public Candy getCandy(int row, int col) {

        return grid[row][col];
    }

    public void swap(int r1, int c1, int r2, int c2) {
        Candy temp = grid[r1][c1];
        grid[r1][c1] = grid[r2][c2];
        grid[r2][c2] = temp;
    }

    public boolean isMatchAt(int row, int col) {
        int color = grid[row][col].getColor();

        // horizontal
        int count = 1;
        for (int i = col - 1; i >= 0 && grid[row][i].getColor() == color; i--) count++;
        for (int i = col + 1; i < cols && grid[row][i].getColor() == color; i++) count++;
        if (count >= 3) return true;

        // vertical
        count = 1;
        for (int i = row - 1; i >= 0 && grid[i][col].getColor() == color; i--) count++;
        for (int i = row + 1; i < rows && grid[i][col].getColor() == color; i++) count++;
        return count >= 3;
    }

    public boolean removeMatches() {
        boolean[][] toRemove = new boolean[rows][cols];
        boolean found = false;

        // detection horizontale et verticale
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (isMatchAt(i, j)) {
                    toRemove[i][j] = true;
                    found = true;
                }
            }
        }

        // suppression
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (toRemove[i][j]) {
                    grid[i][j] = null;
                }
            }
        }

        return found;
    }

    public void dropCandies() {
        for (int col = 0; col < cols; col++) {
            int emptyRow = rows - 1;
            for (int row = rows - 1; row >= 0; row--) {
                if (grid[row][col] != null) {
                    grid[emptyRow][col] = grid[row][col];
                    if (emptyRow != row) grid[row][col] = null;
                    emptyRow--;
                }
            }
            for (int row = emptyRow; row >= 0; row--) {
                grid[row][col] = new Candy(rand.nextInt(colorCount));
            }
        }
    }

    public void printBoard() {
        for (Candy[] row : grid) {
            for (Candy c : row) {
                System.out.print(c.getColor() + " ");
            }
            System.out.println();
        }
        System.out.println("-----------");
    }
}
