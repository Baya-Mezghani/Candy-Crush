import java.util.Scanner;

public class Game {
    private final Board board;
    private final int maxMoves;
    private int movesLeft;
    private int score;

    public Game(int rows, int cols, int maxMoves) {
        board = new Board(rows, cols);
        this.maxMoves = maxMoves;
        this.movesLeft = maxMoves;
        this.score = 0;
    }

    public void play() {
        Scanner scanner = new Scanner(System.in);

        while (movesLeft > 0) {
            board.display();
            System.out.println("Moves left: " + movesLeft + " | Score: " + score);
            System.out.print("Enter move (row1 col1 row2 col2): ");
            int r1 = scanner.nextInt();
            int c1 = scanner.nextInt();
            int r2 = scanner.nextInt();
            int c2 = scanner.nextInt();

            if (!board.isValidMove(r1, c1, r2, c2)) {
                System.out.println("Invalid move. Try again.");
                continue;
            }

            board.makeMove(r1, c1, r2, c2);
            movesLeft--;

            boolean matched;
            do {
                matched = board.removeMatches();
                if (matched) {
                    score += 10;
                    board.applyGravity();
                    board.refillBoard();
                }
            } while (matched);
        }

        System.out.println("Game over! Final score: " + score);
    }
}
