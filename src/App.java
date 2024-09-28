
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class App {

    public static void main(String[] args) throws Exception {

        final String currentDir = System.getProperty("user.dir");
        final String publicDir = currentDir + File.separator + "public";
        final String filesDir = currentDir + File.separator + "public" + File.separator + "lotosorte";
        final String gGamesDir = filesDir + File.separator + "JOGOS GERADOS";
        final String gResultsDir = filesDir + File.separator + "RESULTADOS";

        // Create directories
        createDirectory(filesDir);
        createDirectory(gGamesDir);
        createDirectory(gResultsDir);

        Runnable runner;
        runner = () -> {
            // Construir a GUI na thread de Despacho de Eventos
            JFrame frame = new JFrame("LotoSorte");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);

            // Obter o tamanho da tela dividido por 2, para exibir no meio da tela
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (screenSize.width - frame.getWidth()) / 2;
            int y = (screenSize.height - frame.getHeight()) / 2;
            frame.setLocation(x, y);

            // Criamos um painel principal que conterá todos os outros componentes
            JPanel mPanel = new JPanel();
            mPanel.setLayout(new BorderLayout());

            // Criamos um painel para o menu superior
            JPanel topMenu = new JPanel();
            topMenu.setLayout(new BorderLayout());
            topMenu.setPreferredSize(new Dimension(mPanel.getWidth(), 30));
            topMenu.setBackground(Color.GRAY);

            // Criamos um painel central para o conteúdo principal
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BorderLayout());
            centerPanel.setBackground(Color.WHITE);

            // Criamos um painel inferior
            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new FlowLayout());
            bottomPanel.setBackground(Color.GRAY);
            bottomPanel.setPreferredSize(new Dimension(mPanel.getWidth(), 30));

            // Criamos uma barra lateral
            JPanel sideBar = new JPanel();
            sideBar.setLayout(new BorderLayout());
            sideBar.setPreferredSize(new Dimension(251, centerPanel.getHeight()));
            sideBar.setBackground(Color.WHITE);

            // Adicionamos os painéis ao painel principal
            mPanel.add(topMenu, BorderLayout.NORTH);
            mPanel.add(bottomPanel, BorderLayout.SOUTH);

            // Definimos tamanhos mínimos para a barra lateral e o painel central
            sideBar.setMinimumSize(new Dimension(251, 0));
            centerPanel.setMinimumSize(new Dimension(400, 0));

            mPanel.add(sideBar, BorderLayout.WEST);
            mPanel.add(centerPanel, BorderLayout.CENTER);
            frame.add(mPanel);

            // Criamos o menu superior
            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("Arquivo");
            JMenuItem saveMenuItem = new JMenuItem("Salvar");
            JMenuItem saveAsMenuItem = new JMenuItem("Salvar como");
            JMenuItem openMenuItem = new JMenuItem("Abrir");
            fileMenu.add(saveMenuItem);
            fileMenu.add(saveAsMenuItem);
            fileMenu.addSeparator();
            fileMenu.add(openMenuItem);
            menuBar.add(fileMenu);
            frame.setJMenuBar(menuBar);

            // Criamos uma barra de ferramentas no menu superior
            JToolBar toolBar = new JToolBar();
            toolBar.setFloatable(false);
            toolBar.setBackground(Color.LIGHT_GRAY);
            JButton generateGame = new JButton("Gerar Jogo");
            generateGame.setFocusable(false);
            JButton generateResult = new JButton("Gerar Resultado");
            generateResult.setFocusable(false);
            JButton configGeneration = new JButton("Configurar Geração");
            configGeneration.setFocusable(false);
            JButton manual = new JButton("Manual");
            manual.setFocusable(false);
            toolBar.addSeparator();
            toolBar.add(generateGame);
            toolBar.addSeparator();
            toolBar.add(generateResult);
            toolBar.addSeparator();
            toolBar.add(configGeneration);
            toolBar.addSeparator();
            toolBar.add(manual);
            topMenu.add(toolBar);

            // Criamos a raiz da árvore de arquivos
            DefaultMutableTreeNode generatedGamesRoot = new DefaultMutableTreeNode("Jogos Gerados");
            DefaultMutableTreeNode generatedResultsRoot = new DefaultMutableTreeNode("Resultados Gerados");

            // Criamos a árvore de arquivos
            JTree lotosorteGames = new JTree(generatedGamesRoot);
            lotosorteGames.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            JTree lotosorteResults = new JTree(generatedResultsRoot);
            lotosorteResults.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            JTabbedPane sideTabsBar = new JTabbedPane();
            sideTabsBar.setBackground(Color.WHITE);
            sideTabsBar.addTab("Jogos Gerados", new JScrollPane(lotosorteGames));
            sideTabsBar.addTab("Resultados Gerados", new JScrollPane(lotosorteResults));
            sideBar.add(sideTabsBar);

            JTabbedPane mainTabbedPane = new JTabbedPane();
            mainTabbedPane.setBackground(Color.WHITE);
            centerPanel.add(mainTabbedPane, BorderLayout.CENTER);

            // Add mouse listener here
            lotosorteGames.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) lotosorteGames.getLastSelectedPathComponent();
                        if (node != null && node.getUserObject() instanceof File file && file.isFile()) {
                            SwingUtilities.invokeLater(() -> openFileInTab(file, mainTabbedPane));
                        }
                    }
                }
            });

            // Criar janelas das opçoes da toolbar
            JPanel generateGamePanel = new JPanel();
            generateGamePanel.setLayout(new BorderLayout());
            JPanel generateResultPanel = new JPanel();
            generateResultPanel.setLayout(new BorderLayout());
            JPanel configGenerationPanel = new JPanel();
            configGenerationPanel.setLayout(new BorderLayout());
            JPanel manualPanel = new JPanel();
            manualPanel.setLayout(new BorderLayout());

            generateGamePanel.setBackground(Color.WHITE);
            //Add padding
            generateGamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            generateGamePanel.add(new JLabel("Gerar Jogo"), BorderLayout.NORTH);
            generateGamePanel.add(new JLabel("Gerar Jogo"), BorderLayout.CENTER);

            // Mostrar janelas quando o botão é clicado
            generateGame.addActionListener((ActionEvent e) -> {
                addClosableTab(mainTabbedPane, "Gerar Jogo", generateGamePanel);
                mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
            });

            generateResult.addActionListener((ActionEvent e) -> {
                addClosableTab(mainTabbedPane, "Gerar Resultado", generateResultPanel);
                mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
            });

            configGeneration.addActionListener((ActionEvent e) -> {
                addClosableTab(mainTabbedPane, "Configurar Geração", configGenerationPanel);

                mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
            });
            manual.addActionListener((ActionEvent e) -> {
                addClosableTab(mainTabbedPane, "Manual", manualPanel);
                mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
            });

            frame.setVisible(true);
        };
        EventQueue.invokeLater(runner);
    }

    private static void createDirectory(String dirPath) {
        File directory = new File(dirPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("Directory created: " + dirPath);
            } else {
                System.out.println("Failed to create directory: " + dirPath);
            }
        } else {
            System.out.println("Directory already exists: " + dirPath);
        }
    }

    private static String readFileContent(File file) {
        try {
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            return content.toString();
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    private static void addClosableTab(JTabbedPane tabbedPane, String title, JComponent component) {
        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        JButton closeButton = new JButton("X");
        closeButton.setPreferredSize(new Dimension(15, 15));
        // Make background transparent
        closeButton.setBackground(new Color(0, 0, 0, 0));
        // Change hover setting
        closeButton.setOpaque(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        // make foreground red on hover
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(Color.RED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(Color.BLACK);
            }
        });
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        tabPanel.add(titleLabel, BorderLayout.CENTER);
        tabPanel.add(closeButton, BorderLayout.EAST);

        tabbedPane.addTab(null, component);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabPanel);

        closeButton.addActionListener(e -> {
            tabbedPane.remove(component);
        });
    }

    private static void openFileInTab(File file, JTabbedPane mainTabbedPane) {
        String tabTitle = file.getName();

        // Check if the tab is already open
        for (int i = 0; i < mainTabbedPane.getTabCount(); i++) {
            if (mainTabbedPane.getTitleAt(i).equals(tabTitle)) {
                mainTabbedPane.setSelectedIndex(i);
                return;
            }
        }

        // If not open, create a new tab
        String content = readFileContent(file);
        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        addClosableTab(mainTabbedPane, tabTitle, scrollPane);
        mainTabbedPane.setSelectedIndex(mainTabbedPane.getTabCount() - 1);
    }

}
