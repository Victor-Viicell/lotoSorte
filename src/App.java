
import java.awt.EventQueue;
import java.util.Arrays;

public class App {

    public static void main(String[] args) {

        Runnable runner = () -> {
            GameMode gameMode = new GameMode("GameMode", 10, 7, 21, false, true);
            for (int i = 0; i < 1; i++) {
                System.out.println(Arrays.deepToString(gameMode.superSete.genSuperSeven(7)));
            }
        };
        EventQueue.invokeLater(runner);
    }
}
