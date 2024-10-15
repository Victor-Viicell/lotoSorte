
import java.awt.*;
import javax.swing.*;

public class App {

    public static void main(String[] args) {

        Runnable runner = () -> {
            JFrame frame = mainFrame();
            JPanel panel = mainPanel(frame);
            leftPanel(panel);
            rightPanel(panel);

        };
        EventQueue.invokeLater(runner);
    }

    private static JFrame mainFrame() {
        JFrame frame = new JFrame("LotoSorte");
        frame.setSize(1120, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        return frame;
    }

    private static JPanel mainPanel(JFrame parent) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        parent.add(panel, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel leftPanel(JPanel parent) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        parent.add(panel, BorderLayout.WEST);
        panel.setPreferredSize(new Dimension(260, 100));
        explorerPanel(panel);
        toolButtonsPanel(panel);
        return panel;
    }

    private static JPanel explorerPanel(JPanel parent) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        parent.add(panel, BorderLayout.CENTER);
        panel.add(explorerTabbedPane(panel), BorderLayout.CENTER);
        return panel;
    }

    private static JTabbedPane explorerTabbedPane(JPanel parent) {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        parent.add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab("Games", gamesExplorerTab());
        return tabbedPane;
    }

    private static JPanel gamesExplorerTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(gamesExplorerScrollPane(), BorderLayout.CENTER);

        return panel;
    }

    private static JScrollPane gamesExplorerScrollPane() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setViewportView(gamesExplorerTree());
        return scrollPane;
    }

    private static JTree gamesExplorerTree() {
        JTree tree = new JTree();
        tree.setRootVisible(false);
        tree.setBackground(Color.WHITE);
        return tree;
    }

    private static JPanel toolButtonsPanel(JPanel parent) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        parent.add(panel, BorderLayout.SOUTH);
        panel.setPreferredSize(new Dimension(260, 150));
        panel.add(toolBar(), BorderLayout.CENTER);
        return panel;
    }

    private static JToolBar toolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setLayout(new GridLayout(3, 1, 10, 10));
        toolBar.setBackground(Color.WHITE);
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setBorderPainted(true);
        toolBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        toolBar.add(toolBarButton("New"));
        toolBar.add(toolBarButton("Open"));
        toolBar.add(toolBarButton("Save"));
        return toolBar;
    }

    private static JButton toolBarButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.GRAY);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return button;
    }

    private static JPanel rightPanel(JPanel parent) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        parent.add(panel, BorderLayout.CENTER);
        mainTabbedPane(panel);
        return panel;
    }

    private static JTabbedPane mainTabbedPane(JPanel parent) {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);
        parent.add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab(" ", defTab());
        return tabbedPane;
    }

    private static JPanel defTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        return panel;
    }

}
