// Board.java
import java.util.*;

public class Board {
    private final int rows, cols;
    private final Candy[][] grid;
    private final Random rand = new Random();

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
    private List<Match> activateSpecial(int r, int c,
                                        CandyType type,
                                        CandyColor bombColor,       // only used for bomb
                                        CandyColor otherColor) {     // only used for bomb
        List<Position> pts = new ArrayList<>();

        switch(type) {
            case STRIPED_HORIZONTAL:
                for(int j=0;j<cols;j++) pts.add(new Position(r,j));
                break;

            case STRIPED_VERTICAL:
                for(int i=0;i<rows;i++) pts.add(new Position(i,c));
                break;

            case EXPLOSIVE:
                for(int dr=-1; dr<=1; dr++){
                    for(int dc=-1; dc<=1; dc++){
                        int rr=r+dr, cc=c+dc;
                        if(rr>=0 && rr<rows && cc>=0 && cc<cols)
                            pts.add(new Position(rr,cc));
                    }
                }
                break;

            case COLOR_BOMB:
                // clear bomb cell itself
                pts.add(new Position(r,c));
                // clear every candy of the OTHER color
                for(int i=0;i<rows;i++){
                    for(int j=0;j<cols;j++){
                        Candy cd = grid[i][j];
                        if(cd!=null && cd.getColor()==otherColor) {
                            pts.add(new Position(i,j));
                        }
                    }
                }
                break;

            default:
                return Collections.emptyList();
        }

        // actually clear them now
        for(Position p: pts) {
            grid[p.row][p.col] = null;
        }
        // we treat each activation as a NORMAL clear for scoring
        return List.of(new Match(pts, CandyType.NORMAL, null, null));
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
        if (!isValidMove(r1,c1,r2,c2)) {
            return Collections.emptyList();
        }

        // remember what's being swapped
        Candy c1Candy = grid[r1][c1];
        Candy c2Candy = grid[r2][c2];
        CandyType t1 = c1Candy.getType();
        CandyType t2 = c2Candy.getType();
        CandyColor col1 = c1Candy.getColor();
        CandyColor col2 = c2Candy.getColor();

        // do the swap
        swap(r1,c1,r2,c2);

        List<Match> total = new ArrayList<>();

        // 1) if it involved a special, activate it immediately
        if (t1 != CandyType.NORMAL || t2 != CandyType.NORMAL) {
            // whichever one WAS special (we swapped it) fires at its new position
            if (t1 != CandyType.NORMAL) {
                total.addAll( activateSpecial(r2, c2,
                        t1,
                        col1,    // bombColor (unused unless COLOR_BOMB)
                        col2) ); // otherColor
            } else {
                total.addAll( activateSpecial(r1, c1,
                        t2,
                        col2,
                        col1) );
            }

            // gravity + refill once, then allow cascades from random/new matches
            applyGravity();
            refillBoard();
        }

        // 2) now do normal ComboDetector‐driven cascades
        while (true) {
            List<Match> found = ComboDetector.findMatches(grid);
            if (found.isEmpty()) break;

            // clear & spawn any new specials
            for (Match m : found) {
                for (Position p : m.positions) {
                    grid[p.row][p.col] = null;
                }
                if (m.isSpecial()) {
                    // same logic you already had for placing newly created specials
                    grid[m.specialPosition.row]
                            [m.specialPosition.col] =
                            new Candy(m.color, m.resultType);
                }
            }
            total.addAll(found);

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
