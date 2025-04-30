public class Game {
    private Board board;
    private int score = 0;
    private int movesLeft = 20;

    public Game(int rows, int cols) {
        board = new Board(rows, cols);
    }

    public void playTurn(int r1, int c1, int r2, int c2) {
        board.swap(r1, c1, r2, c2);
        if (board.isMatchAt(r1, c1) || board.isMatchAt(r2, c2)) {
            movesLeft--;
            while (board.removeMatches()) {
                score += 10;
                board.dropCandies();
            }
        } else {
            board.swap(r1, c1, r2, c2); // revert
        }
        board.printBoard();
        System.out.println("Score: " + score + " | Moves left: " + movesLeft);
    }

    public boolean isGameOver() {
        return movesLeft <= 0;
    }
}
