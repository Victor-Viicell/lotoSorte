package com.app;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {

    // Get Executer directiory
    public static final String SRC_FOLDER = System.getProperty("user.dir");
    public static final String SRC_FOLDER_PATH = SRC_FOLDER + File.separator + "src" + File.separator + "main"
            + File.separator + "java" + File.separator + "com" + File.separator + "app" + File.separator;
    public static final String SRC_FOLDER_PATH_GAMES = SRC_FOLDER_PATH + "Games" + File.separator;
    public static final String SRC_FOLDER_PATH_RESULTS = SRC_FOLDER_PATH + "Results" + File.separator;
    public static final String SRC_FOLDER_PATH_CONFIGS = SRC_FOLDER_PATH_GAMES + "Configs" + File.separator;

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

    // Get system window size
    public static double width = Screen.getPrimary().getBounds().getWidth();
    public static double height = Screen.getPrimary().getBounds().getHeight();
    private GridPane gameNumbers;

    Stage window;
    private static final Image icon = new Image(App.class.getResourceAsStream("icon.png"));

    // Make mainTabPane an instance variable
    private TabPane mainTabPane;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(@SuppressWarnings("exports") Stage stage) throws Exception {
        window = stage;

        // Initialize mainTabPane
        mainTabPane = mainTabPane();

        VBox sideMenu = sideMenu();

        BorderPane layout = new BorderPane();
        layout.setLeft(sideMenu);
        layout.setCenter(mainTabPane);
        layout.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        Scene scene = new Scene(layout);
        setStage(scene, window);
    }

    private TabPane mainTabPane() {
        TabPane mTabPane = new TabPane();

        // You can start with an empty TabPane or add a default tab
        // For example, a welcome tab
        Tab welcomeTab = new Tab("Welcome");
        welcomeTab.setClosable(false);
        welcomeTab.setContent(new VBox());
        mTabPane.getTabs().add(welcomeTab);

        return mTabPane;
    }

    private VBox sideMenu() {
        VBox sideMenu = new VBox();
        sideMenu.setPrefWidth(width / 6);
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

        sideMenu.getChildren().addAll(sideTabPane, button1, button2);
        return sideMenu;
    }

    private void loadGameTab(File gameFile) {
        try {
            Game loadedGame = Game.loadFromFile(gameFile.getAbsolutePath());
            Tab gameTab = new Tab(gameFile.getName().replace(".json", ""));
            gameTab.setClosable(true);
            GridPane gameTabPane = new GridPane();
            gameTabPane.setPrefHeight(height);
            VBox gameContent = new VBox(5);
            gameContent.setPadding(new javafx.geometry.Insets(5));
            gameContent.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
            gameContent.setPrefWidth(400);
            gameContent.setPrefHeight(height);
            for (int i = 0; i < loadedGame.games.length; i++) {
                GridPane buttonsGrid = new GridPane();
                Label gameNumber = new Label("Game: " + (i + 1));
                String[] game = loadedGame.games[i];
                List<Button> buttons = numbersButtons(game);
                addGame(buttonsGrid, buttons);
                gameContent.getChildren().addAll(gameNumber, buttonsGrid);
                if (loadedGame.gameMode.name.equals("+Milionária")) {
                    String[] trevos = loadedGame.maisMilionaria.trevos[i];
                    List<Button> trevosButtons = numbersButtons(trevos);
                    GridPane trevosGrid = new GridPane();
                    Label trevosLabel = new Label("Trevos:");
                    addGame(trevosGrid, trevosButtons);
                    gameContent.getChildren().addAll(trevosLabel, trevosGrid);
                }
                if (loadedGame.gameMode.name.equals("Dia de Sorte")) {
                    String month = loadedGame.diaDeSorte.month[i];
                    Button monthButtons = new Button(month);
                    GridPane monthGrid = new GridPane();
                    Label monthLabel = new Label("Mês:");
                    monthGrid.add(monthButtons, 0, 0);
                    gameContent.getChildren().addAll(monthLabel, monthGrid);
                }
                // add a vertical line
                if (i < loadedGame.games.length - 1) {
                    Separator separator = new Separator();
                    separator.setPrefWidth(gameContent.getPrefWidth());
                    gameContent.getChildren().add(separator);
                }
            }
            VBox gameData = new VBox(5);
            gameData.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
            gameData.setPrefWidth(300);
            gameData.setPrefHeight(height);
            gameData.setPadding(new javafx.geometry.Insets(5));

            Label gameModeLabel = new Label("Game Mode: " + loadedGame.gameMode.name);
            Label numberAmountLabel = new Label("Number Amount: " + loadedGame.numbers);
            Label gameAmountLabel = new Label("Game Amount: " + loadedGame.amount);

            gameData.getChildren().addAll(gameModeLabel, numberAmountLabel, gameAmountLabel);

            // Add more details about the loaded game as needed
            ScrollPane scrollPane = new ScrollPane(gameContent);
            ScrollPane scrollPane2 = new ScrollPane(gameData);
            gameTabPane.add(scrollPane, 0, 0);
            gameTabPane.add(scrollPane2, 1, 0);
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
            File gamesFolder = new File(SRC_FOLDER_PATH_GAMES);
            File[] gameFiles = gamesFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

            if (gameFiles != null) {
                for (File gameFile : gameFiles) {
                    VBox gameRow = new VBox(5);
                    //remove .Json from name
                    Button gameButton = new Button(gameFile.getName().replace(".json", ""));
                    gameButton.setPrefWidth(width / 6 - 26);
                    gameButton.setOnAction(e -> loadGameTab(gameFile));

                    Button deleteButton = new Button("Deletar");
                    deleteButton.setOnAction(e -> deleteGame(gameFile, buttonsContainer));

                    gameRow.getChildren().addAll(gameButton, deleteButton);
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
        scrollPaneGenResults.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        tabGenResults.setContent(scrollPaneGenResults);
        tabGenResults.setClosable(false);
    }

    private void updateGameNumbers(ChoiceBox<String> choiceBox) {
        // Clear existing buttons
        gameNumbers.getChildren().clear();

        int selectedIndex = choiceBox.getSelectionModel().getSelectedIndex();
        List<Button> buttons = numbersButtons(gameModes[selectedIndex]);

        int columns = 10; // Define the number of columns you want

        for (int i = 0; i < buttons.size(); i++) {
            int row = i / columns;
            int col = i % columns;
            gameNumbers.add(buttons.get(i), col, row);
        }
        gameNumbers.setHgap(5);
        gameNumbers.setVgap(5);
    }

    private ChoiceBox<String> numberAmount(GameMode gameMode) {
        ChoiceBox<String> numberAmount = new ChoiceBox<>();
        for (int i = gameMode.minSelections; i <= gameMode.maxSelections; i++) {
            numberAmount.getItems().add(i + " Números");
        }
        numberAmount.setValue(gameMode.minSelections + " Números");
        numberAmount.setPrefWidth(168);

        // Set initial value
        currentNumberAmountValue = gameMode.minSelections;

        // Add listener to update currentNumberAmountValue
        numberAmount.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentNumberAmountValue = Integer.parseInt(newValue.split(" ")[0]);
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

    private void openTab(String tabName) {
        // Check if the tab already exists
        for (Tab tab : mainTabPane.getTabs()) {
            if (tab.getText().equals(tabName)) {
                // Select the existing tab
                mainTabPane.getSelectionModel().select(tab);
                return;
            }
        }

        // Create a new tab if it doesn't exist
        Tab newTab = new Tab(tabName);
        newTab.setClosable(true); // Allow the tab to be closed by the user

        // Set the content of the tab based on the tabName
        switch (tabName) {
            case "Gerar Jogo":
                newTab.setContent(GenGame());
                break;
            case "Gerar Resultado":
                newTab.setContent(GenResult());
                break;
            default:
                // Default content if tabName doesn't match any case
                newTab.setContent(new VBox());
                break;
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

        VBox leftPane = new VBox();
        leftPane.setMinWidth(345);
        leftPane.setMinHeight(345);
        leftPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        gridPane.add(leftPane, 0, 0);

        leftPane.setPadding(new javafx.geometry.Insets(5, 5, 5, 5));
        leftPane.setSpacing(5);

        // Create the game mode choice box
        ChoiceBox<String> choiceBox = gameModeChoice();
        ChoiceBox<String> numberAmount = numberAmount(gameModes[choiceBox.getSelectionModel().getSelectedIndex()]);

        HBox gameModeBox = new HBox();
        gameModeBox.setSpacing(5);
        gameModeBox.getChildren().addAll(choiceBox, numberAmount);

        // Initialize gameNumbers
        gameNumbers = new GridPane();
        updateGameNumbers(choiceBox);

        // Initialize trevos
        GridPane trevos = new GridPane();
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

        GridPane monthsButtons = new GridPane();
        updateMonths(choiceBox, monthsButtons);
        Label monthsLabel = new Label("Editar Meses:");

        Button generateButton = new Button("Gerar Jogos");
        generateButton.setPrefWidth(169);
        choiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            GameMode selectedGameMode = gameModes[newValue.intValue()];
            numberAmount.getItems().clear();
            for (int i = selectedGameMode.minSelections; i <= selectedGameMode.maxSelections; i++) {
                numberAmount.getItems().add(i + " Números");
            }
            numberAmount.setValue(selectedGameMode.minSelections + " Números");

            // Update the gameNumbers and other UI elements
            updateGameNumbers(choiceBox);
            updateTrevos(choiceBox, trevos);
            updateMonths(choiceBox, monthsButtons);
            numberAmount.setValue(selectedGameMode.minSelections + " Números");
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
                        trevoAmountValue = Integer.parseInt(selectedTrevoAmount.split(" ")[0]);
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
                    // Save the game to a file
                    game.saveToFile(filePath);
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

        Label label1 = new Label("Escolha o Jogo:");
        label1.setFont(new Font(14));
        Label label2 = new Label("Editar Numeros:");
        label2.setFont(new Font(14));

        leftPane.getChildren().addAll(label1, gameModeBox, label2, gameNumbers);
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
        int columns = 10; // Define o número de colunas desejado

        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            if (button.getText().startsWith("Coluna")) {
                // Se o botão é uma coluna, adiciona em uma nova linha
                currentRow++;
                currentCol = 0;
                gridPane.add(button, currentCol, currentRow);
                currentCol++;
            } else {
                // Para botões numéricos, usa a lógica de grade
                int row = currentRow;
                int col = currentCol % columns;
                gridPane.add(button, col, row);
                currentCol++;

                // Se atingir o número máximo de colunas, avança para a próxima linha
                if (currentCol % columns == 0) {
                    currentRow++;
                    currentCol = 0;
                }
            }
        }

        // Configura o espaçamento e a visibilidade do GridPane
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
            Button button = new Button(game[currentNumber]);
            button.setId("button" + currentNumber);
            if ("|".equals(game[currentNumber])) {
                game[currentNumber] = "Coluna " + col;
                col++;
                button.setDisable(true);
                button.setStyle("-fx-background-color: #000000; -fx-text-fill: #ffffff;");
                button.setText(game[currentNumber]);
            }

            buttonList.add(button);
            currentNumber++;
        }

        return buttonList;
    }

    private VBox GenResult() {
        VBox homeTab = new VBox();
        homeTab.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));

        // Add your content here
        return homeTab;
    }

    private void setStage(Scene scene, Stage window) {
        window.setTitle("LotoSorte");
        window.getIcons().add(icon);
        window.setScene(scene);
        window.setWidth(width / 1.5);
        window.setHeight(height / 1.5);
        window.show();
    }
}
