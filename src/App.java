
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class App {

    public static void main(String[] args) {
        Runnable runner = () -> {

        };

        EventQueue.invokeLater(runner);
    }

    // Methods
    public static GeneratedGame genGame(GameMode gameMode, int amount) {
        GeneratedGame generatedGame = new GeneratedGame();
        generatedGame.gameMode = gameMode.nameString;

        if (gameMode.nameString.equals("Super-Sete")) {
            generatedGame.generatedNumbers = new String[gameMode.superSevenCollumsInteger][amount / gameMode.superSevenCollumsInteger];
            for (int i = 0; i < gameMode.superSevenCollumsInteger; i++) {
                generateUniqueNumbers(gameMode, generatedGame.generatedNumbers[i], amount / gameMode.superSevenCollumsInteger);
            }
        } else {
            generatedGame.generatedNumber = new String[amount];
            generateUniqueNumbers(gameMode, generatedGame.generatedNumber, amount);
        }

        if (gameMode.nameString.equals("Dia de Sorte")) {
            int randomIndex = new Random().nextInt(gameMode.luckyDayMonthsString.length);
            generatedGame.luckyDayMonthString = gameMode.luckyDayMonthsString[randomIndex];
        }
        return generatedGame;
    }

    private static void generateUniqueNumbers(GameMode gameMode, String[] result, int amount) {
        ArrayList<LuckNumber> availableNumbers = new ArrayList<>(Arrays.asList(gameMode.numbersLuckNumber));
        Random random = new Random();
        for (int i = 0; i < amount; i++) {
            double totalWeight = 0;
            for (LuckNumber ln : availableNumbers) {
                totalWeight += ln.numbersPercentageFloat;
            }
            double randomValue = random.nextDouble() * totalWeight;
            double cumulativeWeight = 0.0;

            for (int j = 0; j < availableNumbers.size(); j++) {
                cumulativeWeight += availableNumbers.get(j).numbersPercentageFloat;
                if (randomValue <= cumulativeWeight) {
                    result[i] = availableNumbers.get(j).numberString;
                    availableNumbers.remove(j);
                    break;
                }
            }
        }
    }

    public static GameMode[] gameModes() {
        GameMode[] gameModes = new GameMode[8];
        gameModes[0] = gameModeConstructor("Mega-Sena", 60, 6, 20, false, 5.00);
        gameModes[1] = gameModeConstructor("Lotofácil", 25, 15, 20, false, 3.00);
        gameModes[2] = gameModeConstructor("Quina", 80, 5, 15, false, 2.50);
        gameModes[3] = gameModeConstructor("Lotomania", 100, 50, 50, true, 3.00);
        gameModes[4] = gameModeConstructor("Timemania", 80, 10, 10, false, 3.50);
        gameModes[5] = gameModeConstructor("Dupla-Sena", 50, 6, 15, false, 2.50);
        // Special Rules
        gameModes[6] = gameModeConstructor("Dia de Sorte", 31, 7, 15, false, 2.50);
        gameModes[7] = gameModeConstructor("Super-Sete", 10, 7, 21, true, 2.50);

        return gameModes;
    }

    public static GameMode gameModeConstructor(
            String nameString,
            Integer totalNumbersInteger,
            Integer minSelectionsInteger,
            Integer maxSelectionsInteger,
            Boolean include00Boolean,
            Double cardPriceDouble
    ) {
        // Criamos um novo objeto GameMode para armazenar as informações do modo de jogo
        GameMode gameMode = new GameMode();

        gameMode.nameString = nameString;
        gameMode.totalNumbersInteger = totalNumbersInteger;
        gameMode.minSelectionsInteger = minSelectionsInteger;
        gameMode.maxSelectionsInteger = maxSelectionsInteger;
        gameMode.include00Boolean = include00Boolean;
        gameMode.cardPriceDouble = cardPriceDouble;
        gameMode.numbersLuckNumber = new LuckNumber[totalNumbersInteger];
        for (int i = 0; i < gameMode.totalNumbersInteger; i++) {
            LuckNumber luckNumber = new LuckNumber();
            if (nameString.equals("Super-Sete")) {
                luckNumber.numberString = String.valueOf(i);
            } else {
                if (include00Boolean && i == 0) {
                    luckNumber.numberString = String.format("%02d", i);
                } else {
                    luckNumber.numberString = String.format("%02d", i + 1);
                }
            }
            luckNumber.numbersPercentageFloat = 1.0f; // 100%
            gameMode.numbersLuckNumber[i] = luckNumber;
        }

        return gameMode;

    }

    // Classes
    public static class LuckNumber {

        private String numberString;
        private float numbersPercentageFloat;
    }

    public static class GameMode {

        private String nameString;
        private Integer totalNumbersInteger;
        private Integer minSelectionsInteger;
        private Integer maxSelectionsInteger;
        private Boolean include00Boolean;
        private LuckNumber[] numbersLuckNumber;
        private Double cardPriceDouble;
        private String[] luckyDayMonthsString;
        private Integer superSevenCollumsInteger;

        public GameMode() {
            this.superSevenCollumsInteger = 7;
            this.luckyDayMonthsString = new String[]{"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        }
    }

    public static class GeneratedGame {

        private String gameMode;
        private String[] generatedNumber;
        private String[] generatedNumbers[];
        private String luckyDayMonthString;
    }
}
