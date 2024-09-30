
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame app = new JFrame("LotoSorte");
            app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            app.setSize(1120, 670);

            JPanel bodyMainPanel = new JPanel(new BorderLayout());
            JPanel divSideBar = new JPanel(new BorderLayout());
            JPanel divContent = new JPanel(new BorderLayout());
            JPanel contentFooter = new JPanel(new BorderLayout());
            JPanel contentHeader = new JPanel(new BorderLayout());
            JPanel contentBody = new JPanel(new BorderLayout());
            JToolBar toolBar = new JToolBar();
            JToolBar footerBar = new JToolBar();
            JButton genGameButton = new JButton("Gerar Jogo");
            JButton genResultButton = new JButton("Gerar Resultado");
            JButton readManualButton = new JButton("Ler Manual");
            JTabbedPane filesTabs = new JTabbedPane();
            JTabbedPane mainTabs = new JTabbedPane();
            JPanel logoPanel = new JPanel();
            JPanel logoHeader = new JPanel();
            JPanel gameGenPanel = new JPanel();
            JPanel resultsGenPanel = new JPanel();
            JPanel manualReadPanel = new JPanel();
            JPanel monitorAndConsoleEast = new JPanel();
            JPanel configAndCreationWest = new JPanel();
            JPanel choseGameModeArea = new JPanel();
            String[] gameModes = new String[]{"Jogo 1", "Jogo 2", "Jogo 3"};
            JComboBox<String> gameModeComboBox = new JComboBox<>(gameModes);

            JPanel monitorPanelArea = new JPanel();
            JPanel outputPanelArea = new JPanel();

            JPanel numbersPanelArea = new JPanel();

            JPanel generateGamePanel = new JPanel();
            JPanel generateGamePanelCenter = new JPanel();
            JPanel generateGamePanelEast = new JPanel();
            JTextField setAmountGames = new JTextField();
            JButton generateGameButtonInPanel = new JButton("Gerar Jogo");

            // JTree roots
            DefaultMutableTreeNode gamesFilesRoot = new DefaultMutableTreeNode("Jogos");
            DefaultMutableTreeNode resultsFilesRoot = new DefaultMutableTreeNode("Resultados");

            JTree gamesFilesTree = new JTree(gamesFilesRoot);
            JTree resultsFilesTree = new JTree(resultsFilesRoot);

            JTabbedPane monitorTabs = new JTabbedPane();
            JPanel monitorPanel = new JPanel();
            JTabbedPane outputTabs = new JTabbedPane();
            JPanel outputPanel = new JPanel();

            bodyMainPanel.setBackground(Color.WHITE);
            logoHeader.setBackground(Color.GREEN);
            logoHeader.setPreferredSize(new Dimension(1200, 80));

            gamesFilesTree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            resultsFilesTree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            filesTabs.addTab("Jogos", gamesFilesTree);
            filesTabs.addTab("Resultados", resultsFilesTree);
            mainTabs.addTab(" ", logoPanel);

            divSideBar.add(contentHeader, BorderLayout.NORTH);
            divSideBar.add(filesTabs, BorderLayout.CENTER);

            toolBar.setLayout(new GridLayout(3, 1));
            toolBar.setBackground(Color.WHITE);
            toolBar.setBorderPainted(false);
            toolBar.setPreferredSize(new Dimension(1200, 40));
            toolBar.setFloatable(false);
            toolBar.add(genGameButton);
            toolBar.add(genResultButton);
            toolBar.add(readManualButton);
            contentHeader.add(toolBar);
            contentFooter.add(footerBar);
            footerBar.setFloatable(false);

            // gameGenPanel
            gameGenPanel.setBackground(Color.WHITE);
            gameGenPanel.setLayout(new GridLayout(1, 2));
            gameGenPanel.add(configAndCreationWest);
            configAndCreationWest.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            gameGenPanel.add(monitorAndConsoleEast);
            configAndCreationWest.setBackground(Color.WHITE);
            configAndCreationWest.setLayout(new BorderLayout());
            configAndCreationWest.add(choseGameModeArea, BorderLayout.NORTH);
            choseGameModeArea.setBackground(Color.WHITE);
            choseGameModeArea.setLayout(new BorderLayout());
            choseGameModeArea.add(new JLabel("Escolha o modo de jogo:"), BorderLayout.NORTH);
            choseGameModeArea.add(gameModeComboBox, BorderLayout.CENTER);

            numbersPanelArea.setBackground(Color.WHITE);
            numbersPanelArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            numbersPanelArea.setLayout(new GridLayout(10, 10, 5, 5));
            // from 00 to 99
            for (int i = 0; i < 100; i++) {
                JButton button = new JButton(String.format("%02d", i));
                button.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
                numbersPanelArea.add(button);
                button.addActionListener((ActionEvent e) -> {
                    System.out.println("Button " + button.getText() + " clicked");
                });
            }
            generateGamePanel.setBackground(Color.WHITE);
            generateGamePanel.setLayout(new BorderLayout());
            generateGamePanel.add(generateGamePanelCenter, BorderLayout.CENTER);
            generateGamePanel.add(generateGamePanelEast, BorderLayout.EAST);
            generateGamePanelCenter.setBackground(Color.WHITE);
            generateGamePanelCenter.setLayout(new BorderLayout());
            generateGamePanelCenter.add(new JLabel("Quantidade de jogos:"), BorderLayout.NORTH);
            generateGamePanelCenter.add(setAmountGames, BorderLayout.CENTER);
            generateGamePanelCenter.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
            generateGamePanelEast.setBackground(Color.WHITE);
            generateGamePanelEast.setLayout(new BorderLayout());
            generateGamePanelEast.add(generateGameButtonInPanel, BorderLayout.CENTER);
            generateGameButtonInPanel.addActionListener((ActionEvent e) -> {
                System.out.println("Button " + generateGameButtonInPanel.getText() + " clicked");
            });

            configAndCreationWest.add(numbersPanelArea, BorderLayout.CENTER);
            configAndCreationWest.add(generateGamePanel, BorderLayout.SOUTH);

            monitorAndConsoleEast.setBackground(Color.WHITE);
            monitorAndConsoleEast.setLayout(new GridLayout(2, 1));
            monitorAndConsoleEast.add(monitorPanelArea, BorderLayout.CENTER);
            monitorAndConsoleEast.add(outputPanelArea, BorderLayout.EAST);
            monitorPanelArea.setBackground(Color.RED);
            monitorPanelArea.setLayout(new BorderLayout());
            outputPanelArea.setBackground(Color.BLUE);
            outputPanelArea.setLayout(new BorderLayout());
            monitorTabs.add(new JScrollPane(monitorPanel), "Monitor");
            monitorPanelArea.add(monitorTabs, BorderLayout.CENTER);
            outputPanelArea.add(outputTabs, BorderLayout.CENTER);
            outputTabs.add(new JScrollPane(outputPanel), "Saída");
            monitorPanel.setLayout(new BorderLayout());
            monitorPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            monitorPanel.add(new JLabel("Monitor"), BorderLayout.NORTH);

            contentBody.setBackground(Color.WHITE);
            contentBody.add(mainTabs, BorderLayout.CENTER);
            contentBody.add(logoHeader, BorderLayout.NORTH);
            contentHeader.setBackground(Color.WHITE);
            contentHeader.setPreferredSize(new Dimension(1200, 80));
            contentFooter.setBackground(Color.WHITE);
            contentFooter.setPreferredSize(new Dimension(1200, 30));

            divSideBar.setBackground(Color.WHITE);
            divSideBar.setPreferredSize(new Dimension(280, 700));
            divContent.add(contentFooter, BorderLayout.SOUTH);
            divContent.add(contentBody, BorderLayout.CENTER);
            bodyMainPanel.add(divSideBar, BorderLayout.WEST);
            bodyMainPanel.add(divContent, BorderLayout.CENTER);

            // Add action listeners to buttons
            genGameButton.addActionListener(e -> addTab(mainTabs, "Gerar Jogo", gameGenPanel));
            genResultButton.addActionListener(e -> addTab(mainTabs, "Gerar Resultado", resultsGenPanel));
            readManualButton.addActionListener(e -> addTab(mainTabs, "Ler Manual", manualReadPanel));

            app.add(bodyMainPanel);
            app.setVisible(true);
        });
    }

    private static void addTab(JTabbedPane tabbedPane, String title, Component component) {
        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.add(component, BorderLayout.CENTER);

        JButton closeButton = new JButton("X");
        closeButton.addActionListener(e -> tabbedPane.remove(tabPanel));
        closeButton.setFocusable(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));

        JPanel tabTitlePanel = new JPanel(new BorderLayout());
        tabTitlePanel.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        tabTitlePanel.add(label, BorderLayout.CENTER);
        tabTitlePanel.add(closeButton, BorderLayout.EAST);

        tabbedPane.addTab(null, tabPanel);
        tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(tabPanel), tabTitlePanel);
        tabbedPane.setSelectedComponent(tabPanel);
    }
}
