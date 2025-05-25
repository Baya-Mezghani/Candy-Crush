import java.util.List;
import java.util.Scanner;

public class Game {
    private final Board board;
    private final int maxMoves;
    private int movesLeft;
    private int score;

    public Game(int rows, int cols, int maxMoves) {
        this.board     = new Board(rows, cols);
        this.maxMoves  = maxMoves;
        this.movesLeft = maxMoves;
        this.score     = 0;
    }
    public List<Match> makeMove(int r1, int c1, int r2, int c2) {
        // delegate to board
        List<Match> matches = board.makeMoveAndGetMatches(r1, c1, r2, c2);
        if (matches.isEmpty()) {
            return matches;  // invalid move, no change
        }
        // valid move → consume one move
        movesLeft--;

        // award 10 points per candy cleared
        int turnScore = matches.stream()
                .mapToInt(m -> m.positions.size() * 10)
                .sum();
        score += turnScore;

        return matches;
    }

    public void play() {
        Scanner scanner = new Scanner(System.in);

        while (movesLeft > 0) {
            board.display();
            System.out.printf("Moves left: %d | Score: %d%n", movesLeft, score);
            System.out.print("Enter swap (r1 c1 r2 c2): ");

            int r1 = scanner.nextInt(),
                    c1 = scanner.nextInt(),
                    r2 = scanner.nextInt(),
                    c2 = scanner.nextInt();

            // perform the move and get every match this turn
            List<Match> matches = board.makeMoveAndGetMatches(r1, c1, r2, c2);
            if (matches.isEmpty()) {
                System.out.println("Invalid move (no match). Try again.\n");
                continue;
            }

            movesLeft--;

            // print each match and its score
            int turnScore = 0;
            System.out.println("Combos this turn:");
            for (Match m : matches) {
                int pts = m.positions.size() * 10;
                turnScore += pts;
                System.out.printf("  • %s at %s  → +%d%n",
                        m.resultType,
                        m.positions,
                        pts);
            }

            score += turnScore;
            System.out.println("Total this turn: " + turnScore + "\n");
        }

        System.out.println("Game over! Final score: " + score);
        scanner.close();
    }

    /** Expose the player’s current score */
    public int getScore() {
        return score;
    }

    /** Expose how many moves remain */
    public int getMovesLeft() {
        return movesLeft;
    }

    /** Expose the board so the UI can both read and drive it */
    public Board getBoard() {
        return board;
    }
}
