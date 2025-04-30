import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Game game = new Game(8, 8);
        Scanner sc = new Scanner(System.in);

        while (!game.isGameOver()) {
            System.out.println("Enter move (row1 col1 row2 col2):");
            int r1 = sc.nextInt();
            int c1 = sc.nextInt();
            int r2 = sc.nextInt();
            int c2 = sc.nextInt();

            game.playTurn(r1, c1, r2, c2);
        }

        System.out.println("Game Over!");
    }
}
