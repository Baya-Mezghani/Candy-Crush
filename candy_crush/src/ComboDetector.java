import java.util.ArrayList;
import java.util.List;

public class ComboDetector {
    public static List<Position> findMatches(Candy[][] grid) {
        List<Position> matched = new ArrayList<>();
        int rows = grid.length;
        int cols = grid[0].length;

        // Horizontal match detection
        for (int i = 0; i < rows; i++) {
            int count = 1;
            for (int j = 1; j < cols; j++) {
                if (grid[i][j] != null && grid[i][j - 1] != null &&
                        grid[i][j].getColor() == grid[i][j - 1].getColor()) {
                    count++;
                } else {
                    if (count >= 3) {
                        for (int k = j - count; k < j; k++) {
                            matched.add(new Position(i, k));
                        }
                    }
                    count = 1;
                }
            }
            if (count >= 3) {
                for (int k = cols - count; k < cols; k++) {
                    matched.add(new Position(i, k));
                }
            }
        }

        // Vertical match detection (similar logic)
        for (int j = 0; j < cols; j++) {
            int count = 1;
            for (int i = 1; i < rows; i++) {
                if (grid[i][j] != null && grid[i - 1][j] != null &&
                        grid[i][j].getColor() == grid[i - 1][j].getColor()) {
                    count++;
                } else {
                    if (count >= 3) {
                        for (int k = i - count; k < i; k++) {
                            matched.add(new Position(k, j));
                        }
                    }
                    count = 1;
                }
            }
            if (count >= 3) {
                for (int k = rows - count; k < rows; k++) {
                    matched.add(new Position(k, j));
                }
            }
        }

        return matched;
    }
}
