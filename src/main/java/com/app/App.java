package com.app;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
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

    private ChoiceBox<String> choiceBox;
    private ChoiceBox<String> numberAmount;
    private VBox rightPane;
    private GridPane trevos;
    private GridPane monthsButtons;
    @SuppressWarnings("unused")
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

    private void loadGameTab(File gameFile) {
        try {
            Game loadedGame = Game.loadFromFile(gameFile.getAbsolutePath());
            Tab gameTab = new Tab(gameFile.getName().replace(".json", ""));
            gameTab.setClosable(true);

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
            File gamesFolder = new File(SRC_FOLDER_PATH_GAMES);
            File[] gameFiles = gamesFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

            if (gameFiles != null) {
                for (File gameFile : gameFiles) {
                    VBox gameRow = new VBox(5);
                    // Remover a extensão .json do nome
                    Button gameButton = new Button(gameFile.getName().replace(".json", ""));
                    gameButton.setPrefWidth(width / 6 - 26);
                    gameButton.setOnAction(e -> loadGameTab(gameFile));

                    // Botão Deletar existente
                    Button deleteButton = new Button("Deletar");
                    deleteButton.setOnAction(e -> deleteGame(gameFile, buttonsContainer));

                    // Novo botão Copiar
                    Button copyButton = new Button("Copiar Configuração");
                    copyButton.setOnAction(e -> {
                        copyGameConfiguration(gameFile);
                    });

                    // Adicionar os botões ao layout
                    HBox buttonBox = new HBox(5);
                    buttonBox.getChildren().addAll(deleteButton, copyButton);

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
        gameNumbers.getChildren().clear();  // Clear existing buttons first

        int selectedIndex = choiceBox.getSelectionModel().getSelectedIndex();
        List<Button> buttons = numbersButtons(gameModes[selectedIndex]);

        int columns = 10;
        for (int i = 0; i < buttons.size(); i++) {
            int row = i / columns;
            int col = i % columns;
            Button button = buttons.get(i);

            // Assign an ID to the button
            button.setId("numberButton" + i);

            gameNumbers.add(button, col, row);
        }
        gameNumbers.setHgap(5);
        gameNumbers.setVgap(5);
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
        gameNumbers = new GridPane();
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

                    game.saveToFile(filePath);
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

    private void copyGameConfiguration(File gameFile) {
        try {
            // Carregar o jogo do arquivo
            Game loadedGame = Game.loadFromFile(gameFile.getAbsolutePath());
            GameMode loadedGameMode = loadedGame.gameMode;

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
        int columns = 10; // Number of desired columns

        for (Button button : buttons) {
            if (button.getText().startsWith("Coluna")) {
                // If the button is a column header, start a new row
                currentCol = 0;
                currentRow++;
                gridPane.add(button, currentCol, currentRow);
                currentCol++;
            } else {
                gridPane.add(button, currentCol, currentRow);
                currentCol++;

                // If we reach the end of a row, reset column and move to next row
                if (currentCol >= columns) {
                    currentCol = 0;
                    currentRow++;
                }
            }
        }

        // Set spacing and visibility
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
        window.setWidth(width / 1.4);
        window.setHeight(height / 1.5);
        window.show();
    }
}
