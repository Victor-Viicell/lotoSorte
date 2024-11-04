package lotoSorte;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {

    private Image icon;

    // Get Executer directiory
    public static final String INSTALLATION_PATH = getInstallationPath();
    public static final String SRC_FOLDER_PATH_GAMES = INSTALLATION_PATH + "Games" + File.separator;
    public static final String SRC_FOLDER_PATH_RESULTS = INSTALLATION_PATH + "Results" + File.separator;

// Add this method to handle path resolution
    private static String getInstallationPath() {
        try {
            // Get the location of the running executable/jar
            String executablePath = new File(App.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getPath();

            // Handle Windows path format
            if (executablePath.startsWith("/")) {
                executablePath = executablePath.substring(1);
            }

            // Get parent directory (Program Files (x86)\LotoSorte)
            File installDir = new File(executablePath).getParentFile();
            String installPath = installDir.getAbsolutePath() + File.separator;

            // Create Games and Results directories
            File gamesDir = new File(installPath + "Games");
            File resultsDir = new File(installPath + "Results");

            gamesDir.mkdirs();
            resultsDir.mkdirs();

            return installPath;

        } catch (URISyntaxException e) {
            // Fallback to Program Files if something goes wrong
            String fallbackPath = "C:" + File.separator + "Program Files (x86)"
                    + File.separator + "LotoSorte" + File.separator;

            // Create directories in fallback location
            new File(fallbackPath + "Games").mkdirs();
            new File(fallbackPath + "Results").mkdirs();

            return fallbackPath;
        }
    }

    private ChoiceBox<String> choiceBox;
    private ChoiceBox<String> numberAmount;
    private VBox rightPane;
    private GridPane trevos;
    private GridPane monthsButtons;
    private GameMode currentGameMode;
    public static GameMode[] gameModes = {
        new GameMode("+Milionária", 50, 6, 12, true, false, 6.00f),
        new GameMode("Mega-Sena", 60, 6, 20, true, false, 5.00f),
        new GameMode("Lotofácil", 25, 15, 20, true, false, 3.00f),
        new GameMode("Quina", 80, 5, 15, true, false, 2.50f),
        new GameMode("Lotomania", 100, 50, 50, true, true, 3.00f),
        new GameMode("Dupla Sena", 50, 6, 15, true, false, 2.50f),
        new GameMode("Dia de Sorte", 31, 7, 15, true, false, 2.50f),
        new GameMode("Super Sete", 10, 7, 21, false, true, 2.50f)
    };
    private int currentNumberAmountValue;
    private VBox jogosButtonsContainer;
    private VBox resultsButtonsContainer;
    private Result result;
    private String[] selectedNumbers = new String[0];
    private String[] selectedTrevos = new String[0];
    private String luckyMonth = "";

    private Game loadedGame;
    private Result loadedResult;
    private static final Logger logger = Logger.getLogger(App.class.getName());

    // Get system window size
    public static double width = Screen.getPrimary().getBounds().getWidth();
    public static double height = Screen.getPrimary().getBounds().getHeight();
    private GridPane genGameNumbers; // For GenGame tab
    private GridPane genResultNumbers; // For GenResult tab
    private GridPane specialGrid;
    private GridPane numbersGrid;

    Stage window;

    // Make mainTabPane an instance variable
    private TabPane mainTabPane;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Platform.runLater(() -> {
            icon = new Image(getClass().getResourceAsStream("/icon.png"));
            // Add console handler to logger
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.FINE);
            logger.addHandler(consoleHandler);
            logger.setLevel(Level.FINE);

            window = stage;

            // Initialize mainTabPane
            mainTabPane = new TabPane();
            initializeMainTabPane();

            VBox sideMenu = sideMenu();

            BorderPane layout = new BorderPane();
            layout.setLeft(sideMenu);
            layout.setCenter(mainTabPane);
            layout.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

            Scene scene = new Scene(layout);
            setStage(scene, window);
        });
    }

    private Button createOpenPDFButton() {
        Button openPDFButton = new Button("Abrir Guia PDF");
        openPDFButton.setMaxWidth(Double.MAX_VALUE);
        openPDFButton.setOnAction(e -> {
            File pdfFile = new File(INSTALLATION_PATH + "Guia.pdf");
            if (!pdfFile.exists()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Arquivo não encontrado");
                alert.setHeaderText("Guia PDF não encontrado");
                alert.setContentText("O arquivo Guia.pdf não foi encontrado em: " + pdfFile.getAbsolutePath());
                alert.showAndWait();
                return;
            }
            try {
                Desktop.getDesktop().open(pdfFile);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error opening PDF: " + ex.getMessage());
            }
        });
        return openPDFButton;
    }

    private void initializeMainTabPane() {
        mainTabPane = new TabPane();
        Tab welcomeTab = new Tab("");
        welcomeTab.setClosable(false);

        // Create centered label with bold text
        Label welcomeLabel = new Label("LOTOSORTE");
        welcomeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 124px; -fx-text-fill: #f0f0f0;");

        // Create container for centering
        StackPane centerPane = new StackPane(welcomeLabel);
        centerPane.setPrefWidth(width);
        centerPane.setPrefHeight(height);

        welcomeTab.setContent(centerPane);
        mainTabPane.getTabs().add(welcomeTab);
    }

    private void exportResultToPDF(Result resultFile) {
        logger.log(Level.INFO, "Starting PDF export for result: {0}", resultFile.game.gameMode.name);
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF File");
            String userHome = System.getProperty("user.home");
            fileChooser.setInitialDirectory(new File(userHome + "/Downloads"));
            String generationDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String defaultFileName = resultFile.game.gameMode.name + "_Resultado_" + generationDate + ".pdf";
            fileChooser.setInitialFileName(defaultFileName);
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
            fileChooser.getExtensionFilters().add(extFilter);

            File file = fileChooser.showSaveDialog(window);
            if (file != null) {
                PdfWriter writer = new PdfWriter(file);
                com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);

                try (Document document = new Document(pdf)) {
                    if (resultFile.game.gameMode.name.equals("Super Sete")) {
                        // add "|" before each number
                        String[] numbers = new String[resultFile.championNumbers.length * 2];
                        numbers[0] = "|";
                        for (int i = 0; i < resultFile.championNumbers.length; i++) {
                            numbers[i * 2 + 1] = resultFile.championNumbers[i];  // Put number at odd indices
                            if (i < resultFile.championNumbers.length - 1) {
                                numbers[i * 2 + 2] = "|";                        // Put separator at even indices
                            }
                        }
                        resultFile.championNumbers = numbers;
                        Table numbersSection = new Table(new float[]{1});
                        numbersSection.setWidth(500);
                        Cell numbersHeader = new Cell()
                                .add(new Paragraph("Números Sorteados"))
                                .setFontSize(16)
                                .setBold()
                                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                .setPadding(5);
                        numbersSection.addCell(numbersHeader);

                        StringBuilder numbersStr = new StringBuilder();
                        int column = 1;
                        for (String number : resultFile.championNumbers) {
                            if (number.equals("|")) {
                                logger.log(Level.FINE, "Coluna {0}: {1}", new Object[]{column, numbersStr.toString().trim()});
                                numbersStr.append("\nColuna ").append(column++).append(": ");
                            } else {
                                logger.log(Level.FINE, "Número: {0}", number);
                                numbersStr.append(number).append(" | ");
                            }
                        }
                        numbersSection.addCell(new Cell().add(new Paragraph(numbersStr.toString().trim())).setMarginLeft(20));
                        document.add(numbersSection);
                    } else {
                        // Original code for other game modes
                        Table numbersSection = new Table(new float[]{1});
                        numbersSection.setWidth(500);
                        Cell numbersHeader = new Cell()
                                .add(new Paragraph("Números Sorteados"))
                                .setFontSize(16)
                                .setBold()
                                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                .setPadding(5);
                        numbersSection.addCell(numbersHeader);

                        StringBuilder numbersStr = new StringBuilder();
                        for (String number : resultFile.championNumbers) {
                            numbersStr.append(number).append(" | ");
                        }
                        numbersSection.addCell(new Cell().add(new Paragraph(numbersStr.toString().trim())).setMarginLeft(20));
                        document.add(numbersSection);
                    }
                    // Add game-specific sections based on game mode
                    switch (resultFile.game.gameMode.name) {
                        case "+Milionária" ->
                            addMaisMilionariaSection(document, resultFile);
                        case "Mega-Sena" ->
                            addMegaSenaSection(document, resultFile);
                        case "Lotofácil" ->
                            addLotofacilSection(document, resultFile);
                        case "Quina" ->
                            addQuinaSection(document, resultFile);
                        case "Lotomania" ->
                            addLotomaniaSection(document, resultFile);
                        case "Dupla Sena" ->
                            addDuplaSenaSection(document, resultFile);
                        case "Dia de Sorte" ->
                            addDiaDeSorteSection(document, resultFile);
                        case "Super Sete" ->
                            addSuperSeteSection(document, resultFile);
                    }
                }

                Desktop.getDesktop().open(file);
                logger.log(Level.INFO, "Result PDF generated successfully at: {0}", file.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error generating PDF: {0}", e.getMessage());
            logger.throwing("App", "exportResultToPDF", e);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro na Exportação");
            alert.setHeaderText("Erro ao gerar PDF");
            alert.setContentText("Ocorreu um erro ao gerar o arquivo PDF: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void addMaisMilionariaSection(Document document, Result result) throws IOException {
        addPrizeSection(document, "Faixa 1 (6 números + 2 trevos)", result.maisMilionaria.faixa1);
        addPrizeSection(document, "Faixa 2 (6 números + 1 ou 0 trevos)", result.maisMilionaria.faixa2);
        addPrizeSection(document, "Faixa 3 (5 números + 2 trevos)", result.maisMilionaria.faixa3);
        addPrizeSection(document, "Faixa 4 (5 números + 1 ou 0 trevos)", result.maisMilionaria.faixa4);
        addPrizeSection(document, "Faixa 5 (4 números + 2 trevos)", result.maisMilionaria.faixa5);
        addPrizeSection(document, "Faixa 6 (4 números + 1 ou 0 trevos)", result.maisMilionaria.faixa6);
        addPrizeSection(document, "Faixa 7 (3 números + 2 trevos)", result.maisMilionaria.faixa7);
        addPrizeSection(document, "Faixa 8 (3 números + 1 trevo)", result.maisMilionaria.faixa8);
        addPrizeSection(document, "Faixa 9 (2 números + 2 trevos)", result.maisMilionaria.faixa9);
        addPrizeSection(document, "Faixa 10 (2 números + 1 trevo)", result.maisMilionaria.faixa10);
    }

    private void addMegaSenaSection(Document document, Result result) throws IOException {
        addPrizeSection(document, "Faixa 1 (Sena)", result.megaSena.faixa1);
        addPrizeSection(document, "Faixa 2 (Quina)", result.megaSena.faixa2);
        addPrizeSection(document, "Faixa 3 (Quadra)", result.megaSena.faixa3);
    }

    private void addLotofacilSection(Document document, Result result) throws IOException {
        addPrizeSection(document, "Faixa 1 (15 acertos)", result.lotoFacil.faixa1);
        addPrizeSection(document, "Faixa 2 (14 acertos)", result.lotoFacil.faixa2);
        addPrizeSection(document, "Faixa 3 (13 acertos)", result.lotoFacil.faixa3);
        addPrizeSection(document, "Faixa 4 (12 acertos)", result.lotoFacil.faixa4);
        addPrizeSection(document, "Faixa 5 (11 acertos)", result.lotoFacil.faixa5);
    }

    private void addQuinaSection(Document document, Result result) throws IOException {
        addPrizeSection(document, "Faixa 1 (Quina)", result.quina.faixa1);
        addPrizeSection(document, "Faixa 2 (Quadra)", result.quina.faixa2);
        addPrizeSection(document, "Faixa 3 (Terno)", result.quina.faixa3);
        addPrizeSection(document, "Faixa 4 (Duque)", result.quina.faixa4);
    }

    private void addLotomaniaSection(Document document, Result result) throws IOException {
        addPrizeSection(document, "Faixa 1 (20 acertos)", result.lotoMania.faixa1);
        addPrizeSection(document, "Faixa 2 (19 acertos)", result.lotoMania.faixa2);
        addPrizeSection(document, "Faixa 3 (18 acertos)", result.lotoMania.faixa3);
        addPrizeSection(document, "Faixa 4 (17 acertos)", result.lotoMania.faixa4);
        addPrizeSection(document, "Faixa 5 (16 acertos)", result.lotoMania.faixa5);
        addPrizeSection(document, "Faixa 6 (15 acertos)", result.lotoMania.faixa6);
        addPrizeSection(document, "Faixa 7 (0 acertos)", result.lotoMania.faixa7);
    }

    private void addDuplaSenaSection(Document document, Result result) throws IOException {
        addPrizeSection(document, "Faixa 1 (6 acertos)", result.duplaSena.faixa1);
        addPrizeSection(document, "Faixa 2 (5 acertos)", result.duplaSena.faixa2);
        addPrizeSection(document, "Faixa 3 (4 acertos)", result.duplaSena.faixa3);
        addPrizeSection(document, "Faixa 4 (3 acertos)", result.duplaSena.faixa4);
    }

    private void addDiaDeSorteSection(Document document, Result result) throws IOException {
        addPrizeSection(document, "Faixa 1 (7 números + Mês)", result.diaDeSorte.faixa1);
        addPrizeSection(document, "Faixa 2 (6 números + Mês)", result.diaDeSorte.faixa2);
        addPrizeSection(document, "Faixa 3 (5 números + Mês)", result.diaDeSorte.faixa3);
        addPrizeSection(document, "Faixa 4 (4 números + Mês)", result.diaDeSorte.faixa4);
        addPrizeSection(document, "Faixa 5 (Mês da Sorte)", result.diaDeSorte.faixa5);
    }

    private void addSuperSeteSection(Document document, Result result) throws IOException {
        addPrizeSection(document, "Faixa 1 (7 colunas)", result.superSete.faixa1);
        addPrizeSection(document, "Faixa 2 (6 colunas)", result.superSete.faixa2);
        addPrizeSection(document, "Faixa 3 (5 colunas)", result.superSete.faixa3);
        addPrizeSection(document, "Faixa 4 (4 colunas)", result.superSete.faixa4);
        addPrizeSection(document, "Faixa 5 (3 colunas)", result.superSete.faixa5);
    }

    private void addPrizeSection(Document document, String title, Object prizeData) throws IOException {
        if (prizeData == null) {
            return;
        }

        Table section = new Table(new float[]{1});
        section.setWidth(500);
        section.setMarginTop(10);

        // Get the number of games in the faixa
        int gamesCount = 0;
        switch (prizeData) {
            case Result.MMilionaria[] games ->
                gamesCount = games.length;
            case Result.DSorte[] games ->
                gamesCount = games.length;
            case Result.BaseGame[] games ->
                gamesCount = games.length;
            default -> {
            }
        }

        Cell header = new Cell()
                .add(new Paragraph(title + " (" + gamesCount + " jogos)"))
                .setFontSize(14)
                .setBold()
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(5);
        section.addCell(header);

        switch (prizeData) {
            case Result.MMilionaria[] games -> {
                for (int i = 0; i < games.length; i++) {
                    Cell gameHeader = new Cell()
                            .add(new Paragraph("Jogo " + (i + 1)))
                            .setFontSize(12)
                            .setBold()
                            .setBackgroundColor(ColorConstants.GRAY)
                            .setPadding(3);
                    section.addCell(gameHeader);
                    addGameToSection(section, games[i].numbers, games[i].trevos);
                }
            }
            case Result.DSorte[] games -> {
                for (int i = 0; i < games.length; i++) {
                    Cell gameHeader = new Cell()
                            .add(new Paragraph("Jogo " + (i + 1)))
                            .setFontSize(12)
                            .setBold()
                            .setBackgroundColor(ColorConstants.GRAY)
                            .setPadding(3);
                    section.addCell(gameHeader);
                    addGameToSection(section, games[i].numbers, new String[]{games[i].month});
                }
            }
            case Result.BaseGame[] games -> {
                for (int i = 0; i < games.length; i++) {
                    Cell gameHeader = new Cell()
                            .add(new Paragraph("Jogo " + (i + 1)))
                            .setFontSize(12)
                            .setBold()
                            .setBackgroundColor(ColorConstants.GRAY)
                            .setPadding(3);
                    section.addCell(gameHeader);
                    addGameToSection(section, games[i].numbers, null);
                }
            }
            default -> {
            }
        }

        document.add(section);
    }

    private void addGameToSection(Table section, String[] numbers, String[] extras) {
        if (numbers == null) {
            return;
        }

        StringBuilder gameStr = new StringBuilder();
        if (extras != null && extras.length > 0) {
            // Handle numbers
            for (String number : numbers) {
                if (!number.equals("|")) {
                    gameStr.append(number).append(" | ");
                }
            }
            Cell gameCell = new Cell().add(new Paragraph(gameStr.toString().trim()));
            section.addCell(gameCell);

            // Handle extras based on type
            Cell extrasCell = new Cell();
            if (extras[0].matches("\\d+")) { // Trevos contain only digits
                extrasCell.add(new Paragraph("Trevos: " + String.join(" | ", extras)));
            } else { // Month contains text
                extrasCell.add(new Paragraph("Mês: " + String.join(" | ", extras)));
            }
            extrasCell.setFontSize(10).setItalic();
            section.addCell(extrasCell);
        } else {
            // Existing code for Super Sete and regular games
            if (numbers.length > 7) {
                int column = 1;
                for (String number : numbers) {
                    if (number.equals("|")) {
                        gameStr.append("\nColuna ").append(column++).append(": ");
                    } else {
                        gameStr.append(number).append(" | ");
                    }
                }
            } else {
                for (String number : numbers) {
                    if (!number.equals("|")) {
                        gameStr.append(number).append(" | ");
                    }
                }
            }
            Cell gameCell = new Cell().add(new Paragraph(gameStr.toString().trim()));
            section.addCell(gameCell);
        }
    }

    private void exportGameToPDF(Game gameFile) {
        logger.log(Level.INFO, "Starting PDF export for game: {0}", gameFile.gameMode.name);
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF File");
            String userHome = System.getProperty("user.home");
            fileChooser.setInitialDirectory(new File(userHome + "/Downloads"));
            String generationDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String defaultFileName = gameFile.gameMode.name + "_Bolao_" + generationDate + ".pdf";
            fileChooser.setInitialFileName(defaultFileName);
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
            fileChooser.getExtensionFilters().add(extFilter);

            File file = fileChooser.showSaveDialog(window);
            if (file != null) {
                PdfWriter writer = new PdfWriter(file);
                com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);

                try (Document document = new Document(pdf)) {
                    // Main title with fixed width
                    Table mainTitle = new Table(new float[]{1});
                    mainTitle.setWidth(500);
                    Cell titleCell = new Cell()
                            .add(new Paragraph("Jogo: " + gameFile.gameMode.name))
                            .setFontSize(24)
                            .setBold()
                            .setTextAlignment(TextAlignment.CENTER);
                    mainTitle.addCell(titleCell);
                    document.add(mainTitle);

                    // Game details section with fixed width
                    Table detailsSection = new Table(new float[]{1});
                    detailsSection.setWidth(500);
                    Cell detailsHeader = new Cell()
                            .add(new Paragraph("Detalhes do Jogo"))
                            .setFontSize(16)
                            .setBold()
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                            .setPadding(5);
                    detailsSection.addCell(detailsHeader);
                    detailsSection.addCell(new Cell().add(new Paragraph("Quantidade de Números: " + gameFile.numbers)).setMarginLeft(20));
                    detailsSection.addCell(new Cell().add(new Paragraph("Quantidade de Jogos: " + gameFile.amount)).setMarginLeft(20));
                    detailsSection.addCell(new Cell().add(new Paragraph("Custo Total: " + gameFile.totalCost)).setMarginLeft(20));
                    document.add(detailsSection);

                    // Generated games section with fixed width
                    Table gamesSection = new Table(new float[]{1});
                    gamesSection.setWidth(500);
                    Cell gamesHeader = new Cell()
                            .add(new Paragraph("Jogos Gerados"))
                            .setFontSize(16)
                            .setBold()
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                            .setPadding(5);
                    gamesSection.addCell(gamesHeader);
                    document.add(gamesSection);

                    for (int i = 0; i < gameFile.games.length; i++) {
                        Table gameTable = new Table(new float[]{1});
                        gameTable.setWidth(500);
                        gameTable.setMarginTop(10);

                        Cell numberCell = new Cell().add(new Paragraph("Jogo " + (i + 1)))
                                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                .setBold();

                        Cell contentCell;
                        if (gameFile.gameMode.name.equals("Super Sete")) {
                            StringBuilder gameStr = new StringBuilder();
                            int column = 1;
                            for (String number : gameFile.games[i]) {
                                if (number.equals("|")) {
                                    gameStr.append("\nColuna ").append(column++).append(": ");
                                } else {
                                    gameStr.append(number).append(" | ");
                                }
                            }
                            contentCell = new Cell().add(new Paragraph(gameStr.toString().trim()));
                        } else {
                            StringBuilder gameStr = new StringBuilder();
                            for (String number : gameFile.games[i]) {
                                if (!number.equals("|")) {
                                    gameStr.append(number).append(" | ");
                                }
                            }
                            contentCell = new Cell().add(new Paragraph(gameStr.toString().trim()));
                        }

                        gameTable.addCell(numberCell);
                        gameTable.addCell(contentCell);
                        document.add(gameTable);

                        // Enhanced display for special elements
                        if (gameFile.gameMode.name.equals("+Milionária")) {
                            Table trevosTable = new Table(new float[]{1});
                            trevosTable.setWidth(500);
                            Cell trevosLabel = new Cell().add(new Paragraph("Trevos"))
                                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                    .setFontSize(10)
                                    .setBold();
                            Cell trevosContent = new Cell().add(new Paragraph(String.join(" | ", gameFile.maisMilionaria.trevos[i]))
                                    .setFontSize(10)
                                    .setItalic());
                            trevosTable.addCell(trevosLabel);
                            trevosTable.addCell(trevosContent);
                            document.add(trevosTable);
                        } else if (gameFile.gameMode.name.equals("Dia de Sorte")) {
                            Table monthTable = new Table(new float[]{1});
                            monthTable.setWidth(500);
                            Cell monthLabel = new Cell().add(new Paragraph("Mês"))
                                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                    .setFontSize(10)
                                    .setBold();
                            Cell monthContent = new Cell().add(new Paragraph(gameFile.diaDeSorte.month[i])
                                    .setFontSize(10)
                                    .setItalic());
                            monthTable.addCell(monthLabel);
                            monthTable.addCell(monthContent);
                            document.add(monthTable);
                        }
                    }
                }

                Desktop.getDesktop().open(file);
                logger.log(Level.INFO, "PDF generated successfully at: {0}", file.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.severe("Error generating PDF: " + e.getMessage());
            logger.throwing("App", "exportGameToPDF", e);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro na Exportação");
            alert.setHeaderText("Erro ao gerar PDF");
            alert.setContentText("Ocorreu um erro ao gerar o arquivo PDF: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private VBox sideMenu() {
        VBox sideMenu = new VBox();
        sideMenu.setPrefWidth(320);
        sideMenu.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        TabPane sideTabPane = new TabPane();

        // Creating tabs for the sideTabPane
        Tab tabGenGame = new Tab("Jogos");
        jogosFilesTab(tabGenGame);
        Tab tabGenResults = new Tab("Resultados");
        resultsFilesTab(tabGenResults);

        // Adding the tabs to sideTabPane
        sideTabPane.getTabs().addAll(tabGenGame, tabGenResults);
        // Grow vertically
        sideTabPane.setPrefHeight(height);

        // Create buttons
        Button button1 = new Button("Gerar Jogo");
        button1.setPrefWidth(width);

        Button button2 = new Button("Gerar Resultado");
        button2.setPrefWidth(width);

        // Add action handlers to buttons
        button1.setOnAction(e -> openTab("Gerar Jogo"));
        button2.setOnAction(e -> openTab("Gerar Resultado"));

        sideMenu.getChildren().addAll(sideTabPane, button1, button2, createOpenPDFButton());
        return sideMenu;
    }

    private void addConfigLabel(VBox gameContent, String labelName, String labelValue) {
        HBox labelContainer = new HBox(5);
        Button label = new Button(labelName);
        label.setPrefWidth(200);
        label.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER_LEFT;");
        Button labelVal = new Button(labelValue);
        labelVal.setPrefWidth(200);
        labelVal.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER_LEFT;");
        labelContainer.getChildren().addAll(label, labelVal);
        gameContent.getChildren().add(labelContainer);
    }

    private void addConfigLabel(VBox gameContent, String labelName, String labelValue, String labelValue2, boolean type) {
        HBox labelContainer = new HBox(5);
        Button label = new Button(labelName);
        label.setPrefWidth(200);
        label.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER_LEFT;");
        Button labelVal = new Button(labelValue);
        labelVal.setPrefWidth(100);
        labelVal.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER_LEFT;");
        Button labelVal2 = new Button(labelValue2);
        labelVal2.setPrefWidth(100);
        labelVal2.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER_LEFT;");
        Button isComposite = new Button("Composto");
        isComposite.setPrefWidth(100);
        isComposite.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER_LEFT;");
        Button isPrime = new Button("Primo");
        isPrime.setPrefWidth(100);
        isPrime.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER_LEFT;");
        Button none = new Button("Nenhum");
        none.setPrefWidth(100);
        none.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER_LEFT;");
        labelContainer.getChildren().addAll(label, labelVal, labelVal2);

        if (type) {
            if (labelName.length() == 2 && labelName.charAt(0) == '0' && labelName.charAt(1) == '0') {
                labelName = labelName.substring(1);
            }
            int number = Integer.parseInt(labelName);
            if (Game.Data.isComposite(number)) {
                labelContainer.getChildren().add(isComposite);
                label.setPrefWidth(100);
            } else if (Game.Data.isPrime(number)) {
                labelContainer.getChildren().add(isPrime);
                label.setPrefWidth(100);
            } else if (number == 0 || number == 1) {
                labelContainer.getChildren().add(none);
                label.setPrefWidth(100);
            }
        }
        gameContent.getChildren().add(labelContainer);
    }

    private void loadResultTab(File resultFile) {
        String resultName = resultFile.getName();
        if (isTabOpen(resultName)) {
            return;
        }
        try {
            loadedResult = Result.loadFromFile(resultFile.getAbsolutePath());
            Tab resultTab = new Tab(resultFile.getName().split("_")[0] + " | "
                    + resultFile.getName().split("_")[1].split("-")[2] + "/"
                    + resultFile.getName().split("_")[1].split("-")[1] + "/"
                    + resultFile.getName().split("_")[1].split("-")[0] + " | "
                    + resultFile.getName().split("_")[2].split("-")[0] + ":"
                    + resultFile.getName().split("_")[2].split("-")[1] + ":"
                    + resultFile.getName().split("_")[2].split("-")[2].replace(".json", ""));
            resultTab.setClosable(true);
            loadedResult = Result.loadFromFile(resultFile.getAbsolutePath());
            System.out.println("\n=== Result Debug ===");
            System.out.println("Loaded Result: " + resultFile.getName());
            System.out.println("Game Mode: " + loadedResult.game.gameMode.name);
            System.out.println("Champion Numbers: " + Arrays.toString(loadedResult.championNumbers));
            if (loadedResult.championTrevos != null && loadedResult.championTrevos.length > 0) {
                System.out.println("Champion Trevos: " + Arrays.toString(loadedResult.championTrevos));
            }
            if (loadedResult.luckyMonth != null && !loadedResult.luckyMonth.isEmpty()) {
                System.out.println("Lucky Month: " + loadedResult.luckyMonth);
            }
            System.out.println("================\n");

            // Main container
            GridPane resultTabPane = new GridPane();
            resultTabPane.setPrefWidth(width);
            resultTabPane.setPrefHeight(height);
            resultTabPane.setMaxHeight(Double.MAX_VALUE);

            // Left side - result content with pagination
            VBox leftPane = new VBox(5);
            leftPane.setPrefWidth(width * 0.6);
            leftPane.setPrefHeight(height);
            leftPane.setMaxHeight(Double.MAX_VALUE);
            leftPane.setPadding(new javafx.geometry.Insets(10));
            leftPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

            Button warningLabel = new Button("Por favor, selecione uma faixa de premiação");
            warningLabel.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER; -fx-font-size: 16px;");
            warningLabel.setPrefWidth(width * 0.6);
            leftPane.getChildren().add(warningLabel);

            // Right side - combined data and faixas
            VBox rightPaneData = new VBox(5);
            rightPaneData.setPrefWidth(width * 0.4);
            rightPaneData.setPrefHeight(height);
            rightPaneData.setMaxHeight(Double.MAX_VALUE);
            rightPaneData.setPadding(new javafx.geometry.Insets(10));
            rightPaneData.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

            // Add champion numbers section
            Button championNumbersLabel = new Button("Números Premiados");
            championNumbersLabel.setPrefWidth(width);
            championNumbersLabel.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");

            GridPane numbersGridResult = new GridPane();
            displayChampionNumbers(numbersGridResult, loadedResult);

            rightPaneData.getChildren().addAll(championNumbersLabel, numbersGridResult);

            if (loadedResult.game.gameMode.name.equals("Super Sete")) {
                // Display Super Sete format
                for (int col = 0; col < 7; col++) {
                    Button columnLabel = new Button("C " + (col + 1));
                    columnLabel.setDisable(true);
                    columnLabel.setPrefWidth(80);
                    columnLabel.setStyle("-fx-background-color: #000000; -fx-text-fill: #ffffff;");
                    numbersGridResult.add(columnLabel, col, 0);

                    Button numberButton = new Button(loadedResult.championNumbers[col]);
                    numberButton.setPrefWidth(80);
                    numbersGridResult.add(numberButton, col, 1);
                }
            } else {
                // Display standard format with separators and labels
                Button numberLabel = new Button("Números");
                numberLabel.setPrefWidth(width);
                numberLabel.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");

                int columns = 5;
                int rows = (int) Math.ceil((double) loadedResult.championNumbers.length / columns);

                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < columns; col++) {
                        int index = row * columns + col;
                        if (index < loadedResult.championNumbers.length) {
                            Button numberButton = new Button(loadedResult.championNumbers[index]);
                            numberButton.setPrefWidth(40);
                            numbersGridResult.add(numberButton, col, row);
                        }
                    }
                }
            }
            numbersGridResult.setHgap(5);
            numbersGridResult.setVgap(5);

            // Add special elements if needed (trevos, month)
            addSpecialElements(rightPaneData, loadedResult);

            // Add separator
            Separator separator = new Separator();
            separator.setPrefWidth(width);
            rightPaneData.getChildren().add(separator);

            // Add faixas section
            Button faixasLabel = new Button("Faixas de Premiação");
            faixasLabel.setPrefWidth(width);
            faixasLabel.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");
            rightPaneData.getChildren().add(faixasLabel);

            // Add faixas buttons based on game mode
            switch (loadedResult.game.gameMode.name) {
                case "+Milionária" -> {
                    addFaixaButton(rightPaneData, "Faixa 1 - 6 números + 2 trevos", loadedResult.maisMilionaria.faixa1, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 2 - 6 números + 1 ou 0 trevos", loadedResult.maisMilionaria.faixa2, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 3 - 5 números + 2 trevos", loadedResult.maisMilionaria.faixa3, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 4 - 5 números + 1 ou 0 trevos", loadedResult.maisMilionaria.faixa4, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 5 - 4 números + 2 trevos", loadedResult.maisMilionaria.faixa5, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 6 - 4 números + 1 ou 0 trevos", loadedResult.maisMilionaria.faixa6, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 7 - 3 números + 2 trevos", loadedResult.maisMilionaria.faixa7, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 8 - 3 números + 1 trevo", loadedResult.maisMilionaria.faixa8, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 9 - 2 números + 2 trevos", loadedResult.maisMilionaria.faixa9, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 10 - 2 números + 1 trevo", loadedResult.maisMilionaria.faixa10, leftPane, loadedResult);
                }
                case "Mega-Sena" -> {
                    addFaixaButton(rightPaneData, "Faixa 1 - Sena", loadedResult.megaSena.faixa1, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 2 - Quina", loadedResult.megaSena.faixa2, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 3 - Quadra", loadedResult.megaSena.faixa3, leftPane, loadedResult);
                }
                case "Lotofácil" -> {
                    addFaixaButton(rightPaneData, "Faixa 1 - 15 acertos", loadedResult.lotoFacil.faixa1, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 2 - 14 acertos", loadedResult.lotoFacil.faixa2, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 3 - 13 acertos", loadedResult.lotoFacil.faixa3, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 4 - 12 acertos", loadedResult.lotoFacil.faixa4, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 5 - 11 acertos", loadedResult.lotoFacil.faixa5, leftPane, loadedResult);
                }
                case "Quina" -> {
                    addFaixaButton(rightPaneData, "Faixa 1 - Quina", loadedResult.quina.faixa1, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 2 - Quadra", loadedResult.quina.faixa2, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 3 - Terno", loadedResult.quina.faixa3, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 4 - Duque", loadedResult.quina.faixa4, leftPane, loadedResult);
                }
                case "Lotomania" -> {
                    addFaixaButton(rightPaneData, "Faixa 1 - 20 acertos", loadedResult.lotoMania.faixa1, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 2 - 19 acertos", loadedResult.lotoMania.faixa2, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 3 - 18 acertos", loadedResult.lotoMania.faixa3, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 4 - 17 acertos", loadedResult.lotoMania.faixa4, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 5 - 16 acertos", loadedResult.lotoMania.faixa5, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 6 - 15 acertos", loadedResult.lotoMania.faixa6, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 7 - 0 acertos", loadedResult.lotoMania.faixa7, leftPane, loadedResult);
                }
                case "Dupla Sena" -> {
                    addFaixaButton(rightPaneData, "Faixa 1 - Sena", loadedResult.duplaSena.faixa1, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 2 - Quina", loadedResult.duplaSena.faixa2, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 3 - Quadra", loadedResult.duplaSena.faixa3, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 4 - Terno", loadedResult.duplaSena.faixa4, leftPane, loadedResult);
                }
                case "Dia de Sorte" -> {
                    addFaixaButton(rightPaneData, "Faixa 1 - 7 acertos + Mês", loadedResult.diaDeSorte.faixa1, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 2 - 6 acertos + Mês", loadedResult.diaDeSorte.faixa2, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 3 - 5 acertos + Mês", loadedResult.diaDeSorte.faixa3, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 4 - 4 acertos + Mês", loadedResult.diaDeSorte.faixa4, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 5 - Mês", loadedResult.diaDeSorte.faixa5, leftPane, loadedResult);
                }
                case "Super Sete" -> {
                    addFaixaButton(rightPaneData, "Faixa 1 - 7 colunas", loadedResult.superSete.faixa1, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 2 - 6 colunas", loadedResult.superSete.faixa2, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 3 - 5 colunas", loadedResult.superSete.faixa3, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 4 - 4 colunas", loadedResult.superSete.faixa4, leftPane, loadedResult);
                    addFaixaButton(rightPaneData, "Faixa 5 - 3 colunas", loadedResult.superSete.faixa5, leftPane, loadedResult);
                }
            }

            // Configure ScrollPanes
            ScrollPane leftScroll = new ScrollPane(leftPane);
            ScrollPane rightScroll = new ScrollPane(rightPane);
            rightScroll.setContent(rightPaneData);

            configureScrollPane(leftScroll);
            configureScrollPane(rightScroll);

            // Add to main container
            resultTabPane.add(leftScroll, 0, 0);
            resultTabPane.add(rightScroll, 1, 0);

            setGridConstraints(resultTabPane);

            resultTab.setContent(resultTabPane);
            mainTabPane.getTabs().add(resultTab);
            mainTabPane.getSelectionModel().select(resultTab);

        } catch (Exception e) {
            System.err.println("Error loading result: " + e.getMessage());
        }
    }

    private void displayChampionNumbers(GridPane grid, Result loadedResult) {
        if (loadedResult.game.gameMode.name.equals("Super Sete")) {
            // Keep Super Sete format unchanged
            for (int col = 0; col < 7; col++) {
                Button columnLabel = new Button("C " + (col + 1));
                columnLabel.setDisable(true);
                columnLabel.setPrefWidth(80);
                columnLabel.setStyle("-fx-background-color: #000000; -fx-text-fill: #ffffff;");
                grid.add(columnLabel, col, 0);

                Button numberButton = new Button(loadedResult.championNumbers[col]);
                numberButton.setPrefWidth(80);
                grid.add(numberButton, col, 1);
            }
        } else {
            // Display standard format in 5 columns
            for (int i = 0; i < loadedResult.championNumbers.length; i++) {
                Button numberButton = new Button(loadedResult.championNumbers[i]);
                numberButton.setPrefWidth(40);
                grid.add(numberButton, i % 5, i / 5);  // Changed from 10 to 5 columns
            }
        }
        grid.setHgap(5);
        grid.setVgap(5);
    }

    // Then modify the addFaixaButton method to clear the warning when a faixa is selected
    private void addFaixaButton(VBox container, String faixaName, Object faixaData, VBox leftPane, Result result) {
        HBox faixaBox = new HBox(5);
        Button faixaButton = new Button(faixaName);
        faixaButton.setPrefWidth(width);

        Button countButton = new Button();
        countButton.setPrefWidth(width - 100);
        countButton.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER; ");

        switch (faixaData) {
            case Result.MMilionaria[] mMilionarias ->
                countButton.setText(mMilionarias.length + " jogos");
            case Result.BaseGame[] baseGames ->
                countButton.setText(baseGames.length + " jogos");
            case Result.DSorte[] dSortes ->
                countButton.setText(dSortes.length + " jogos");
            default -> {
            }
        }

        faixaButton.setOnAction(e -> {
            leftPane.getChildren().clear();
            leftPane.setId(faixaName);

            // Special handling for Super Sete
            if (result.game.gameMode.name.equals("Super Sete")) {
                displaySuperSeteWinningGames(leftPane, faixaData, result);
            } else {
                displayWinningGames(leftPane, faixaData, result);
            }
        });

        faixaBox.getChildren().addAll(faixaButton, countButton);
        container.getChildren().add(faixaBox);
    }

    private void displaySuperSeteWinningGames(VBox conteiner, Object faixaData, Result result) {
        Button titleLabel = new Button("Jogos Premiados");
        titleLabel.setPrefWidth(width);
        titleLabel.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");

        Button currentFaixaLabel = new Button("Faixa Atual: " + conteiner.getId());
        currentFaixaLabel.setPrefWidth(width);
        currentFaixaLabel.setStyle("-fx-background-color: #e0e0e0; -fx-alignment: CENTER;");

        conteiner.getChildren().addAll(titleLabel, currentFaixaLabel);

        VBox gamesContainer = new VBox(10);
        gamesContainer.setPadding(new Insets(5));

        Result.BaseGame[] games = (Result.BaseGame[]) faixaData;

        int gameNumber = 1; // Add a game counter

        for (Result.BaseGame game : games) {
            VBox gameBox = new VBox(5);
            gameBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 5;");

            Button gameNumberLabel = new Button("Jogo " + gameNumber++); // Add the game number
            gameNumberLabel.setPrefWidth(width);
            gameNumberLabel.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");

            GridPane numbersGrids = new GridPane();
            numbersGrids.setHgap(5);
            numbersGrids.setVgap(5);

            List<List<String>> columns = new ArrayList<>();
            List<String> currentColumn = new ArrayList<>();

            for (String number : game.numbers) {
                if (number.equals("|")) {
                    if (!currentColumn.isEmpty()) {
                        columns.add(new ArrayList<>(currentColumn));
                        currentColumn.clear();
                    }
                } else {
                    currentColumn.add(number);
                }
            }
            if (!currentColumn.isEmpty()) {
                columns.add(currentColumn);
            }

            for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
                Button columnLabel = new Button("C " + (colIndex + 1));
                columnLabel.setDisable(true);
                columnLabel.setPrefWidth(80);
                columnLabel.setStyle("-fx-background-color: #000000; -fx-text-fill: #ffffff;");
                numbersGrids.add(columnLabel, 0, colIndex);

                GridPane numberPane = new GridPane();
                numberPane.setHgap(2);
                numberPane.setVgap(2);

                List<String> columnNumbers = columns.get(colIndex);
                for (int i = 0; i < columnNumbers.size(); i++) {
                    Button numberButton = new Button(columnNumbers.get(i));
                    numberButton.setPrefWidth(25);

                    if (result.championNumbers[colIndex].equals(columnNumbers.get(i))) {
                        numberButton.setStyle("-fx-background-color: #90EE90;");
                    }

                    numberPane.add(numberButton, i, 0); // Changed from (0, i) to (i, 0)
                }

                numbersGrids.add(numberPane, 1, colIndex);
            }

            gameBox.getChildren().addAll(gameNumberLabel, numbersGrids); // Changed from numbersGrid to numbersGrids
            gamesContainer.getChildren().add(gameBox);
        }

        conteiner.getChildren().add(gamesContainer);
    }

    private void displayWinningGames(VBox container, Object faixaData, Result result) {
        Button titleLabel = new Button("Jogos Premiados");
        titleLabel.setPrefWidth(width);
        titleLabel.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");

        Button currentFaixaLabel = new Button("Faixa Atual: " + container.getId());
        currentFaixaLabel.setPrefWidth(width);
        currentFaixaLabel.setStyle("-fx-background-color: #e0e0e0; -fx-alignment: CENTER;");

        container.getChildren().addAll(titleLabel, currentFaixaLabel);

        VBox gamesContainer = new VBox(10);
        gamesContainer.setPadding(new javafx.geometry.Insets(5));

        int gameNumber = 1;

        switch (faixaData) {
            case Result.MMilionaria[] games -> {
                for (Result.MMilionaria game : games) {
                    VBox gameBox = new VBox(5);
                    gameBox.setStyle("-fx-background-color: white; -fx-padding: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");

                    Button gameNumberLabel = new Button("Jogo " + gameNumber++);
                    gameNumberLabel.setPrefWidth(width - 20);
                    gameNumberLabel.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #212529; -fx-font-weight: bold;");

                    GridPane numbersGrids = new GridPane();
                    numbersGrids.setHgap(2);
                    numbersGrids.setVgap(2);

                    // Add numbers with color highlighting
                    for (int i = 0; i < game.numbers.length; i++) {
                        Button numButton = new Button(game.numbers[i]);
                        numButton.setPrefWidth(40);
                        // Highlight if number matches champion number
                        if (Arrays.asList(result.championNumbers).contains(game.numbers[i])) {
                            numButton.setStyle("-fx-background-color: #90EE90;");
                        }
                        numbersGrids.add(numButton, i % 10, i / 10);
                    }

                    // Add trevos with color highlighting
                    GridPane trevosGrid = new GridPane();
                    trevosGrid.setHgap(2);
                    for (int i = 0; i < game.trevos.length; i++) {
                        Button trevoButton = new Button(game.trevos[i]);
                        trevoButton.setPrefWidth(40);
                        // Highlight if trevo matches champion trevo
                        if (Arrays.asList(result.championTrevos).contains(game.trevos[i])) {
                            trevoButton.setStyle("-fx-background-color: #90EE90;");
                        }
                        trevosGrid.add(trevoButton, i, 0);
                    }

                    gameBox.getChildren().addAll(gameNumberLabel, numbersGrids, trevosGrid);
                    gamesContainer.getChildren().add(gameBox);
                }
            }
            case Result.BaseGame[] games -> {
                for (Result.BaseGame game : games) {
                    VBox gameBox = new VBox(5);
                    gameBox.setStyle("-fx-background-color: white; -fx-padding: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");

                    Button gameNumberLabel = new Button("Jogo " + gameNumber++);
                    gameNumberLabel.setPrefWidth(width - 20);
                    gameNumberLabel.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #212529; -fx-font-weight: bold;");

                    GridPane numbersGrids = new GridPane();
                    numbersGrids.setHgap(2);
                    numbersGrids.setVgap(2);

                    for (int i = 0; i < game.numbers.length; i++) {
                        Button numButton = new Button(game.numbers[i]);
                        numButton.setPrefWidth(40);
                        // Highlight if number matches champion number
                        if (Arrays.asList(result.championNumbers).contains(game.numbers[i])) {
                            numButton.setStyle("-fx-background-color: #90EE90;");
                        }
                        numbersGrids.add(numButton, i % 10, i / 10);
                    }

                    gameBox.getChildren().addAll(gameNumberLabel, numbersGrids);
                    gamesContainer.getChildren().add(gameBox);
                }
            }
            case Result.DSorte[] games -> {
                for (Result.DSorte game : games) {
                    VBox gameBox = new VBox(5);
                    gameBox.setStyle("-fx-background-color: white; -fx-padding: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");

                    Button gameNumberLabel = new Button("Jogo " + gameNumber++);
                    gameNumberLabel.setPrefWidth(width - 20);
                    gameNumberLabel.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #212529; -fx-font-weight: bold;");

                    GridPane numbersGrids = new GridPane();
                    numbersGrids.setHgap(2);
                    numbersGrids.setVgap(2);

                    for (int i = 0; i < game.numbers.length; i++) {
                        Button numButton = new Button(game.numbers[i]);
                        numButton.setPrefWidth(40);
                        // Highlight if number matches champion number
                        if (Arrays.asList(result.championNumbers).contains(game.numbers[i])) {
                            numButton.setStyle("-fx-background-color: #90EE90;");
                        }
                        numbersGrids.add(numButton, i % 10, i / 10);
                    }

                    // Add month with color highlighting
                    Button monthButton = new Button(game.month);
                    monthButton.setPrefWidth(80);
                    // Highlight if month matches lucky month
                    if (game.month.equals(result.luckyMonth)) {
                        monthButton.setStyle("-fx-background-color: #90EE90;");
                    }

                    gameBox.getChildren().addAll(gameNumberLabel, numbersGrids, monthButton);
                    gamesContainer.getChildren().add(gameBox);
                }
            }
            default -> {
            }
        }

        container.getChildren().add(gamesContainer);
    }

    private void setGridConstraints(GridPane gridPane) {
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(60);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(40);
        gridPane.getColumnConstraints().addAll(col1, col2);

        RowConstraints row = new RowConstraints();
        row.setVgrow(Priority.ALWAYS);
        gridPane.getRowConstraints().add(row);
    }

    private void configureScrollPane(ScrollPane scrollPane) {
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefViewportHeight(height);
        scrollPane.setMaxHeight(Double.MAX_VALUE);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    }

    private void addSpecialElements(VBox content, Result loadedResult) {
        if (loadedResult.game.gameMode.name.equals("+Milionária") && loadedResult.championTrevos != null && loadedResult.championTrevos.length > 0) {
            Button trevosLabel = new Button("Trevos Sorteados:");
            trevosLabel.setPrefWidth(width);
            trevosLabel.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");
            content.getChildren().add(trevosLabel);

            GridPane trevosGrid = new GridPane();
            trevosGrid.setHgap(5);
            trevosGrid.setVgap(5);
            for (int i = 0; i < loadedResult.championTrevos.length; i++) {
                Button trevoButton = new Button(loadedResult.championTrevos[i]);
                trevoButton.setPrefWidth(40);
                trevosGrid.add(trevoButton, i, 0);
            }
            content.getChildren().add(trevosGrid);
        }

        if (loadedResult.game.gameMode.name.equals("Dia de Sorte") && loadedResult.luckyMonth != null) {
            Button monthLabel = new Button("Mês Sorteado:");
            monthLabel.setPrefWidth(width);
            monthLabel.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");
            content.getChildren().add(monthLabel);

            Button monthButton = new Button(loadedResult.luckyMonth);
            monthButton.setPrefWidth(100);
            content.getChildren().add(monthButton);
        }
    }

    private void loadGameTab(File gameFile) {
        String gameName = gameFile.getName();
        if (isTabOpen(gameName)) {
            return;
        }
        try {
            loadedGame = Game.loadFromFile(gameFile.getAbsolutePath());
            Tab gameTab = new Tab(gameFile.getName().split("_")[0] + " | "
                    + gameFile.getName().split("_")[1].split("-")[2] + "/"
                    + gameFile.getName().split("_")[1].split("-")[1] + "/"
                    + gameFile.getName().split("_")[1].split("-")[0] + " | "
                    + gameFile.getName().split("_")[2].split("-")[0] + ":"
                    + gameFile.getName().split("_")[2].split("-")[1] + ":"
                    + gameFile.getName().split("_")[2].split("-")[2].replace(".json", ""));
            gameTab.setClosable(true);
            loadedGame = Game.loadFromFile(gameFile.getAbsolutePath());
            System.out.println("\n=== Game Debug ===");
            System.out.println("Loaded Game: " + gameFile.getName());
            System.out.println("Game Mode: " + loadedGame.gameMode.name);
            System.out.println("Number of Games: " + loadedGame.games.length);
            System.out.println("================\n");

            // Main container
            GridPane gameTabPane = new GridPane();
            gameTabPane.setPrefWidth(width);
            gameTabPane.setPrefHeight(height);
            gameTabPane.setMaxHeight(Double.MAX_VALUE);

            // Left side - game content with pagination
            VBox gameContent = new VBox(5);
            gameContent.setPrefWidth(width * 0.7);
            gameContent.setPrefHeight(height);
            gameContent.setMaxHeight(Double.MAX_VALUE);
            gameContent.setPadding(new javafx.geometry.Insets(10));
            gameContent.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

            // Create pagination
            int itemsPerPage = 10; // Adjust this value as needed
            int pageCount = (int) Math.ceil((double) loadedGame.games.length / itemsPerPage);
            Pagination pagination = new Pagination(pageCount);
            Pagination topPagination = new Pagination(pageCount);
            topPagination.setPageFactory(pagination.getPageFactory());
            gameContent.getChildren().add(0, topPagination);
            topPagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
                pagination.setCurrentPageIndex(newIndex.intValue());
            });

            pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
                topPagination.setCurrentPageIndex(newIndex.intValue());
            });
            pagination.setPageFactory(pageIndex -> {
                VBox page = new VBox(10);
                int start = pageIndex * itemsPerPage;
                int end = Math.min(start + itemsPerPage, loadedGame.games.length);

                for (int i = start; i < end; i++) {
                    GridPane buttonsGrid = new GridPane();

                    // Create HBox for game header
                    HBox gameHeader = new HBox(5);
                    Button gameNumber = new Button("Jogo: " + (i + 1));
                    gameNumber.setPrefWidth(width);
                    gameNumber.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");

                    Button prontoButton = new Button("Pronto");
                    prontoButton.setMinWidth(80);
                    prontoButton.setStyle("-fx-background-color: #90EE90;");

                    gameHeader.getChildren().addAll(gameNumber, prontoButton);

                    String[] game = loadedGame.games[i];
                    List<Button> buttons = numbersButtons(game);
                    addGame(buttonsGrid, buttons);

                    // Add Pronto button functionality
                    final List<Button> gameButtons = buttons;
                    prontoButton.setOnAction(e -> {
                        boolean isReady = prontoButton.getText().equals("Pronto");
                        if (isReady) {
                            prontoButton.setText("Editar");
                            prontoButton.setStyle("-fx-background-color: #FFB6C1;");
                            gameButtons.forEach(button -> button.setDisable(true));

                            if (loadedGame.gameMode.name.equals("+Milionária")) {
                                GridPane trevosGrid = (GridPane) page.getChildren().get(
                                        page.getChildren().indexOf(buttonsGrid) + 2
                                );
                                trevosGrid.getChildren().forEach(node -> {
                                    if (node instanceof Button) {
                                        node.setDisable(true);
                                    }
                                });
                            }

                            if (loadedGame.gameMode.name.equals("Dia de Sorte")) {
                                GridPane monthGrid = (GridPane) page.getChildren().get(
                                        page.getChildren().indexOf(buttonsGrid) + 2
                                );
                                monthGrid.getChildren().forEach(node -> {
                                    if (node instanceof Button) {
                                        node.setDisable(true);
                                    }
                                });
                            }
                        } else {
                            prontoButton.setText("Pronto");
                            prontoButton.setStyle("-fx-background-color: #90EE90;");
                            gameButtons.forEach(button -> {
                                if (!button.getText().startsWith("Coluna")) {
                                    button.setDisable(false);
                                }
                            });

                            if (loadedGame.gameMode.name.equals("+Milionária")) {
                                GridPane trevosGrid = (GridPane) page.getChildren().get(
                                        page.getChildren().indexOf(buttonsGrid) + 2
                                );
                                trevosGrid.getChildren().forEach(node -> {
                                    if (node instanceof Button) {
                                        node.setDisable(false);
                                    }
                                });
                            }

                            if (loadedGame.gameMode.name.equals("Dia de Sorte")) {
                                GridPane monthGrid = (GridPane) page.getChildren().get(
                                        page.getChildren().indexOf(buttonsGrid) + 2
                                );
                                monthGrid.getChildren().forEach(node -> {
                                    if (node instanceof Button) {
                                        node.setDisable(false);
                                    }
                                });
                            }
                        }
                    });

                    page.getChildren().addAll(gameHeader, buttonsGrid);

                    // Add special elements (trevos, months) if needed
                    if (loadedGame.gameMode.name.equals("+Milionária")) {
                        String[] trevos1 = loadedGame.maisMilionaria.trevos[i];
                        List<Button> trevosButtons = numbersButtons(trevos1);
                        GridPane trevosGrid = new GridPane();
                        Button trevosButton = new Button("Trevos");
                        trevosButton.setPrefWidth(width);
                        trevosButton.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");
                        addGame(trevosGrid, trevosButtons);
                        page.getChildren().addAll(trevosButton, trevosGrid);
                    }

                    if (loadedGame.gameMode.name.equals("Dia de Sorte")) {
                        String month = loadedGame.diaDeSorte.month[i];
                        Button monthButtons = new Button(month);
                        GridPane monthGrid = new GridPane();
                        Button monthButton = new Button("Mês:");
                        monthButton.setPrefWidth(width);
                        monthButton.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");
                        monthGrid.add(monthButtons, 0, 0);
                        page.getChildren().addAll(monthButton, monthGrid);
                    }

                    if (i < end) {
                        Separator separator = new Separator();
                        separator.setPrefWidth(page.getPrefWidth());
                        page.getChildren().add(separator);
                    }

                }
                return page;
            });

            gameContent.getChildren().add(pagination);

            // Right side TabPane
            TabPane rightTabPane = new TabPane();
            rightTabPane.setPrefWidth(width * 0.3);
            rightTabPane.setPrefHeight(height);
            rightTabPane.setMaxHeight(Double.MAX_VALUE);

            // Tab 1 - Dados do Jogo
            Tab dadosTab = new Tab("Dados do Jogo");
            dadosTab.setClosable(false);
            VBox dadosContent = new VBox(5);
            dadosContent.setPadding(new javafx.geometry.Insets(10));
            dadosContent.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

            Button GameModeLabel = new Button(loadedGame.gameMode.name);
            GameModeLabel.setPrefWidth(width);
            GameModeLabel.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");
            dadosContent.getChildren().add(GameModeLabel);
            addConfigLabel(dadosContent, "Seleções:", String.valueOf(loadedGame.numbers));
            addConfigLabel(dadosContent, "Quantidade:", String.valueOf(loadedGame.amount));
            addConfigLabel(dadosContent, "Custo:", String.valueOf(loadedGame.totalCost));
            dadosContent.getChildren().add(new Separator());
            Button DataLabel = new Button("Data");
            DataLabel.setPrefWidth(width);
            DataLabel.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");
            dadosContent.getChildren().add(DataLabel);

            addConfigLabel(dadosContent, "Números Par", String.valueOf(loadedGame.data.totalEvenNumbers),
                    String.format("%.2f%%", loadedGame.data.evenNumberPercentage), false);
            for (Game.Data.EvenNumbers evenNumber : loadedGame.data.evenNumbers) {
                addConfigLabel(dadosContent, evenNumber.evenNumber, String.valueOf(evenNumber.evenAmount), String.format("%.2f%%", (evenNumber.evenAmount * 100.0f) / loadedGame.data.totalNumbers), true);
            }
            addConfigLabel(dadosContent, "Números Ímpar", String.valueOf(loadedGame.data.totalOddNumbers),
                    String.format("%.2f%%", loadedGame.data.oddNumberPercentage), false);
            for (Game.Data.OddNumbers oddNumber : loadedGame.data.oddNumbers) {
                addConfigLabel(dadosContent, oddNumber.oddNumber, String.valueOf(oddNumber.oddAmount), String.format("%.2f%%", (oddNumber.oddAmount * 100.0f) / loadedGame.data.totalNumbers), true);
            }

            ScrollPane dadosScroll = new ScrollPane(dadosContent);
            dadosScroll.setFitToWidth(true);
            dadosScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            dadosScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            dadosTab.setContent(dadosScroll);

            // Tab 2 - Configuração Usada
            Tab configTab = new Tab("Configuração Usada");
            configTab.setClosable(false);
            VBox configContent = new VBox(5);
            configContent.setPadding(new javafx.geometry.Insets(10));
            configContent.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

            Button numbersConfigButton = new Button("Probabilidades dos Números:");
            numbersConfigButton.setPrefWidth(width);
            numbersConfigButton.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");
            configContent.getChildren().add(numbersConfigButton);

            for (GameMode.Numbers number : loadedGame.gameMode.numbers) {

                addConfigLabel(configContent, "Número " + number.number + ":",
                        String.format("%.0f%%", number.probability * 100));
            }

            if (loadedGame.gameMode.name.equals("+Milionária")) {
                Button trevosConfigButton = new Button("Probabilidades dos Trevos:");
                trevosConfigButton.setPrefWidth(width);
                trevosConfigButton.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");
                configContent.getChildren().add(trevosConfigButton);

                for (GameMode.MaisMilionaria.Trevo trevo : loadedGame.gameMode.maisMilionaria.trevos) {
                    addConfigLabel(configContent, "Trevo " + trevo.trevo + ":",
                            String.format("%.0f%%", trevo.probability * 100));
                }
            } else if (loadedGame.gameMode.name.equals("Dia de Sorte")) {
                Button monthsConfigButton = new Button("Probabilidades dos Meses:");
                monthsConfigButton.setPrefWidth(width);
                monthsConfigButton.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");
                configContent.getChildren().add(monthsConfigButton);

                for (GameMode.DiaDeSorte.Months month : loadedGame.gameMode.diaDeSorte.months) {
                    addConfigLabel(configContent, "Mês " + month.month + ":",
                            String.format("%.0f%%", month.probability * 100));
                }
            }

            ScrollPane configScroll = new ScrollPane(configContent);
            configScroll.setFitToWidth(true);
            configTab.setContent(configScroll);

            rightTabPane.getTabs().addAll(dadosTab, configTab);

            // Configure ScrollPanes
            ScrollPane scrollPane = new ScrollPane(gameContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setPrefViewportHeight(height);
            scrollPane.setMaxHeight(Double.MAX_VALUE);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            ScrollPane scrollPane2 = new ScrollPane(rightTabPane);
            scrollPane2.setFitToWidth(true);
            scrollPane2.setFitToHeight(true);
            scrollPane2.setPrefViewportHeight(height);
            scrollPane2.setMaxHeight(Double.MAX_VALUE);
            scrollPane2.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane2.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            // Set column constraints
            gameTabPane.add(scrollPane, 0, 0);
            gameTabPane.add(scrollPane2, 1, 0);

            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPercentWidth(60);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setPercentWidth(40);
            gameTabPane.getColumnConstraints().addAll(col1, col2);

            // Set row constraints for vertical growth
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.ALWAYS);
            gameTabPane.getRowConstraints().add(row);

            gameTab.setContent(gameTabPane);
            mainTabPane.getTabs().add(gameTab);
            mainTabPane.getSelectionModel().select(gameTab);

        } catch (Exception e) {
            System.err.println("Error loading game: " + e.getMessage());
        }
    }

    private void refreshGameList(VBox buttonsContainer) {
        Platform.runLater(() -> {
            buttonsContainer.getChildren().clear();
            buttonsContainer.setSpacing(10);
            buttonsContainer.setPadding(new javafx.geometry.Insets(5));

            File gamesFolder = new File(SRC_FOLDER_PATH_GAMES);
            File[] gameFiles = gamesFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

            if (gameFiles != null) {
                for (File gameFile : gameFiles) {
                    VBox gameRow = new VBox(5);
                    gameRow.setStyle("-fx-background-color: white; -fx-padding: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");

                    // Main game button with improved styling
                    Button gameButton = new Button(
                            gameFile.getName().split("_")[0] + " | "
                            + gameFile.getName().split("_")[1].split("-")[2] + "/"
                            + gameFile.getName().split("_")[1].split("-")[1] + "/"
                            + gameFile.getName().split("_")[1].split("-")[0] + " | "
                            + gameFile.getName().split("_")[2].split("-")[0] + ":"
                            + gameFile.getName().split("_")[2].split("-")[1] + ":"
                            + gameFile.getName().split("_")[2].split("-")[2].replace(".json", ""));
                    gameButton.setPrefWidth(275);
                    gameButton.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #212529; -fx-font-weight: bold;");
                    gameButton.setOnAction(e -> loadGameTab(gameFile));

                    // Action buttons container
                    HBox buttonBox = new HBox(5);
                    buttonBox.setPrefWidth(275);
                    buttonBox.setAlignment(Pos.CENTER);

                    // Delete button with red styling
                    Button deleteButton = new Button("Deletar");
                    deleteButton.setPrefWidth(75);
                    deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                    deleteButton.setOnAction(e -> deleteGame(gameFile, buttonsContainer));

                    // Copy button with blue styling
                    Button copyButton = new Button("Copiar");
                    copyButton.setPrefWidth(75);
                    copyButton.setStyle("-fx-background-color: #0d6efd; -fx-text-fill: white;");
                    copyButton.setOnAction(e -> copyGameConfiguration(gameFile));

                    Button exportButton = new Button("Exportar PDF");
                    exportButton.setPrefWidth(125);
                    exportButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
                    exportButton.setOnAction(e -> {
                        try {
                            Game gameToExport = Game.loadFromFile(gameFile.getAbsolutePath());
                            exportGameToPDF(gameToExport);
                        } catch (Exception ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                            stage.getIcons().add(icon);
                            alert.setTitle("Erro");
                            alert.setHeaderText("Erro ao exportar jogo");
                            alert.setContentText("Ocorreu um erro ao tentar exportar o jogo para PDF.");
                            alert.showAndWait();
                        }
                    });

                    // Add hover effects
                    gameButton.setOnMouseEntered(e -> gameButton.setStyle("-fx-background-color: #e9ecef; -fx-text-fill: #212529; -fx-font-weight: bold;"));
                    gameButton.setOnMouseExited(e -> gameButton.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #212529; -fx-font-weight: bold;"));

                    deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #bb2d3b; -fx-text-fill: white;"));
                    deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;"));

                    copyButton.setOnMouseEntered(e -> copyButton.setStyle("-fx-background-color: #0b5ed7; -fx-text-fill: white;"));
                    copyButton.setOnMouseExited(e -> copyButton.setStyle("-fx-background-color: #0d6efd; -fx-text-fill: white;"));

                    exportButton.setOnMouseEntered(e -> exportButton.setStyle("-fx-background-color: #218838; -fx-text-fill: white;"));
                    exportButton.setOnMouseExited(e -> exportButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;"));

                    buttonBox.getChildren().addAll(deleteButton, copyButton, exportButton);
                    gameRow.getChildren().addAll(gameButton, buttonBox);
                    buttonsContainer.getChildren().add(gameRow);
                }
            }
        });
    }

    private void deleteGame(File gameFile, VBox buttonsContainer) {
        // Adiciona um popup para confirmar a exclusão
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        // Define o ícone do alerta
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(icon);
        alert.setTitle("Deletar Jogo");
        alert.setHeaderText("Você tem certeza que deseja deletar este jogo?");
        alert.setContentText("Esta ação não pode ser desfeita.");

        // Verifica se o usuário confirmou a exclusão e tenta deletar o arquivo
        if (alert.showAndWait().get() == ButtonType.OK && gameFile.delete()) {
            refreshGameList(buttonsContainer);
        } else {
            System.err.println("Falha ao deletar o arquivo do jogo.");
        }
    }

    private void jogosFilesTab(Tab tabGenGame) {
        ScrollPane scrollPaneGenGame = new ScrollPane();
        scrollPaneGenGame.setPrefWidth(width);
        scrollPaneGenGame.setPrefHeight(height);
        scrollPaneGenGame.setPadding(new javafx.geometry.Insets(5));
        scrollPaneGenGame.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneGenGame.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        // Initialize and assign the instance variable
        jogosButtonsContainer = new VBox(10);
        refreshGameList(jogosButtonsContainer);

        scrollPaneGenGame.setContent(jogosButtonsContainer);
        tabGenGame.setContent(scrollPaneGenGame);
        tabGenGame.setClosable(false);
    }

    private void resultsFilesTab(Tab tabGenResults) {
        ScrollPane scrollPaneGenResults = new ScrollPane();
        scrollPaneGenResults.setPrefWidth(width);
        scrollPaneGenResults.setPrefHeight(height);
        scrollPaneGenResults.setPadding(new javafx.geometry.Insets(5));
        scrollPaneGenResults.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneGenResults.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        // Initialize the class-level container
        resultsButtonsContainer = new VBox(10);
        refreshResultsList(resultsButtonsContainer);

        scrollPaneGenResults.setContent(resultsButtonsContainer);
        tabGenResults.setContent(scrollPaneGenResults);
        tabGenResults.setClosable(false);
    }

    private void updateResultsList(VBox buttonsContainer) {
        buttonsContainer.getChildren().clear();
        buttonsContainer.setSpacing(10);
        buttonsContainer.setPadding(new javafx.geometry.Insets(5));

        File resultsFolder = new File(SRC_FOLDER_PATH_RESULTS);
        File[] resultFiles = resultsFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        if (resultFiles != null) {
            for (File resultFile : resultFiles) {
                try {
                    Result localResult = Result.loadFromFile(resultFile.getAbsolutePath());
                    System.out.println("\n=== Result File Debug ===");
                    System.out.println("File: " + resultFile.getName());

                    // Check champion numbers
                    if (localResult.championNumbers != null) {
                        System.out.println("Champion Numbers: " + Arrays.toString(localResult.championNumbers));
                    } else {
                        System.out.println("ERROR: Champion Numbers are null");
                    }

                    // Check trevos if present
                    if (localResult.championTrevos != null && localResult.championTrevos.length > 0) {
                        System.out.println("Champion Trevos: " + Arrays.toString(localResult.championTrevos));
                    } else if (localResult.game.gameMode.name.equals("+Milionária")) {
                        System.out.println("ERROR: Champion Trevos missing for +Milionária result");
                    }

                    // Check lucky month if present
                    if (localResult.luckyMonth != null && !localResult.luckyMonth.isEmpty()) {
                        System.out.println("Lucky Month: " + localResult.luckyMonth);
                    } else if (localResult.game.gameMode.name.equals("Dia de Sorte")) {
                        System.out.println("ERROR: Lucky Month missing for Dia de Sorte result");
                    }

                    System.out.println("======================\n");
                } catch (Exception e) {
                    System.err.println("Error loading result file: " + resultFile.getName());
                    System.err.println("Error type: " + e.getClass().getSimpleName());
                    System.err.println("Error message: " + e.getMessage());
                    if (e.getCause() != null) {
                        System.err.println("Caused by: " + e.getCause().getMessage());
                    }
                }

                VBox resultRow = new VBox(5);
                resultRow.setStyle("-fx-background-color: white; -fx-padding: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");

                Button resultButton = new Button(
                        resultFile.getName().split("_")[0] + " | "
                        + resultFile.getName().split("_")[1].split("-")[2] + "/"
                        + resultFile.getName().split("_")[1].split("-")[1] + "/"
                        + resultFile.getName().split("_")[1].split("-")[0] + " | "
                        + resultFile.getName().split("_")[2].split("-")[0] + ":"
                        + resultFile.getName().split("_")[2].split("-")[1] + ":"
                        + resultFile.getName().split("_")[2].split("-")[2].replace(".json", ""));
                resultButton.setPrefWidth(275);
                resultButton.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #212529; -fx-font-weight: bold;");
                resultButton.setOnAction(e -> loadResultTab(resultFile));

                HBox buttonBox = new HBox(5);
                buttonBox.setPrefWidth(275);
                buttonBox.setAlignment(Pos.CENTER);

                Button deleteButton = new Button("Deletar");
                deleteButton.setPrefWidth(100);
                deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                deleteButton.setOnAction(e -> deleteResult(resultFile, buttonsContainer));

                Button exportButton = new Button("Exportar PDF");
                exportButton.setPrefWidth(175);
                exportButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");

                // Add hover effects
                resultButton.setOnMouseEntered(e -> resultButton.setStyle("-fx-background-color: #e9ecef; -fx-text-fill: #212529; -fx-font-weight: bold;"));
                resultButton.setOnMouseExited(e -> resultButton.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #212529; -fx-font-weight: bold;"));

                deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #bb2d3b; -fx-text-fill: white;"));
                deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;"));

                exportButton.setOnMouseEntered(e -> exportButton.setStyle("-fx-background-color: #218838; -fx-text-fill: white;"));
                exportButton.setOnMouseExited(e -> exportButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;"));

                buttonBox.getChildren().addAll(deleteButton, exportButton);
                resultRow.getChildren().addAll(resultButton, buttonBox);
                buttonsContainer.getChildren().add(resultRow);
            }
        }
    }

    private void refreshResultsList(VBox buttonsContainer) {
        Platform.runLater(() -> {
            updateResultsList(buttonsContainer);
            buttonsContainer.getChildren().clear();
            buttonsContainer.setSpacing(10);
            buttonsContainer.setPadding(new javafx.geometry.Insets(5));

            File resultsFolder = new File(SRC_FOLDER_PATH_RESULTS);
            File[] resultFiles = resultsFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

            if (resultFiles != null) {
                for (File resultFile : resultFiles) {
                    try {
                        // Load and debug the result file
                        Result localResult = Result.loadFromFile(resultFile.getAbsolutePath());
                        System.out.println("\n=== Result File Debug ===");
                        System.out.println("File: " + resultFile.getName());
                        System.out.println("Champion Numbers: " + Arrays.toString(localResult.championNumbers));
                        if (localResult.championTrevos != null && localResult.championTrevos.length > 0) {
                            System.out.println("Champion Trevos: " + Arrays.toString(localResult.championTrevos));
                        }
                        if (localResult.luckyMonth != null && !localResult.luckyMonth.isEmpty()) {
                            System.out.println("Lucky Month: " + localResult.luckyMonth);
                        }
                        System.out.println("======================\n");
                    } catch (Exception e) {
                        System.err.println("Error loading result file: " + resultFile.getName());
                    }

                    VBox resultRow = new VBox(5);
                    resultRow.setStyle("-fx-background-color: white; -fx-padding: 5; -fx-border-color: #cccccc; -fx-border-radius: 5;");

                    Button resultButton = new Button(
                            resultFile.getName().split("_")[0] + " | "
                            + resultFile.getName().split("_")[1].split("-")[2] + "/"
                            + resultFile.getName().split("_")[1].split("-")[1] + "/"
                            + resultFile.getName().split("_")[1].split("-")[0] + " | "
                            + resultFile.getName().split("_")[2].split("-")[0] + ":"
                            + resultFile.getName().split("_")[2].split("-")[1] + ":"
                            + resultFile.getName().split("_")[2].split("-")[2].replace(".json", ""));
                    resultButton.setPrefWidth(275);
                    resultButton.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #212529; -fx-font-weight: bold;");
                    resultButton.setOnAction(e -> loadResultTab(resultFile));

                    HBox buttonBox = new HBox(5);
                    buttonBox.setPrefWidth(275);
                    buttonBox.setAlignment(Pos.CENTER);

                    Button deleteButton = new Button("Deletar");
                    deleteButton.setPrefWidth(100);
                    deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                    deleteButton.setOnAction(e -> deleteResult(resultFile, buttonsContainer));

                    Button exportButton = new Button("Exportar PDF");
                    exportButton.setPrefWidth(175);
                    exportButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
                    exportButton.setOnAction(e -> {
                        try {
                            Result resultToExport = Result.loadFromFile(resultFile.getAbsolutePath());
                            exportResultToPDF(resultToExport);
                        } catch (Exception ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                            stage.getIcons().add(icon);
                            alert.setTitle("Erro");
                            alert.setHeaderText("Erro ao exportar resultado");
                            alert.setContentText("Ocorreu um erro ao tentar exportar o resultado para PDF.");
                            alert.showAndWait();
                        }
                    });

                    // Add hover effects
                    resultButton.setOnMouseEntered(e -> resultButton.setStyle("-fx-background-color: #e9ecef; -fx-text-fill: #212529; -fx-font-weight: bold;"));
                    resultButton.setOnMouseExited(e -> resultButton.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: #212529; -fx-font-weight: bold;"));

                    deleteButton.setOnMouseEntered(e -> deleteButton.setStyle("-fx-background-color: #bb2d3b; -fx-text-fill: white;"));
                    deleteButton.setOnMouseExited(e -> deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;"));

                    exportButton.setOnMouseEntered(e -> exportButton.setStyle("-fx-background-color: #218838; -fx-text-fill: white;"));
                    exportButton.setOnMouseExited(e -> exportButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;"));

                    buttonBox.getChildren().addAll(deleteButton, exportButton);
                    resultRow.getChildren().addAll(resultButton, buttonBox);
                    buttonsContainer.getChildren().add(resultRow);
                }
            }
        });
    }

    private void deleteResult(File resultFile, VBox buttonsContainer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(icon);
        alert.setTitle("Deletar Resultado");
        alert.setHeaderText("Você tem certeza que deseja deletar este resultado?");
        alert.setContentText("Esta ação não pode ser desfeita.");

        if (alert.showAndWait().get() == ButtonType.OK && resultFile.delete()) {
            refreshResultsList(buttonsContainer);
        } else {
            System.err.println("Falha ao deletar o arquivo do resultado.");
        }
    }

    private void updateGameNumbers(ChoiceBox<String> choiceBox) {
        genGameNumbers.getChildren().clear();  // Clear existing buttons first

        int selectedIndex = choiceBox.getSelectionModel().getSelectedIndex();
        List<Button> buttons = numbersButtons(gameModes[selectedIndex]);

        int columns = 10;
        for (int i = 0; i < buttons.size(); i++) {
            int row = i / columns;
            int col = i % columns;
            Button button = buttons.get(i);

            // Assign an ID to the button
            button.setId("numberButton" + i);

            genGameNumbers.add(button, col, row);
        }
        genGameNumbers.setHgap(5);
        genGameNumbers.setVgap(5);
    }

    private ChoiceBox<String> numberAmount(GameMode gameMode) {
        numberAmount = new ChoiceBox<>();
        for (int i = gameMode.minSelections; i <= gameMode.maxSelections; i++) {
            numberAmount.getItems().add(i + " Números");
        }
        numberAmount.setValue(gameMode.minSelections + " Números");
        numberAmount.setPrefWidth(168);

        // Set initial value
        currentNumberAmountValue = gameMode.minSelections;

        // Add listener to update currentNumberAmountValue
        numberAmount.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentNumberAmountValue = Integer.parseInt(newValue.split(" ")[0]);
            }
        });

        return numberAmount;
    }

    private void updateTrevos(ChoiceBox<String> choiceBox, GridPane gridPane) {
        // Clear existing buttons
        gridPane.getChildren().clear();

        int selectedIndex = choiceBox.getSelectionModel().getSelectedIndex();

        if (selectedIndex == 0) {
            List<Button> buttons = trevosButtons(gameModes[selectedIndex]);

            int columns = 6; // Define the number of columns you want

            for (int i = 0; i < buttons.size(); i++) {
                int row = i / columns;
                int col = i % columns;
                gridPane.add(buttons.get(i), col, row);
            }
            gridPane.setHgap(5);
            gridPane.setVgap(5);
            gridPane.setVisible(true); // Make sure the gridPane is visible
        } else {
            gridPane.setVisible(false); // Hide the gridPane if not in use
        }
    }

    private ChoiceBox<String> trevoAmount(ChoiceBox<String> choiceBox) {
        GameMode gameMode = gameModes[choiceBox.getSelectionModel().getSelectedIndex()];
        ChoiceBox<String> trevoAmount = new ChoiceBox<>();
        for (int i = 1; i < gameMode.maisMilionaria.maxTrevos; i++) {
            trevoAmount.getItems().add(gameMode.maisMilionaria.trevos[i].trevo + " Trevos");
        }
        trevoAmount.setValue(gameMode.maisMilionaria.trevos[1].trevo + " Trevos");
        return trevoAmount;
    }

    private List<Button> trevosButtons(GameMode gameMode) {
        List<Button> buttonList = new ArrayList<>();
        int currentNumber = 0;

        while (currentNumber < gameMode.maisMilionaria.trevos.length) {
            Button button = new Button(gameMode.maisMilionaria.trevos[currentNumber].trevo);
            button.setId("button" + currentNumber);
            buttonList.add(button);
            currentNumber++;
        }
        return buttonList;
    }

    private boolean isTabOpen(String tabName) {
        for (Tab tab : mainTabPane.getTabs()) {
            if (tab.getText().startsWith(tabName)) {
                mainTabPane.getSelectionModel().select(tab);
                return true;
            }
        }
        return false;
    }

    private void openTab(String tabName) {
        // Check if the tab already exists
        for (Tab tab : mainTabPane.getTabs()) {
            if (tab.getText().equals(tabName)) {
                // Select the existing tab and return immediately
                mainTabPane.getSelectionModel().select(tab);
                return;
            }
        }

        // Create a new tab since it doesn't exist
        Tab newTab = new Tab(tabName);
        newTab.setClosable(true);

        // Set the content of the tab based on the tabName
        switch (tabName) {
            case "Gerar Jogo" ->
                newTab.setContent(GenGame());
            case "Gerar Resultado" ->
                newTab.setContent(GenResult());
            default ->
                newTab.setContent(new VBox());
        }

        // Add the new tab to mainTabPane and select it
        mainTabPane.getTabs().add(newTab);
        mainTabPane.getSelectionModel().select(newTab);
    }

    private GridPane GenGame() {
        GridPane gridPane = new GridPane();
        gridPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        gridPane.setPrefWidth(width);
        gridPane.setPrefHeight(height);

        // Left side - game content
        VBox leftPane = new VBox(5);
        leftPane.setPrefWidth(width * 0.6);
        leftPane.setPrefHeight(height);
        leftPane.setMaxHeight(Double.MAX_VALUE);
        leftPane.setPadding(new javafx.geometry.Insets(10));
        leftPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        // Right side - game mode configuration (Probability Editor)
        rightPane = new VBox(5); // Atualizado para variável de instância
        rightPane.setPrefWidth(width * 0.4);
        rightPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        // Configure ScrollPanes for both panes
        ScrollPane leftScroll = new ScrollPane(leftPane);
        leftScroll.setFitToWidth(true);
        leftScroll.setFitToHeight(true);
        leftScroll.setPrefViewportHeight(height);
        leftScroll.setMaxHeight(Double.MAX_VALUE);
        leftScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        ScrollPane rightScroll = new ScrollPane(rightPane);
        rightScroll.setFitToWidth(true);
        rightScroll.setFitToHeight(true);
        rightScroll.setPrefViewportHeight(height);
        rightScroll.setMaxHeight(Double.MAX_VALUE);
        rightScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rightScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Add the scroll panes to the grid
        gridPane.add(leftScroll, 0, 0);
        gridPane.add(rightScroll, 1, 0);

        // Set column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        gridPane.getColumnConstraints().addAll(col1, col2);

        // Set row constraints for vertical growth
        RowConstraints row = new RowConstraints();
        row.setVgrow(Priority.ALWAYS);
        gridPane.getRowConstraints().add(row);

        // Build the content for leftPane
        leftPane.setPadding(new javafx.geometry.Insets(5, 5, 5, 5));
        leftPane.setSpacing(5);

        // Create the game mode choice box
        choiceBox = gameModeChoice(); // Atualizado para variável de instância
        numberAmount = numberAmount(gameModes[choiceBox.getSelectionModel().getSelectedIndex()]);

        HBox gameModeBox = new HBox();
        gameModeBox.setSpacing(5);
        gameModeBox.getChildren().addAll(choiceBox, numberAmount);

        // Initialize gameNumbers
        genGameNumbers = new GridPane();
        updateGameNumbers(choiceBox);

        // Initialize trevos
        trevos = new GridPane();
        updateTrevos(choiceBox, trevos);

        ChoiceBox<String> trevoAmount = trevoAmount(choiceBox);
        trevoAmount.setPrefWidth(165);

        Label trevoLabel = new Label("Editar Trevos:");
        trevoLabel.setFont(new Font(14));

        HBox trevoAmountBox = new HBox();
        trevoAmountBox.setSpacing(10);
        trevoAmountBox.getChildren().addAll(trevos, trevoAmount);

        // Set initial visibility
        trevoLabel.setVisible(choiceBox.getSelectionModel().getSelectedIndex() == 0);
        trevos.setVisible(choiceBox.getSelectionModel().getSelectedIndex() == 0);
        TextField numberAmountField = new TextField();
        numberAmountField.setPrefWidth(165);
        numberAmountField.setPromptText("Digite o número de Jogos:");
        HBox buttonBox = new HBox();
        monthsButtons = new GridPane();
        updateMonths(choiceBox, monthsButtons);
        Label monthsLabel = new Label("Editar Meses:");

        Button generateButton = new Button("Gerar Jogos");
        generateButton.setPrefWidth(169);
        choiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            GameMode selectedGameMode = gameModes[newValue.intValue()];
            numberAmount.getItems().clear();

            // Set items first
            for (int i = selectedGameMode.minSelections; i <= selectedGameMode.maxSelections; i++) {
                numberAmount.getItems().add(i + " Números");
            }

            // Then set the value after items are populated using Platform.runLater
            Platform.runLater(() -> {
                numberAmount.setValue(selectedGameMode.minSelections + " Números");
            });

            // Update the gameNumbers and other UI elements
            updateGameNumbers(choiceBox);
            updateTrevos(choiceBox, trevos);
            updateMonths(choiceBox, monthsButtons);
        });

        generateButton.setOnAction(event -> {
            // add a popup to confirm the generation
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            Stage stage = (Stage) generateButton.getScene().getWindow();
            alert.initOwner(stage);
            stage.getIcons().add(icon);
            alert.setTitle("Confirmar Geração de Jogos");
            alert.setHeaderText("Deseja realmente gerar os jogos?");
            alert.setContentText("Ao confirmar, os jogos serão gerados e salvos em um arquivo.");
            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    int selectedIndex = choiceBox.getSelectionModel().getSelectedIndex();
                    GameMode selectedGameMode = gameModes[selectedIndex];

                    // Use the currentNumberAmountValue
                    int numberAmountValue = currentNumberAmountValue;

                    // Get the selected number of trevos (for Mais Milionária)
                    int trevoAmountValue = 0;
                    if (selectedIndex == 0 && trevoAmount.getValue() != null) {
                        String selectedTrevoAmount = trevoAmount.getValue();
                        if (gameModes[selectedIndex].name.equals("+Milionária")) {
                            trevoAmountValue = Integer.parseInt(selectedTrevoAmount.split(" ")[0]);
                        }
                    }

                    // Get the number of games to generate
                    int gameAmount = Integer.parseInt(numberAmountField.getText());

                    // Create a new Game object with the selected parameters
                    Game game = new Game(selectedGameMode, gameAmount, numberAmountValue, trevoAmountValue);

                    // Generate the current date formatted
                    String generationDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

                    // Create the game file name
                    String gameFileName = selectedGameMode.name + "_" + generationDate + ".json";
                    String filePath = SRC_FOLDER_PATH_GAMES + gameFileName;

                    // Create the directory if it doesn't exist
                    createDirectoryIfNotExists(SRC_FOLDER_PATH_GAMES);

                    // Create file in path
                    File file = new File(filePath);
                    // Check if file exists
                    if (file.exists()) {
                        // If file exists, delete it
                        file.delete();
                    }

                    game.saveToFile(filePath);
                    System.out.println("\nGame saved successfully to: " + filePath);
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    stage = (Stage) successAlert.getDialogPane().getScene().getWindow();
                    stage.getIcons().add(icon);
                    successAlert.setTitle("Jogo Salvo");
                    successAlert.setHeaderText("Jogo salvo com sucesso!");
                    successAlert.setContentText("Arquivo salvo em:\n" + filePath);
                    successAlert.showAndWait();
                    System.out.println("\nGame saved successfully to: " + filePath);
                    Tab jogosTab = mainTabPane.getTabs().stream()
                            .filter(tab -> tab.getText().equals("Jogos"))
                            .findFirst()
                            .orElse(null);
                    refreshGameList(jogosButtonsContainer);

                    if (jogosTab != null) {
                        ScrollPane scrollPane = (ScrollPane) jogosTab.getContent();
                        VBox buttonsContainer = (VBox) scrollPane.getContent();
                        refreshGameList(buttonsContainer);
                    }

                    // ... (add any additional logic here, such as showing a success message)
                } catch (Exception e) {
                    System.err.println("An error occurred: " + e.getMessage());
                }
            } else {
                // User canceled the operation
                System.out.println("Operation canceled by user.");
            }
        });

        // Add listener to update gameNumbers, trevos, and trevoLabel visibility when selection changes
        choiceBox.getSelectionModel().selectedIndexProperty().addListener((ov, old_val, new_val) -> {
            updateGameNumbers(choiceBox);
            updateTrevos(choiceBox, trevos);
            updateMonths(choiceBox, monthsButtons);
            boolean isMaisMilionaria = new_val.intValue() == 0;
            boolean isDiaDeSorte = new_val.intValue() == 6;

            GameMode selectedGameMode = gameModes[new_val.intValue()];
            ChoiceBox<String> updatedNumberAmount = numberAmount(selectedGameMode);
            gameModeBox.getChildren().set(1, updatedNumberAmount);

            // Remove all special elements first
            leftPane.getChildren().removeAll(trevoLabel, trevoAmountBox, monthsLabel, monthsButtons);

            // Find the index of buttonBox
            int buttonBoxIndex = leftPane.getChildren().indexOf(buttonBox);

            if (isMaisMilionaria) {
                // Add trevo elements
                leftPane.getChildren().add(buttonBoxIndex, trevoLabel);
                leftPane.getChildren().add(buttonBoxIndex + 1, trevoAmountBox);
                trevoLabel.setVisible(true);
                trevos.setVisible(true);
                trevoAmount.setVisible(true);
            } else if (isDiaDeSorte) {
                // Add month elements
                leftPane.getChildren().add(buttonBoxIndex, monthsLabel);
                leftPane.getChildren().add(buttonBoxIndex + 1, monthsButtons);
                monthsLabel.setVisible(true);
                monthsButtons.setVisible(true);
            }
        });

        // Add listener to update gameNumbers and trevos when selection changes
        choiceBox.getSelectionModel().selectedIndexProperty().addListener((ov, old_val, new_val) -> {
            updateGameNumbers(choiceBox);
            updateTrevos(choiceBox, trevos);
        });

        // Initialize the probability editor for the default game mode
        GameMode selectedGameMode = gameModes[0]; // Default to first game mode
        VBox probabilityEditor = createProbabilityEditor(selectedGameMode);
        rightPane.getChildren().add(probabilityEditor);

        // Update the probability editor when the game mode changes
        choiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            GameMode newSelectedGameMode = gameModes[newValue.intValue()];
            VBox newProbabilityEditor = createProbabilityEditor(newSelectedGameMode);
            rightPane.getChildren().clear();
            rightPane.getChildren().add(newProbabilityEditor);
        });

        Label label1 = new Label("Escolha o Jogo:");
        label1.setFont(new Font(14));
        Label label2 = new Label("Editar Numeros:");
        label2.setFont(new Font(14));

        leftPane.getChildren().addAll(label1, gameModeBox, label2, genGameNumbers);
        if (choiceBox.getSelectionModel().getSelectedIndex() == 0) {
            leftPane.getChildren().addAll(trevoLabel, trevoAmountBox);
        }
        if (choiceBox.getSelectionModel().getSelectedIndex() == 6) {
            leftPane.getChildren().addAll(monthsLabel, monthsButtons);
        }

        buttonBox.setSpacing(5);
        buttonBox.getChildren().addAll(numberAmountField, generateButton);
        leftPane.getChildren().add(buttonBox);
        return gridPane;
    }

    private void copyGameConfiguration(File gameFile) {
        try {
            // Carregar o jogo do arquivo
            Game loadedGameFile = Game.loadFromFile(gameFile.getAbsolutePath());
            GameMode loadedGameMode = loadedGameFile.gameMode;

            // Atualizar o gameMode atual
            currentGameMode = loadedGameMode;

            // Atualizar a interface na aba "Gerar Jogo"
            Platform.runLater(() -> {
                // Selecionar a aba "Gerar Jogo"
                Tab gerarJogoTab = mainTabPane.getTabs().stream()
                        .filter(tab -> tab.getText().equals("Gerar Jogo"))
                        .findFirst()
                        .orElse(null);

                // Se a aba não estiver aberta, abri-la
                if (gerarJogoTab == null) {
                    openTab("Gerar Jogo");
                    gerarJogoTab = mainTabPane.getTabs().stream()
                            .filter(tab -> tab.getText().equals("Gerar Jogo"))
                            .findFirst()
                            .orElse(null);
                }

                // Selecionar a aba "Gerar Jogo"
                if (gerarJogoTab != null) {
                    mainTabPane.getSelectionModel().select(gerarJogoTab);
                }

                // Atualizar os componentes da interface com a configuração carregada
                updateGenerateGameTabWithGameMode(loadedGameMode, loadedGame);
            });

        } catch (Exception e) {
            System.err.println("Erro ao copiar a configuração do jogo: " + e.getMessage());
        }
    }

    private void updateGenerateGameTabWithGameMode(GameMode gameMode, Game loadedGame) {
        // Atualizar o ChoiceBox de modos de jogo
        choiceBox.setValue(gameMode.name);

        // Atualizar o número de números selecionados
        numberAmount.getItems().clear();
        for (int i = gameMode.minSelections; i <= gameMode.maxSelections; i++) {
            numberAmount.getItems().add(i + " Números");
        }
        numberAmount.setValue(loadedGame.numbers + " Números");

        // Atualizar as probabilidades dos números
        for (int i = 0; i < gameMode.numbers.length; i++) {
            gameMode.numbers[i].probability = loadedGame.gameMode.numbers[i].probability;
        }

        // Atualizar a interface da seção de probabilidade
        rightPane.getChildren().clear();
        VBox probabilityEditor = createProbabilityEditor(gameMode);
        rightPane.getChildren().add(probabilityEditor);

        // Atualizar quaisquer outros componentes necessários
        updateGameNumbers(choiceBox);
        updateTrevos(choiceBox, trevos);
        updateMonths(choiceBox, monthsButtons);
    }

    private VBox createProbabilityEditor(GameMode gameMode) {
        VBox probabilityEditor = new VBox(15);
        probabilityEditor.setPadding(new javafx.geometry.Insets(5));
        probabilityEditor.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        if (gameMode.name.equals("Super Sete")) {
            Label numbersLabel = new Label("Editar probabilidade dos números (%)");
            numbersLabel.setFont(new Font(16));
            probabilityEditor.getChildren().add(numbersLabel);
            VBox superSevenContainer = createSuperSevenProbabilitySection(gameMode);
            probabilityEditor.getChildren().add(superSevenContainer);
        } else {
            // Numbers section
            Label numbersLabel = new Label("Editar probabilidade dos números (%)");
            numbersLabel.setFont(new Font(16));
            probabilityEditor.getChildren().add(numbersLabel);

            VBox numbersContainer = createNumbersProbabilitySection(gameMode);
            probabilityEditor.getChildren().add(numbersContainer);

            // Trevos section (for Mais Milionária)
            if (gameMode.name.equals("+Milionária")) {
                Label trevosLabel = new Label("Editar probabilidade dos trevos (%)");
                trevosLabel.setFont(new Font(16));
                probabilityEditor.getChildren().add(trevosLabel);

                VBox trevosContainer = createTrevosProbabilitySection(gameMode);
                probabilityEditor.getChildren().add(trevosContainer);
            }

            // Months section (for Dia de Sorte)
            if (gameMode.name.equals("Dia de Sorte")) {
                Label monthsLabel = new Label("Editar probabilidade dos meses (%)");
                monthsLabel.setFont(new Font(16));
                probabilityEditor.getChildren().add(monthsLabel);

                VBox monthsContainer = createMonthsProbabilitySection(gameMode);
                probabilityEditor.getChildren().add(monthsContainer);
            }
        }

        ScrollPane scrollPane = new ScrollPane(probabilityEditor);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox mainContainer = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return mainContainer;
    }

    private VBox createNumbersProbabilitySection(GameMode gameMode) {
        VBox container = new VBox(10);
        container.setPadding(new javafx.geometry.Insets(5));

        // Skip if it's Super Seven
        if (gameMode.name.equals("Super Sete")) {
            return container;
        }

        for (int i = 0; i < gameMode.numbers.length; i++) {
            Button numberButton = new Button(gameMode.numbers[i].number);
            numberButton.setDisable(true);
            numberButton.setPrefWidth(60);

            // Set initial style based on probability
            updateButtonStyle(numberButton, gameMode.numbers[i].probability);

            Slider probabilitySlider = new Slider(0, 100, gameMode.numbers[i].probability * 100);
            probabilitySlider.setPrefWidth(200);
            probabilitySlider.setMajorTickUnit(10);
            probabilitySlider.setMinorTickCount(1);
            probabilitySlider.setBlockIncrement(1);
            probabilitySlider.setShowTickMarks(true);
            probabilitySlider.setShowTickLabels(true);

            Label probabilityLabel = new Label(String.format("%.0f%%", gameMode.numbers[i].probability * 100));
            probabilityLabel.setPrefWidth(50);

            int index = i;
            probabilitySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                float newProbability = newValue.floatValue() / 100.0f;
                gameMode.updateProbabilities("numbers", index, newProbability);
                probabilityLabel.setText(String.format("%.0f%%", newValue.floatValue()));

                // Update button style based on new probability
                updateButtonStyle(numberButton, newProbability);

                System.out.println(String.format("Number %s probability changed from %.2f%% to %.2f%%",
                        numberButton.getText(), oldValue.floatValue(), newValue.floatValue()));
            });

            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.getChildren().addAll(numberButton, probabilitySlider, probabilityLabel);
            container.getChildren().add(hbox);
        }
        return container;
    }

    private VBox createSuperSevenProbabilitySection(GameMode gameMode) {
        VBox container = new VBox(10);
        container.setPadding(new javafx.geometry.Insets(5));

        // Create sliders for numbers 0-9 that affect all columns
        for (int i = 0; i < 10; i++) {
            Button numberButton = new Button(String.valueOf(i));
            numberButton.setDisable(true);
            numberButton.setPrefWidth(60);

            float initialProbability = gameMode.superSete.columns[0].numbers[i].probability;
            updateButtonStyle(numberButton, initialProbability);

            Slider probabilitySlider = new Slider(0, 100, initialProbability * 100);
            probabilitySlider.setPrefWidth(200);
            probabilitySlider.setMajorTickUnit(10);
            probabilitySlider.setMinorTickCount(1);
            probabilitySlider.setBlockIncrement(1);
            probabilitySlider.setShowTickMarks(true);
            probabilitySlider.setShowTickLabels(true);

            Label probabilityLabel = new Label(String.format("%.0f%%", initialProbability * 100));
            probabilityLabel.setPrefWidth(50);

            int index = i;
            probabilitySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                float newProbability = newValue.floatValue() / 100.0f;
                // Update probability for this number across all columns
                gameMode.updateSuperSevenProbability(index, newProbability);
                probabilityLabel.setText(String.format("%.0f%%", newValue.floatValue()));
                updateButtonStyle(numberButton, newProbability);
            });

            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.getChildren().addAll(numberButton, probabilitySlider, probabilityLabel);
            container.getChildren().add(hbox);
        }
        return container;
    }

    private VBox createTrevosProbabilitySection(GameMode gameMode) {
        VBox container = new VBox(10);
        container.setPadding(new javafx.geometry.Insets(5));

        for (int i = 0; i < gameMode.maisMilionaria.trevos.length; i++) {
            Button trevoButton = new Button(gameMode.maisMilionaria.trevos[i].trevo);
            trevoButton.setDisable(true);
            trevoButton.setPrefWidth(60);

            // Set initial style based on probability
            updateButtonStyle(trevoButton, gameMode.maisMilionaria.trevos[i].probability);

            Slider probabilitySlider = new Slider(0, 100, gameMode.maisMilionaria.trevos[i].probability * 100);
            probabilitySlider.setPrefWidth(200);
            probabilitySlider.setMajorTickUnit(10);
            probabilitySlider.setMinorTickCount(1);
            probabilitySlider.setBlockIncrement(1);
            probabilitySlider.setShowTickMarks(true);
            probabilitySlider.setShowTickLabels(true);

            Label probabilityLabel = new Label(String.format("%.0f%%", gameMode.maisMilionaria.trevos[i].probability * 100));
            probabilityLabel.setPrefWidth(50);

            int index = i;
            probabilitySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                float newProbability = newValue.floatValue() / 100.0f;
                gameMode.updateProbabilities("trevos", index, newProbability);
                probabilityLabel.setText(String.format("%.0f%%", newValue.floatValue()));

                // Update button style based on new probability
                updateButtonStyle(trevoButton, newProbability);

                System.out.println(String.format("Trevo %s probability changed from %.2f%% to %.2f%%",
                        trevoButton.getText(), oldValue.floatValue(), newValue.floatValue()));
            });

            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.getChildren().addAll(trevoButton, probabilitySlider, probabilityLabel);
            container.getChildren().add(hbox);
        }
        return container;
    }

    private VBox createMonthsProbabilitySection(GameMode gameMode) {
        VBox container = new VBox(10);
        container.setPadding(new javafx.geometry.Insets(5));

        for (int i = 0; i < gameMode.diaDeSorte.months.length; i++) {
            Button monthButton = new Button(gameMode.diaDeSorte.months[i].month);
            monthButton.setDisable(true);
            monthButton.setPrefWidth(100);

            // Set initial style based on probability
            updateButtonStyle(monthButton, gameMode.diaDeSorte.months[i].probability);

            Slider probabilitySlider = new Slider(0, 100, gameMode.diaDeSorte.months[i].probability * 100);
            probabilitySlider.setPrefWidth(200);
            probabilitySlider.setMajorTickUnit(10);
            probabilitySlider.setMinorTickCount(1);
            probabilitySlider.setBlockIncrement(1);
            probabilitySlider.setShowTickMarks(true);
            probabilitySlider.setShowTickLabels(true);

            Label probabilityLabel = new Label(String.format("%.0f%%", gameMode.diaDeSorte.months[i].probability * 100));
            probabilityLabel.setPrefWidth(50);

            int index = i;
            probabilitySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                float newProbability = newValue.floatValue() / 100.0f;
                gameMode.updateProbabilities("months", index, newProbability);
                probabilityLabel.setText(String.format("%.0f%%", newValue.floatValue()));

                // Update button style based on new probability
                updateButtonStyle(monthButton, newProbability);

                System.out.println(String.format("Month %s probability changed from %.2f%% to %.2f%%",
                        monthButton.getText(), oldValue.floatValue(), newValue.floatValue()));
            });

            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.getChildren().addAll(monthButton, probabilitySlider, probabilityLabel);
            container.getChildren().add(hbox);
        }
        return container;
    }

    private void updateButtonStyle(Button button, float probability) {
        if (probability != 1.0f) {
            button.setStyle("-fx-background-color: #FFCCCC;");
        } else {
            button.setStyle("");
        }
    }

    private void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void updateMonths(ChoiceBox<String> choiceBox, GridPane gridPane) {
        // Clear existing buttons
        gridPane.getChildren().clear();

        int selectedIndex = choiceBox.getSelectionModel().getSelectedIndex();

        if (selectedIndex == 6) {
            List<Button> buttons = monthsButtons(gameModes[selectedIndex]);

            int columns = 4; // Define the number of columns you want

            for (int i = 0; i < buttons.size(); i++) {
                int row = i / columns;
                int col = i % columns;
                gridPane.add(buttons.get(i), col, row);
            }
            gridPane.setHgap(5);
            gridPane.setVgap(5);
            gridPane.setVisible(true); // Make sure the gridPane is visible
        } else {
            gridPane.setVisible(false); // Hide the gridPane if not in use
        }
    }

    /**
     * Adiciona botões de jogo a um GridPane, organizando-os em colunas.
     *
     * @param gridPane O GridPane onde os botões serão adicionados.
     * @param buttons A lista de botões a serem adicionados ao GridPane.
     */
    private void addGame(GridPane gridPane, List<Button> buttons) {
        int currentCol = 0;
        int currentRow = 0;
        // Set columns to 4 for Dia de Sorte months, otherwise use 10
        int columns = buttons.get(0).getText().matches("Janeiro|Fevereiro|Março|Abril|Maio|Junho|Julho|Agosto|Setembro|Outubro|Novembro|Dezembro") ? 4 : 10;

        for (Button button : buttons) {
            if (button.getText().startsWith("Coluna")) {
                currentCol = 0;
                currentRow++;
                gridPane.add(button, currentCol, currentRow);
                currentCol++;
            } else {
                gridPane.add(button, currentCol, currentRow);
                currentCol++;

                if (currentCol >= columns) {
                    currentCol = 0;
                    currentRow++;
                }
            }
        }

        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setVisible(true);
    }

    private List<Button> monthsButtons(GameMode gameMode) {
        List<Button> buttonList = new ArrayList<>();
        int currentMonth = 0;

        while (currentMonth < gameMode.diaDeSorte.months.length) {
            Button button = new Button(gameMode.diaDeSorte.months[currentMonth].month);
            button.setId("button" + currentMonth);
            buttonList.add(button);
            button.setPrefWidth(81);
            currentMonth++;
        }
        return buttonList;
    }

    private ChoiceBox<String> gameModeChoice() {
        ChoiceBox<String> gameModeChoice = new ChoiceBox<>();
        for (GameMode gameMode : gameModes) {
            gameModeChoice.getItems().add(gameMode.name);
        }
        gameModeChoice.setValue(gameModes[0].name);
        gameModeChoice.setPrefWidth(165);

        // Atualizar currentGameMode quando o jogo é selecionado
        currentGameMode = gameModes[0]; // Inicializa com o primeiro gameMode

        gameModeChoice.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            currentGameMode = gameModes[newValue.intValue()];
        });

        return gameModeChoice;
    }

    private List<Button> numbersButtons(GameMode gameMode) {
        List<Button> buttonList = new ArrayList<>();
        int currentNumber = 0;

        while (currentNumber < gameMode.playableNumbers) {
            Button button = new Button(gameMode.numbers[currentNumber].number);
            button.setId("button" + currentNumber);
            buttonList.add(button);
            currentNumber++;
        }

        return buttonList;
    }

    private List<Button> numbersButtons(String[] game) {
        List<Button> buttonList = new ArrayList<>();
        int currentNumber = 0;
        int col = 1;
        while (currentNumber < game.length) {
            String buttonText = game[currentNumber];
            Button button = new Button(buttonText);
            button.setId("button" + currentNumber);
            if ("|".equals(buttonText)) {
                buttonText = "Coluna " + col;
                button.setDisable(true);
                button.setStyle("-fx-background-color: #000000; -fx-text-fill: #ffffff;");
                button.setText(buttonText);
                col++;
            }
            buttonList.add(button);
            currentNumber++;
        }

        return buttonList;
    }

    private GridPane GenResult() {
        GridPane gridPane = new GridPane();
        gridPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        gridPane.setPrefWidth(width);
        gridPane.setPrefHeight(height);

        // Left side - game content
        VBox leftPane = new VBox(5); // Increased spacing
        leftPane.setPrefWidth(width * 0.6);
        leftPane.setPrefHeight(height);
        leftPane.setMaxHeight(Double.MAX_VALUE);
        leftPane.setPadding(new javafx.geometry.Insets(5)); // Increased padding
        leftPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        // Right side
        rightPane = new VBox(5);
        rightPane.setPrefWidth(width * 0.4);
        rightPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        ChoiceBox<String> gameChoice = gameChoice();
        gameChoice.setPrefWidth(300);

        // Inside the GenResult() method, after initializing rightPane:
        rightPane = new VBox(5);
        rightPane.setPrefWidth(width * 0.4);
        rightPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        // Add the new button labels
        HBox resultHeader = new HBox(5);
        Button numeroPremiadoLabel = new Button("Número Premiado");
        numeroPremiadoLabel.setPrefWidth(width / 2);
        numeroPremiadoLabel.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");

        resultHeader.getChildren().addAll(numeroPremiadoLabel);
        rightPane.getChildren().add(resultHeader);

        Button specialPremiadoLabel = new Button(""); // Will be updated based on game type
        specialPremiadoLabel.setPrefWidth(width);
        specialPremiadoLabel.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER;");

        // Create GridPane for numbers
        numbersGrid = new GridPane();
        numbersGrid.setHgap(5);
        numbersGrid.setVgap(5);

        // Create GridPane for special buttons (trevos/month)
        specialGrid = new GridPane();
        specialGrid.setHgap(5);
        specialGrid.setVgap(5);

        gameChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("Escolha um jogo") && !newValue.equals("Jogo não encontrado")) {
                String gameName = newValue.split(" \\|")[0];
                for (GameMode mode : gameModes) {

                    if (mode.name.equals(gameName)) {
                        numbersGrid.getChildren().clear();
                        specialGrid.getChildren().clear();

                        if (mode.name.equals("Super Sete")) {
                            // Reset the array with exactly 7 elements for Super Sete
                            selectedNumbers = new String[7];
                            Arrays.fill(selectedNumbers, ""); // Initialize with empty strings
                            for (int col = 0; col < 7; col++) {
                                // Add column label
                                Button columnLabel = new Button("C " + (col + 1));
                                columnLabel.setDisable(true);
                                columnLabel.setPrefWidth(80);
                                columnLabel.setStyle("-fx-background-color: #000000; -fx-text-fill: #ffffff;");
                                numbersGrid.add(columnLabel, col, 0);

                                // Add single number display for each column
                                Button numberButton = new Button("");
                                numberButton.setPrefWidth(80);
                                numbersGrid.add(numberButton, col, 1);
                            }
                            updateSuperSeteState(selectedNumbers);
                        } else {
                            // Original code for other game modes
                            for (int i = 0; i < mode.minSelections; i++) {
                                Button numberButton = new Button(" ");
                                numberButton.setPrefWidth(40);
                                numbersGrid.add(numberButton, i % 10, i / 10);
                            }
                        }

                        // Handle special buttons
                        switch (mode.name) {
                            case "+Milionária" -> {
                                specialPremiadoLabel.setText("Trevos Premiados");
                                specialPremiadoLabel.setVisible(true);
                                // Add trevo buttons based on minTrevos
                                for (int i = 0; i < mode.maisMilionaria.minTrevos; i++) {
                                    Button trevoButton = new Button(" ");
                                    trevoButton.setPrefWidth(40);
                                    specialGrid.add(trevoButton, i, 0);
                                }
                                specialGrid.setVisible(true);
                            }
                            case "Dia de Sorte" -> {
                                specialPremiadoLabel.setText("Mês Premiado");
                                specialPremiadoLabel.setVisible(true);
                                // Add single month button
                                Button monthButton = new Button("Mês");
                                monthButton.setPrefWidth(100);
                                specialGrid.add(monthButton, 0, 0);
                                specialGrid.setVisible(true);
                            }
                            default -> {
                                specialPremiadoLabel.setVisible(false);
                                specialGrid.setVisible(false);
                            }
                        }
                        break;
                    }
                }
            }
        });
        rightPane.setPadding(new javafx.geometry.Insets(5));
        rightPane.getChildren().addAll(numeroPremiadoLabel, numbersGrid, specialPremiadoLabel, specialGrid);

        // Initialize trevos GridPane
        trevos = new GridPane();
        trevos.setHgap(5);
        trevos.setVgap(5);

        Button simResult = new Button("Simular Resultado");
        simResult.setPrefWidth(200);

        simResult.setOnAction(event -> {
            String selectedGame = gameChoice.getValue();
            if (selectedGame != null && !selectedGame.equals("Escolha um jogo") && !selectedGame.equals("Jogo não encontrado")) {
                String gameName = selectedGame.split(" \\|")[0];
                for (GameMode mode : gameModes) {
                    if (mode.name.equals(gameName)) {
                        currentGameMode = mode;
                        result = new Result();
                        Game simGame = result.simulateResult(mode);

                        // Update selected values with simulated results
                        selectedNumbers = simGame.games[0];
                        if (mode.name.equals("Super Sete")) {
                            selectedNumbers = Arrays.stream(simGame.games[0])
                                    .filter(s -> !s.equals("|"))
                                    .toArray(String[]::new);
                            updateSuperSeteState(selectedNumbers);
                        }

                        // Initialize special elements if needed
                        if (mode.name.equals("+Milionária")) {
                            selectedTrevos = simGame.maisMilionaria.trevos[0];
                        } else if (mode.name.equals("Dia de Sorte")) {
                            luckyMonth = simGame.diaDeSorte.month[0];
                        }

                        // Update colors in left pane first
                        updateButtonColors();

                        // Then update displays
                        if (mode.name.equals("Super Sete")) {
                            updateSuperSeteState(selectedNumbers);
                        } else {
                            updateResultDisplay(selectedNumbers, numbersGrid);
                        }
                        if (mode.name.equals("+Milionária")) {
                            updateTrevoDisplay(selectedTrevos, specialGrid);
                        } else if (mode.name.equals("Dia de Sorte")) {
                            updateMonthDisplay(luckyMonth, specialGrid);
                        }

                        // Update result object
                        result.championNumbers = selectedNumbers;
                        result.championTrevos = selectedTrevos;
                        result.luckyMonth = luckyMonth;

                        debugState();
                        break;
                    }
                }
            }
        });

        HBox hboxGen = new HBox(5);
        hboxGen.getChildren().addAll(gameChoice, simResult);

        genResultNumbers = new GridPane();
        genResultNumbers.setHgap(5);
        genResultNumbers.setVgap(5);
        Button resultLabel = new Button("Escolha um jogo");
        resultLabel.setPrefWidth(500);
        resultLabel.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER_LEFT;");

        Button generateResultButton = new Button("Gerar Resultado");
        generateResultButton.setPrefWidth(505);

        generateResultButton.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            Stage stage = (Stage) generateResultButton.getScene().getWindow();
            alert.initOwner(stage);
            stage.getIcons().add(icon);
            alert.setTitle("Confirmar Geração de Resultado");
            alert.setHeaderText("Deseja realmente gerar o resultado?");
            alert.setContentText("Ao confirmar, o resultado será gerado e salvo em um arquivo.");

            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    String selectedGame = gameChoice.getValue();
                    // Split by pipe character and trim whitespace
                    String[] mainParts = selectedGame.split("\\|");
                    String gameName = mainParts[0].trim();

                    // Extract date and time parts more safely
                    String[] dateAndTime = mainParts[1].trim().split(" ");
                    String date = dateAndTime[0];
                    String time = mainParts[2].trim();

                    // Convert display date format (29/10/2024) to filename format (2024-10-29)
                    String[] dateParts = date.split("/");
                    String[] timeParts = time.split(":");
                    String fileDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
                    String fileTime = timeParts[0] + "-" + timeParts[1] + "-" + timeParts[2];

                    Game game = Game.loadFromFile(SRC_FOLDER_PATH_GAMES + gameName + "_" + fileDate + "_" + fileTime + ".json");

                    // Create result directory if it doesn't exist
                    createDirectoryIfNotExists(SRC_FOLDER_PATH_RESULTS);

                    // Generate current date formatted for the new result file
                    String generationDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

                    // Create the result file name
                    String resultFileName = gameName + "_" + generationDate + ".json";
                    String filePath = SRC_FOLDER_PATH_RESULTS + resultFileName;

                    // Create and save the result
                    Result resultToSave = new Result(game, selectedNumbers, selectedTrevos, luckyMonth);

                    // Create file in path
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }

                    resultToSave.saveToFile(filePath);
                    System.out.println("\nResult saved successfully to: " + filePath);
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    stage = (Stage) successAlert.getDialogPane().getScene().getWindow();
                    stage.getIcons().add(icon);
                    successAlert.setTitle("Jogo Salvo");
                    successAlert.setHeaderText("Jogo salvo com sucesso!");
                    successAlert.setContentText("Arquivo salvo em:\n" + filePath);
                    successAlert.showAndWait();
                    System.out.println("\nResult saved successfully to: " + filePath);
                    refreshResultsList(resultsButtonsContainer);
                } catch (Exception e) {
                    System.err.println("Error saving result: " + e.getMessage());
                }
            }
        });

        gameChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals("Jogo não encontrado")) {
                String gameName = newValue.split(" \\|")[0];
                for (GameMode mode : gameModes) {
                    if (mode.name.equals(gameName)) {
                        currentGameMode = mode;

                        // Initialize arrays only once with proper size
                        if (selectedNumbers == null || selectedNumbers.length != mode.minSelections) {
                            selectedNumbers = mode.name.equals("Super Sete")
                                    ? new String[7]
                                    : new String[mode.minSelections];
                            Arrays.fill(selectedNumbers, "");
                        }

                        // Initialize special elements only when needed and not already initialized
                        if (mode.name.equals("+Milionária")) {
                            if (selectedTrevos == null || selectedTrevos.length != mode.maisMilionaria.minTrevos) {
                                selectedTrevos = new String[mode.maisMilionaria.minTrevos];
                                Arrays.fill(selectedTrevos, ""); // Initialize with empty strings
                            }
                        } else {
                            selectedTrevos = new String[0];
                        }

                        // In the month selection logic, modify the luckyMonth initialization:
                        if (mode.name.equals("Dia de Sorte")) {
                            luckyMonth = ""; // Initialize with empty string
                        }
                        genResultNumbers.getChildren().clear();
                        leftPane.getChildren().clear();
                        leftPane.getChildren().addAll(resultLabel, hboxGen);

                        // Inside the GenResult() method, modify the Super Sete selection logic:
                        if (mode.name.equals("Super Sete")) {

                            // For Super Sete columns
                            for (int col = 0; col < 7; col++) {
                                Button columnLabel = new Button("C " + (col + 1));
                                columnLabel.setDisable(true);
                                columnLabel.setPrefWidth(80);
                                columnLabel.setStyle("-fx-background-color: #000000; -fx-text-fill: #ffffff;");
                                genResultNumbers.add(columnLabel, col, 0);

                                final int currentCol = col;

                                for (int num = 0; num < 10; num++) {
                                    Button numberButton = new Button(String.format("%d", num));
                                    numberButton.setPrefWidth(80);

                                    numberButton.setOnAction(e -> {
                                        // If clicking the same number that's already selected, clear it
                                        if (selectedNumbers[currentCol] != null && selectedNumbers[currentCol].equals(numberButton.getText())) {
                                            selectedNumbers[currentCol] = "";
                                        } else {
                                            selectedNumbers[currentCol] = numberButton.getText();
                                        }
                                        updateSuperSeteState(selectedNumbers);
                                        debugState();
                                    });

                                    genResultNumbers.add(numberButton, col, num + 1);
                                }
                            }
                            leftPane.getChildren().add(genResultNumbers);
                        } else {
                            // For regular numbers in non-Super Sete games
                            List<Button> buttons = numbersButtons(mode);
                            int columns = 10;
                            for (int i = 0; i < buttons.size(); i++) {
                                int row = i / columns;
                                int col = i % columns;
                                Button button = buttons.get(i);

                                // Add debug action
                                button.setOnAction(e -> {
                                    String number = button.getText();
                                    List<String> numbersList = new ArrayList<>();

                                    // Build list from existing selected numbers
                                    for (String n : selectedNumbers) {
                                        if (n != null && !n.isEmpty()) {
                                            numbersList.add(n);
                                        }
                                    }

                                    // Toggle selection with proper array management
                                    if (numbersList.contains(number)) {
                                        numbersList.remove(number);
                                    } else if (numbersList.size() < currentGameMode.minSelections) {
                                        numbersList.add(number);
                                    }

                                    Collections.sort(numbersList);
                                    Arrays.fill(selectedNumbers, "");
                                    for (int j = 0; j < numbersList.size(); j++) {
                                        selectedNumbers[j] = numbersList.get(j);
                                    }
                                    updateButtonColors();
                                    updateResultDisplay(selectedNumbers, numbersGrid);
                                    if (result == null) {
                                        result = new Result();
                                    }
                                    result.championNumbers = selectedNumbers;
                                    debugState();
                                });

                                genResultNumbers.add(button, col, row);
                            }
                            genResultNumbers.setHgap(5);
                            genResultNumbers.setVgap(5);
                            leftPane.getChildren().add(genResultNumbers);
                        }

                        // Add special elements (+Milionária and Dia de Sorte)
                        if (mode.name.equals("+Milionária")) {
                            Button trevosLabel = new Button("Trevos");
                            trevosLabel.setPrefWidth(500);
                            trevosLabel.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER_LEFT;");
                            GridPane trevosGrid = new GridPane();
                            // For Trevos (+Milionária)
                            List<Button> trevosButtons = trevosButtons(mode);
                            for (Button trevoButton : trevosButtons) {
                                trevoButton.setOnAction(e -> {
                                    String trevo = trevoButton.getText();
                                    List<String> trevosList = new ArrayList<>();

                                    // Build list from existing selected trevos
                                    for (String t : selectedTrevos) {
                                        if (t != null && !t.isEmpty()) {
                                            trevosList.add(t);
                                        }
                                    }

                                    // Toggle selection with proper array management
                                    if (trevosList.contains(trevo)) {
                                        trevosList.remove(trevo);
                                    } else if (trevosList.size() < currentGameMode.maisMilionaria.minTrevos) {
                                        trevosList.add(trevo);
                                    }

                                    Collections.sort(trevosList);
                                    selectedTrevos = new String[currentGameMode.maisMilionaria.minTrevos];
                                    Arrays.fill(selectedTrevos, ""); // Fill with empty strings first
                                    for (int i = 0; i < trevosList.size(); i++) {
                                        selectedTrevos[i] = trevosList.get(i);
                                    }
                                    updateButtonColors();
                                    updateTrevoDisplay(selectedTrevos, specialGrid);
                                    if (result == null) {
                                        result = new Result();
                                    }
                                    result.championTrevos = selectedTrevos;
                                    debugState();
                                });
                            }
                            addGame(trevosGrid, trevosButtons);
                            leftPane.getChildren().addAll(trevosLabel, trevosGrid);
                        }

                        if (mode.name.equals("Dia de Sorte")) {
                            Button monthsLabel = new Button("Mês:");
                            monthsLabel.setPrefWidth(500);
                            monthsLabel.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: CENTER_LEFT;");
                            GridPane monthsGrid = new GridPane();
                            // For Months (Dia de Sorte)
                            List<Button> monthButtons = monthsButtons(mode);
                            for (Button monthButton : monthButtons) {
                                monthButton.setOnAction(e -> {
                                    String month = monthButton.getText();

                                    // Toggle selection
                                    if (month.equals(luckyMonth)) {
                                        luckyMonth = "";
                                    } else {
                                        luckyMonth = month;
                                    }

                                    updateButtonColors();
                                    updateMonthDisplay(luckyMonth, specialGrid);
                                    if (result == null) {
                                        result = new Result();
                                    }
                                    result.luckyMonth = luckyMonth;
                                    debugState();
                                });
                            }
                            addGame(monthsGrid, monthButtons);
                            leftPane.getChildren().addAll(monthsLabel, monthsGrid);
                        }

                        // Add generate result button
                        leftPane.getChildren().add(generateResultButton);
                        break;
                    }
                }
            }
        });

        // Add components to left pane
        leftPane.getChildren().addAll(resultLabel, hboxGen, genResultNumbers);
        leftPane.getChildren().add(generateResultButton);

        // Configure ScrollPanes
        ScrollPane leftScroll = new ScrollPane(leftPane);
        leftScroll.setFitToWidth(true);
        leftScroll.setFitToHeight(true);
        ScrollPane rightScroll = new ScrollPane(rightPane);
        rightScroll.setFitToWidth(true);
        rightScroll.setFitToHeight(true);

        // Add scroll panes to grid
        gridPane.add(leftScroll, 0, 0);
        gridPane.add(rightScroll, 1, 0);

        // Set constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        gridPane.getColumnConstraints().addAll(col1, col2);

        RowConstraints row = new RowConstraints();
        row.setVgrow(Priority.ALWAYS);
        gridPane.getRowConstraints().add(row);

        return gridPane;
    }

    public void debugState() {
        System.out.println("\n=== Result Debug State ===");
        System.out.println("Selected Numbers: " + Arrays.toString(selectedNumbers));
        if (currentGameMode == gameModes[0]) {
            System.out.println("Selected Trevos: " + Arrays.toString(selectedTrevos));
        }
        if (currentGameMode == gameModes[6]) {
            System.out.println("Lucky Month: " + luckyMonth);
        }
        System.out.println("========================\n");
    }

    private void updateResultDisplay(String[] numbers, GridPane numbersGrid) {
        numbersGrid.getChildren().clear();

        if (currentGameMode.name.equals("Super Sete")) {
            int currentCol = 0;
            List<String> currentColumnNumbers = new ArrayList<>();

            for (String number : numbers) {
                if (number.equals("|")) {
                    // Add column header
                    Button columnLabel = new Button("Coluna " + (currentCol + 1));
                    columnLabel.setDisable(true);
                    columnLabel.setPrefWidth(80);
                    columnLabel.setStyle("-fx-background-color: #000000; -fx-text-fill: #ffffff;");
                    numbersGrid.add(columnLabel, currentCol, 0);

                    // Add numbers for this column
                    for (int i = 0; i < currentColumnNumbers.size(); i++) {
                        Button numberButton = new Button(currentColumnNumbers.get(i));
                        numberButton.setPrefWidth(80);
                        numbersGrid.add(numberButton, currentCol, i + 1);
                    }

                    currentCol++;
                    currentColumnNumbers.clear();
                } else {
                    currentColumnNumbers.add(number);
                }
            }

            // Handle the last column
            if (!currentColumnNumbers.isEmpty()) {
                Button columnLabel = new Button("Coluna " + (currentCol + 1));
                columnLabel.setDisable(true);
                columnLabel.setPrefWidth(80);
                columnLabel.setStyle("-fx-background-color: #000000; -fx-text-fill: #ffffff;");
                numbersGrid.add(columnLabel, currentCol, 0);

                for (int i = 0; i < currentColumnNumbers.size(); i++) {
                    Button numberButton = new Button(currentColumnNumbers.get(i));
                    numberButton.setPrefWidth(80);
                    numbersGrid.add(numberButton, currentCol, i + 1);
                }
            }
        } else {
            // Original code for other game modes
            for (int i = 0; i < numbers.length; i++) {
                Button button = new Button(numbers[i]);
                button.setPrefWidth(40);
                numbersGrid.add(button, i % 10, i / 10);
            }
        }
    }

    private void updateTrevoDisplay(String[] trevos, GridPane specialGrid) {
        specialGrid.getChildren().clear();
        for (int i = 0; i < currentGameMode.maisMilionaria.minTrevos; i++) {
            Button button = new Button(trevos[i] != null ? trevos[i] : "");
            button.setPrefWidth(40);
            specialGrid.add(button, i, 0);
        }
    }

    private void updateSuperSeteRightPane(String[] numbers, GridPane numbersGrid) {
        numbersGrid.getChildren().clear();

        for (int i = 0; i < 7; i++) {
            Button columnLabel = new Button("C " + (i + 1));
            columnLabel.setDisable(true);
            columnLabel.setPrefWidth(80);
            columnLabel.setStyle("-fx-background-color: #000000; -fx-text-fill: #ffffff;");
            numbersGrid.add(columnLabel, i, 0);

            Button numberButton = new Button(numbers != null && i < numbers.length ? numbers[i] : "");
            numberButton.setPrefWidth(80);
            numbersGrid.add(numberButton, i, 1);
        }
    }

    private void updateSuperSeteState(String[] numbers) {
        // Update the selected numbers array
        selectedNumbers = numbers;

        // Update button colors in left pane
        for (Node node : genResultNumbers.getChildren()) {
            if (node instanceof Button button) {
                int column = GridPane.getColumnIndex(node);
                int row = GridPane.getRowIndex(node);

                // Skip column headers
                if (row == 0) {
                    continue;
                }

                if (column < numbers.length && numbers[column] != null
                        && numbers[column].equals(button.getText())) {
                    button.setStyle("-fx-background-color: #90EE90;");
                } else {
                    button.setStyle("");
                }
            }
        }

        // Update right pane display
        updateSuperSeteRightPane(numbers, numbersGrid);

        // Update result object
        if (result == null) {
            result = new Result();
        }
        result.championNumbers = numbers;

        debugState();
    }

    private void updateMonthDisplay(String month, GridPane specialGrid) {
        specialGrid.getChildren().clear();
        Button button = new Button(month != null ? month : "");
        button.setPrefWidth(100);
        specialGrid.add(button, 0, 0);
    }

    private void updateButtonColors() {
        // Update number buttons in left pane
        if (currentGameMode != null && currentGameMode.name.equals("Super Sete")) {
            String[] columns = String.join("|", selectedNumbers).split("\\|");
            for (Node node : genResultNumbers.getChildren()) {
                if (node instanceof Button button) {
                    int column = GridPane.getColumnIndex(node);
                    int row = GridPane.getRowIndex(node);

                    // Skip column headers
                    if (row == 0) {
                        continue;
                    }

                    // Get the selected number for this column
                    String selectedNumber = columns[column];

                    // Check if this number is selected for this specific column
                    if (selectedNumber != null && !selectedNumber.isEmpty()
                            && selectedNumber.equals(button.getText())) {
                        button.setStyle("-fx-background-color: #90EE90;");
                    } else {
                        button.setStyle("");
                    }
                }
            }
        } else {
            // Rest of the existing code for other game modes
            if (selectedNumbers != null) {
                for (Node node : genResultNumbers.getChildren()) {
                    if (node instanceof Button button) {
                        if (Arrays.asList(selectedNumbers).contains(button.getText())) {
                            button.setStyle("-fx-background-color: #90EE90;");
                        } else {
                            button.setStyle("");
                        }
                    }
                }
            }
        }

        // Update trevo buttons in left pane
        if (selectedTrevos != null && currentGameMode != null && currentGameMode.name.equals("+Milionária")) {
            for (Node node : genResultNumbers.getParent().getChildrenUnmodifiable()) {
                if (node instanceof GridPane && node != genResultNumbers) {
                    GridPane trevosGrid = (GridPane) node;
                    for (Node tNode : trevosGrid.getChildren()) {
                        if (tNode instanceof Button button) {
                            if (Arrays.asList(selectedTrevos).contains(button.getText())) {
                                button.setStyle("-fx-background-color: #90EE90;");
                            } else {
                                button.setStyle("");
                            }
                        }
                    }
                }
            }
        }

        // Update month buttons in left pane
        if (luckyMonth != null && currentGameMode != null && currentGameMode.name.equals("Dia de Sorte")) {
            for (Node node : genResultNumbers.getParent().getChildrenUnmodifiable()) {
                if (node instanceof GridPane && node != genResultNumbers) {
                    GridPane monthsGrid = (GridPane) node;
                    for (Node mNode : monthsGrid.getChildren()) {
                        if (mNode instanceof Button button) {
                            if (button.getText().equals(luckyMonth)) {
                                button.setStyle("-fx-background-color: #90EE90;");
                            } else {
                                button.setStyle("");
                            }
                        }
                    }
                }
            }
        }
    }

    private ChoiceBox<String> gameChoice() {
        ChoiceBox<String> gameChoice = new ChoiceBox<>();

        Thread fileWatcher = new Thread(() -> {
            while (true) {
                Platform.runLater(() -> {
                    File folder = new File(SRC_FOLDER_PATH_GAMES);
                    File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

                    if (files != null && files.length > 0) {
                        String currentSelection = gameChoice.getValue();
                        List<String> currentItems = new ArrayList<>(gameChoice.getItems());
                        List<String> newItems = new ArrayList<>();

                        for (File file : files) {
                            newItems.add(file.getName().split("_")[0] + " | "
                                    + file.getName().split("_")[1].split("-")[2] + "/"
                                    + file.getName().split("_")[1].split("-")[1] + "/"
                                    + file.getName().split("_")[1].split("-")[0] + " | "
                                    + file.getName().split("_")[2].split("-")[0] + ":"
                                    + file.getName().split("_")[2].split("-")[1] + ":"
                                    + file.getName().split("_")[2].split("-")[2].replace(".json", ""));
                        }

                        if (!currentItems.equals(newItems)) {
                            gameChoice.getItems().clear();
                            gameChoice.getItems().addAll(newItems);

                            if (newItems.contains(currentSelection)) {
                                gameChoice.setValue(currentSelection);
                            } else {
                                gameChoice.setValue("Escolha um jogo");
                            }
                        }
                    } else {
                        gameChoice.getItems().clear();
                        gameChoice.getItems().add("Jogo não encontrado");
                        gameChoice.setValue("Jogo não encontrado");
                    }
                });

                try {
                    WatchService watchService = FileSystems.getDefault().newWatchService();
                    Path path = Paths.get(SRC_FOLDER_PATH_GAMES);
                    path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.ENTRY_MODIFY);

                    WatchKey key = watchService.take();
                    key.pollEvents();
                    key.reset();
                } catch (IOException | InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        fileWatcher.setDaemon(true);
        fileWatcher.start();

        return gameChoice;
    }

    private void setStage(Scene scene, Stage window) {
        window.setTitle("LotoSorte");
        window.getIcons().add(icon);
        window.setScene(scene);
        window.setWidth(width / 1.2);
        window.setHeight(height / 1.5);
        window.show();
    }
}
