
import java.awt.EventQueue;

public class App {

    public static void main(String[] args) {
        Runnable runner = () -> {
            System.out.println("Hello World!");
        };
        EventQueue.invokeLater(runner);
    }
}
