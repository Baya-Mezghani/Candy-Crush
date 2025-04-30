public class Main {
    public static void main(String[] args) {
        int rows = 9;
        int cols = 9;
        int maxMoves = 20;

        System.out.println("Welcome to Candy Crush (Java Edition)!");
        Game game = new Game(rows, cols, maxMoves);
        game.play();
    }
}
