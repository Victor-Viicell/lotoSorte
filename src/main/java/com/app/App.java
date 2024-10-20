package com.app;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
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

        MenuBar topMenu = topMenu();

        // Initialize mainTabPane
        mainTabPane = mainTabPane();

        VBox sideMenu = sideMenu();

        BorderPane layout = new BorderPane();
        layout.setLeft(sideMenu);
        layout.setTop(topMenu);
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

    private MenuBar topMenu() {
        MenuBar topMenu = new MenuBar();
        Menu arquivoButton = new Menu("Arquivo");
        Menu editarButton = new Menu("Editar");
        Menu ajudaButton = new Menu("Ajuda");

        topMenu.getMenus().addAll(arquivoButton, editarButton, ajudaButton);
        return topMenu;
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

        Button button3 = new Button("Configurações");
        button3.setPrefWidth(width);

        // Add action handlers to buttons
        button1.setOnAction(e -> openTab("Gerar Jogo"));
        button2.setOnAction(e -> openTab("Gerar Resultado"));
        button3.setOnAction(e -> openTab("Configurações"));

        sideMenu.getChildren().addAll(sideTabPane, button1, button2, button3);
        return sideMenu;
    }

    private void jogosFilesTab(Tab tabGenGame) {
        ScrollPane scrollPaneGenGame = new ScrollPane();
        scrollPaneGenGame.setPrefWidth(width);
        scrollPaneGenGame.setPrefHeight(height);
        scrollPaneGenGame.setPadding(new javafx.geometry.Insets(5));
        scrollPaneGenGame.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
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
            case "Configurações":
                newTab.setContent(Config());
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

        Button generateButton = new Button("Gerar Jogos");
        generateButton.setPrefWidth(169);

        HBox buttonBox = new HBox();

        GridPane monthsButtons = new GridPane();
        updateMonths(choiceBox, monthsButtons);
        Label monthsLabel = new Label("Editar Meses:");

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

    private VBox GenResult() {
        VBox homeTab = new VBox();
        homeTab.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));

        // Add your content here
        return homeTab;
    }

    private VBox Config() {
        VBox homeTab = new VBox();
        homeTab.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));

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
