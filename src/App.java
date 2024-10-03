
import java.awt.EventQueue;

public class App extends GameMode {

    public static void main(String[] args) {
        Runnable runner = () -> {
            System.out.println(maisMilhionaria);
        };
        EventQueue.invokeLater(runner);
    }
}
