// Board.java
import java.util.*;

public class Board {
    private final int rows, cols;
    private final Candy[][] grid;
    private final Random rand = new Random();
    private Position lastSwap1, lastSwap2;


    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Candy[rows][cols];
        initializeBoard();
    }

    private void initializeBoard() {
        // fill until no initial matches remain
        do {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    grid[r][c] = randomCandy();
                }
            }
        } while (!ComboDetector.findMatches(grid).isEmpty());
    }

    private Candy randomCandy() {
        CandyColor[] vals = CandyColor.values();
        return new Candy(vals[rand.nextInt(vals.length)]);
    }

    public void display() {
        for (Candy[] row : grid) {
            for (Candy c : row) {
                if (c == null) {
                    System.out.print(".  ");
                } else {
                    // pad to 2 characters for alignment
                    String s = c.toString();
                    System.out.print(s.length()==1 ? s + "  " : s + " ");
                }
            }
            System.out.println();
        }
    }


    private boolean areAdjacent(int r1,int c1,int r2,int c2) {
        return (Math.abs(r1-r2)==1 && c1==c2)
                || (r1==r2 && Math.abs(c1-c2)==1);
    }

    public boolean isValidMove(int r1, int c1, int r2, int c2) {
        if (!areAdjacent(r1,c1,r2,c2)) return false;

        Candy c1Candy = grid[r1][c1];
        Candy c2Candy = grid[r2][c2];
        boolean specialSwap = c1Candy.isSpecial() || c2Candy.isSpecial();

        // if it’s a special swap, it’s always valid
        if (specialSwap) return true;

        // otherwise must create a classic match
        swap(r1,c1,r2,c2);
        boolean ok = !ComboDetector.findMatches(grid).isEmpty();
        swap(r1,c1,r2,c2);
        return ok;
    }


    public void swap(int r1,int c1,int r2,int c2) {
        Candy tmp = grid[r1][c1];
        grid[r1][c1] = grid[r2][c2];
        grid[r2][c2] = tmp;
    }

    /**
     * Returns a list of Matches corresponding to activating
     * the special candy at (r,c).  Also immediately clears
     * those positions in `grid` (setting them to null).
     *
     * For:
     *  • STRIPED_HORIZONTAL: clears entire row r
     *  • STRIPED_VERTICAL:   clears entire col c
     *  • EXPLOSIVE:          clears the 3×3 centered at (r,c)
     *  • COLOR_BOMB:         clears every candy matching `otherColor`
     *                        plus the bomb itself at (r,c)
     */
    // helper to trigger a single special‐swap effect
    private void triggerSpecialSwap(
            CandyType type, int er, int ec, CandyColor otherColor,
            List<Match> total
    ) {
        List<Position> pts = new ArrayList<>();
        switch (type) {
            case STRIPED_HORIZONTAL:
                // clear row 'er'
                for (int c = 0; c < cols; c++) pts.add(new Position(er, c));
                break;
            case STRIPED_VERTICAL:
                // clear column 'ec'
                for (int r = 0; r < rows; r++) pts.add(new Position(r, ec));
                break;
            case EXPLOSIVE:
                // clear 3×3 around (er,ec)
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int rr = er + dr, cc = ec + dc;
                        if (rr >= 0 && rr < rows && cc >= 0 && cc < cols)
                            pts.add(new Position(rr, cc));
                    }
                }
                break;
            case COLOR_BOMB:
                // clear the bomb itself at (er,ec)
                pts.add(new Position(er, ec));
                // clear every candy matching otherColor
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        Candy cd = grid[r][c];
                        if (cd != null && cd.getColor() == otherColor) {
                            pts.add(new Position(r, c));
                        }
                    }
                }
                break;
            default:
                return;
        }

        // actually clear
        for (Position p : pts) {
            grid[p.row][p.col] = null;
        }
        // record it as one big NORMAL‐type match
        total.add(new Match(pts, CandyType.NORMAL, null, null));

        applyGravity();
        refillBoard();
    }


    /**
     * Performs a swap, then repeatedly:
     *   • findMatches → clear those positions → spawn special if any
     *   • apply gravity
     *   • refill EMPTY spots
     * until no matches remain.
     * Returns **all** Match objects found this turn.
     */
    public List<Match> makeMoveAndGetMatches(int r1, int c1, int r2, int c2) {
        if (!isValidMove(r1, c1, r2, c2)) return Collections.emptyList();

        // remember swap coords
        lastSwap1 = new Position(r1, c1);
        lastSwap2 = new Position(r2, c2);

        // remember what was there
        Candy before1 = grid[r1][c1];
        Candy before2 = grid[r2][c2];
        CandyType t1 = before1.getType(), t2 = before2.getType();
        CandyColor col1 = before1.getColor(), col2 = before2.getColor();

        // do the swap
        swap(r1, c1, r2, c2);

        List<Match> total = new ArrayList<>();

        // 1) handle special‐for‐normal swap
        boolean s1 = t1 != CandyType.NORMAL;
        boolean s2 = t2 != CandyType.NORMAL;
        if (s1 || s2) {
            // if both are special, trigger both on each other's spots
            if (s1) {
                triggerSpecialSwap(t1, r2, c2, col2, total);
            }
            if (s2) {
                triggerSpecialSwap(t2, r1, c1, col1, total);
            }
        }

        // 2) now do your normal spawn‐on‐swap + cascade logic
        //    (we'll catch any new 4/T/L/5 bonuses here)

        // first pass: catch any special‐creating match
        List<Match> initial = ComboDetector.findMatches(grid);
        if (!initial.isEmpty()) {
            for (Match m : initial) {
                // if it’s special, force it to the swapped cell
                if (m.isSpecial()) {
                    Position spawnAt = null;
                    if (m.positions.contains(lastSwap1)) spawnAt = lastSwap1;
                    else if (m.positions.contains(lastSwap2)) spawnAt = lastSwap2;
                    if (spawnAt != null) {
                        m = new Match(m.positions, m.resultType, spawnAt, m.color);
                    }
                }
                // clear matched candies
                for (Position p : m.positions) grid[p.row][p.col] = null;
                // spawn the new special, if any
                if (m.isSpecial()) {
                    grid[m.specialPosition.row]
                            [m.specialPosition.col] =
                            new Candy(m.color, m.resultType);
                }
                total.add(m);
            }
            applyGravity();
            refillBoard();
        }

        // 3) full cascade
        while (true) {
            List<Match> found = ComboDetector.findMatches(grid);
            if (found.isEmpty()) break;
            for (Match m : found) {
                for (Position p : m.positions) grid[p.row][p.col] = null;
                if (m.isSpecial()) {
                    grid[m.specialPosition.row]
                            [m.specialPosition.col] =
                            new Candy(m.color, m.resultType);
                }
                total.add(m);
            }
            applyGravity();
            refillBoard();
        }

        return total;
    }


    private void applyGravity() {
        for (int c = 0; c < cols; c++) {
            int write = rows - 1;
            for (int r = rows - 1; r >= 0; r--) {
                if (grid[r][c] != null) {
                    grid[write--][c] = grid[r][c];
                }
            }
            // null out anything above write
            for (int r = write; r >= 0; r--) {
                grid[r][c] = null;
            }
        }
    }

    private void refillBoard() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == null) {
                    grid[r][c] = randomCandy();
                }
            }
        }
    }

    public Candy[][] getGrid() {
        return grid;
    }
}
