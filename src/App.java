
import java.awt.EventQueue;

public class App {

    public static void main(String[] args) {

        Runnable runner = () -> {
            GameMode gameMode = new GameMode("LotoFácil", 25, 15, 20, true, false, 3.00f);
            Game game = new Game(gameMode, 24, 20, null);
            System.out.println(game.totalCost);
        };
        EventQueue.invokeLater(runner);
    }
}
