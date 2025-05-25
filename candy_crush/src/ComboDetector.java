// ComboDetector.java
import java.util.*;

public class ComboDetector {
    public static List<Match> findMatches(Candy[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        boolean[][] used = new boolean[rows][cols];
        List<Match> matches = new ArrayList<>();

        // 1) FIVE-in-a-row → COLOR_BOMB
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c <= cols - 5; c++) {
                Candy base = grid[r][c];
                if (base == null) continue;
                CandyColor col = base.getColor();
                boolean ok = true;
                for (int k = 1; k < 5; k++) {
                    if (grid[r][c+k] == null || grid[r][c+k].getColor() != col) {
                        ok = false; break;
                    }
                }
                if (!ok) continue;

                List<Position> pts = new ArrayList<>();
                for (int k = 0; k < 5; k++) {
                    pts.add(new Position(r, c+k));
                    used[r][c+k] = true;
                }
                // place color bomb in the middle
                matches.add(new Match(pts,
                        CandyType.COLOR_BOMB,
                        new Position(r, c+2),
                        col));
            }
        }

        // 1b) FIVE-in-a-column → COLOR_BOMB
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r <= rows - 5; r++) {
                Candy base = grid[r][c];
                if (base == null) continue;
                CandyColor col = base.getColor();
                boolean ok = true;
                for (int k = 1; k < 5; k++) {
                    if (grid[r+k][c] == null || grid[r+k][c].getColor() != col) {
                        ok = false; break;
                    }
                }
                if (!ok) continue;

                List<Position> pts = new ArrayList<>();
                for (int k = 0; k < 5; k++) {
                    pts.add(new Position(r+k, c));
                    used[r+k][c] = true;
                }
                matches.add(new Match(pts,
                        CandyType.COLOR_BOMB,
                        new Position(r+2, c),
                        col));
            }
        }

        // 2) T-shapes → EXPLOSIVE (4 orientations)

        // 2a) T pointing DOWN:
        //   B B B
        //     B
        //     B
        for (int r = 0; r <= rows - 3; r++) {
            for (int c = 1; c < cols - 1; c++) {
                Candy center = grid[r][c];
                if (center == null) continue;
                CandyColor col = center.getColor();
                // check not already used
                if (used[r][c] || used[r][c-1] || used[r][c+1]
                        || used[r+1][c] || used[r+2][c]) continue;
                // pattern match
                if (allSame(grid, r,   c-1, col)
                        && allSame(grid, r,   c+1, col)
                        && allSame(grid, r+1, c,   col)
                        && allSame(grid, r+2, c,   col)) {
                    List<Position> pts = List.of(
                            new Position(r,   c-1),
                            new Position(r,   c),
                            new Position(r,   c+1),
                            new Position(r+1, c),
                            new Position(r+2, c)
                    );
                    pts.forEach(p -> used[p.row][p.col] = true);
                    matches.add(new Match(pts,
                            CandyType.EXPLOSIVE,
                            new Position(r, c),
                            col));
                }
            }
        }

        // 2b) T pointing UP:
        //     B
        //     B
        //   B B B
        for (int r = 2; r < rows; r++) {
            for (int c = 1; c < cols - 1; c++) {
                Candy center = grid[r][c];
                if (center == null) continue;
                CandyColor col = center.getColor();
                if (used[r][c] || used[r][c-1] || used[r][c+1]
                        || used[r-1][c] || used[r-2][c]) continue;
                if (allSame(grid, r,   c-1, col)
                        && allSame(grid, r,   c+1, col)
                        && allSame(grid, r-1, c,   col)
                        && allSame(grid, r-2, c,   col)) {
                    List<Position> pts = List.of(
                            new Position(r,   c-1),
                            new Position(r,   c),
                            new Position(r,   c+1),
                            new Position(r-1, c),
                            new Position(r-2, c)
                    );
                    pts.forEach(p -> used[p.row][p.col] = true);
                    matches.add(new Match(pts,
                            CandyType.EXPLOSIVE,
                            new Position(r, c),
                            col));
                }
            }
        }

        // 2c) T pointing RIGHT:
        //     B
        //   B B
        //     B
        //     B
        for (int r = 1; r < rows - 1; r++) {
            for (int c = 0; c <= cols - 3; c++) {
                Candy center = grid[r][c];
                if (center == null) continue;
                CandyColor col = center.getColor();
                if (used[r][c] || used[r-1][c] || used[r+1][c]
                        || used[r][c+1] || used[r][c+2]) continue;
                if (allSame(grid, r-1, c,   col)
                        && allSame(grid, r+1, c,   col)
                        && allSame(grid, r,   c+1, col)
                        && allSame(grid, r,   c+2, col)) {
                    List<Position> pts = List.of(
                            new Position(r-1, c),
                            new Position(r,   c),
                            new Position(r+1, c),
                            new Position(r,   c+1),
                            new Position(r,   c+2)
                    );
                    pts.forEach(p -> used[p.row][p.col] = true);
                    matches.add(new Match(pts,
                            CandyType.EXPLOSIVE,
                            new Position(r, c),
                            col));
                }
            }
        }

        // 2d) T pointing LEFT:
        //   B B B
        //     B
        //     B
        for (int r = 1; r < rows - 1; r++) {
            for (int c = 2; c < cols; c++) {
                Candy center = grid[r][c];
                if (center == null) continue;
                CandyColor col = center.getColor();
                if (used[r][c] || used[r-1][c] || used[r+1][c]
                        || used[r][c-1] || used[r][c-2]) continue;
                if (allSame(grid, r-1, c,   col)
                        && allSame(grid, r+1, c,   col)
                        && allSame(grid, r,   c-1, col)
                        && allSame(grid, r,   c-2, col)) {
                    List<Position> pts = List.of(
                            new Position(r-1, c),
                            new Position(r,   c),
                            new Position(r+1, c),
                            new Position(r,   c-1),
                            new Position(r,   c-2)
                    );
                    pts.forEach(p -> used[p.row][p.col] = true);
                    matches.add(new Match(pts,
                            CandyType.EXPLOSIVE,
                            new Position(r, c),
                            col));
                }
            }
        }

        // 3) L-shapes → EXPLOSIVE (4 rotations)
        // down→right
        for (int r = 0; r <= rows-3; r++) {
            for (int c = 0; c <= cols-3; c++) {
                if (checkL(grid, r, c, 1, 0, 2, 0, 2, 1, 2, 2)) {
                    List<Position> pts = List.of(
                            new Position(r,   c),
                            new Position(r+1, c),
                            new Position(r+2, c),
                            new Position(r+2, c+1),
                            new Position(r+2, c+2)
                    );
                    pts.forEach(p->used[p.row][p.col]=true);
                    matches.add(new Match(pts,
                            CandyType.EXPLOSIVE,
                            new Position(r+2, c+2),
                            grid[r][c].getColor()));
                }
            }
        }
        // down→left
        for (int r = 0; r <= rows-3; r++) {
            for (int c = 2; c < cols; c++) {
                if (checkL(grid, r, c, 1, 0, 2, 0, 2,-1, 2,-2)) {
                    List<Position> pts = List.of(
                            new Position(r,   c),
                            new Position(r+1, c),
                            new Position(r+2, c),
                            new Position(r+2, c-1),
                            new Position(r+2, c-2)
                    );
                    pts.forEach(p->used[p.row][p.col]=true);
                    matches.add(new Match(pts,
                            CandyType.EXPLOSIVE,
                            new Position(r+2, c-2),
                            grid[r][c].getColor()));
                }
            }
        }
        // up→right
        for (int r = 2; r < rows; r++) {
            for (int c = 0; c <= cols-3; c++) {
                if (checkL(grid, r, c,-1, 0,-2, 0,-2, 1,-2, 2)) {
                    List<Position> pts = List.of(
                            new Position(r,   c),
                            new Position(r-1, c),
                            new Position(r-2, c),
                            new Position(r-2, c+1),
                            new Position(r-2, c+2)
                    );
                    pts.forEach(p->used[p.row][p.col]=true);
                    matches.add(new Match(pts,
                            CandyType.EXPLOSIVE,
                            new Position(r-2, c+2),
                            grid[r][c].getColor()));
                }
            }
        }
        // up→left
        for (int r = 2; r < rows; r++) {
            for (int c = 2; c < cols; c++) {
                if (checkL(grid, r, c,-1, 0,-2, 0,-2,-1,-2,-2)) {
                    List<Position> pts = List.of(
                            new Position(r,   c),
                            new Position(r-1, c),
                            new Position(r-2, c),
                            new Position(r-2, c-1),
                            new Position(r-2, c-2)
                    );
                    pts.forEach(p->used[p.row][p.col]=true);
                    matches.add(new Match(pts,
                            CandyType.EXPLOSIVE,
                            new Position(r-2, c-2),
                            grid[r][c].getColor()));
                }
            }
        }

        // 4) FOUR-in-a-row → STRIPED_HORIZONTAL
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c <= cols-4; c++) {
                if (used[r][c]||used[r][c+1]||used[r][c+2]||used[r][c+3])
                    continue;
                Candy base = grid[r][c];
                if (base == null) continue;
                CandyColor col = base.getColor();
                if (grid[r][c+1]!=null && grid[r][c+1].getColor()==col &&
                        grid[r][c+2]!=null && grid[r][c+2].getColor()==col &&
                        grid[r][c+3]!=null && grid[r][c+3].getColor()==col) {
                    List<Position> pts = List.of(
                            new Position(r, c),
                            new Position(r, c+1),
                            new Position(r, c+2),
                            new Position(r, c+3)
                    );
                    pts.forEach(p->used[p.row][p.col]=true);
                    matches.add(new Match(pts,
                            CandyType.STRIPED_HORIZONTAL,
                            new Position(r, c+1),
                            col));
                }
            }
        }
        // 4b) FOUR-in-a-column → STRIPED_VERTICAL
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r <= rows-4; r++) {
                if (used[r][c]||used[r+1][c]||used[r+2][c]||used[r+3][c])
                    continue;
                Candy base = grid[r][c];
                if (base == null) continue;
                CandyColor col = base.getColor();
                if (grid[r+1][c]!=null && grid[r+1][c].getColor()==col &&
                        grid[r+2][c]!=null && grid[r+2][c].getColor()==col &&
                        grid[r+3][c]!=null && grid[r+3][c].getColor()==col) {
                    List<Position> pts = List.of(
                            new Position(r,   c),
                            new Position(r+1, c),
                            new Position(r+2, c),
                            new Position(r+3, c)
                    );
                    pts.forEach(p->used[p.row][p.col]=true);
                    matches.add(new Match(pts,
                            CandyType.STRIPED_VERTICAL,
                            new Position(r+1, c),
                            col));
                }
            }
        }

        // 5) THREE-in-a-row → NORMAL clear
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c <= cols-3; c++) {
                if (used[r][c]||used[r][c+1]||used[r][c+2]) continue;
                Candy base = grid[r][c];
                if (base == null) continue;
                CandyColor col = base.getColor();
                if (grid[r][c+1]!=null && grid[r][c+1].getColor()==col &&
                        grid[r][c+2]!=null && grid[r][c+2].getColor()==col) {
                    List<Position> pts = List.of(
                            new Position(r, c),
                            new Position(r, c+1),
                            new Position(r, c+2)
                    );
                    pts.forEach(p->used[p.row][p.col]=true);
                    matches.add(new Match(pts, CandyType.NORMAL, null, col));
                }
            }
        }
        // 5b) THREE-in-a-column → NORMAL clear
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r <= rows-3; r++) {
                if (used[r][c]||used[r+1][c]||used[r+2][c]) continue;
                Candy base = grid[r][c];
                if (base == null) continue;
                CandyColor col = base.getColor();
                if (grid[r+1][c]!=null && grid[r+1][c].getColor()==col &&
                        grid[r+2][c]!=null && grid[r+2][c].getColor()==col) {
                    List<Position> pts = List.of(
                            new Position(r,   c),
                            new Position(r+1, c),
                            new Position(r+2, c)
                    );
                    pts.forEach(p->used[p.row][p.col]=true);
                    matches.add(new Match(pts, CandyType.NORMAL, null, col));
                }
            }
        }

        return matches;
    }

    // helper for L-shapes
    private static boolean checkL(Candy[][] g,
                                  int r, int c,
                                  int dr1,int dc1,
                                  int dr2,int dc2,
                                  int dr3,int dc3,
                                  int dr4,int dc4) {
        Candy base = g[r][c];
        if (base == null) return false;
        CandyColor col = base.getColor();
        return  allSame(g, r+dr1, c+dc1, col)
                && allSame(g, r+dr2, c+dc2, col)
                && allSame(g, r+dr3, c+dc3, col)
                && allSame(g, r+dr4, c+dc4, col);
    }

    private static boolean allSame(Candy[][] g, int r, int c, CandyColor col) {
        return g[r][c] != null && g[r][c].getColor() == col;
    }
}
