
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class App {

    public static void main(String[] args) {
        Runnable runner = () -> {
        };

        EventQueue.invokeLater(runner);
    }

    // Methods
    public static GeneratedGameFile genGameFile(GameMode gameMode, GeneratedGame[] generatedGames) {
        GeneratedGameFile generatedGameFile = new GeneratedGameFile(gameMode, generatedGames, generatedGames.length);
        generatedGameFile.calculateStatistics();
        return generatedGameFile;
    }

    public static GeneratedGame[] genMultiGames(GameMode gameMode, int amountGames, int numbersPerGame) {
        GeneratedGame[] generatedGames = new GeneratedGame[amountGames];
        for (int i = 0; i < amountGames; i++) {
            generatedGames[i] = genGame(gameMode, numbersPerGame);
        }
        return generatedGames;
    }

    public static GeneratedGame genGame(GameMode gameMode, int amount) {
        GeneratedGame generatedGame = new GeneratedGame(null);
        generatedGame.gameMode = gameMode.nameString;

        ArrayList<String> oddNumbers = new ArrayList<>();
        ArrayList<String> evenNumbers = new ArrayList<>();
        ArrayList<String> primeNumbers = new ArrayList<>();

        if (gameMode.nameString.equals("Super-Sete")) {
            generatedGame.generatedNumbers = new String[gameMode.superSevenCollumsInteger][amount / gameMode.superSevenCollumsInteger + 1];
            int remainingNumbers = amount;

            for (int i = 0; i < gameMode.superSevenCollumsInteger; i++) {
                int numbersInColumn = remainingNumbers / (gameMode.superSevenCollumsInteger - i);
                generateUniqueNumbers(gameMode, generatedGame.generatedNumbers[i], numbersInColumn);
                categorizeNumbers(generatedGame.generatedNumbers[i], oddNumbers, evenNumbers, primeNumbers);
                remainingNumbers -= numbersInColumn;
            }

            // Distribute any remaining numbers randomly
            while (remainingNumbers > 0) {
                int randomColumn = new Random().nextInt(gameMode.superSevenCollumsInteger);
                String[] column = generatedGame.generatedNumbers[randomColumn];
                int emptyIndex = findFirstEmptyIndex(column);
                if (emptyIndex != -1) {
                    generateUniqueNumbers(gameMode, column, 1);
                    categorizeNumbers(new String[]{column[emptyIndex]}, oddNumbers, evenNumbers, primeNumbers);
                    remainingNumbers--;
                }
            }
        } else {
            generatedGame.generatedNumber = new String[amount];
            generateUniqueNumbers(gameMode, generatedGame.generatedNumber, amount);
            categorizeNumbers(generatedGame.generatedNumber, oddNumbers, evenNumbers, primeNumbers);
        }

        generatedGame.oddNumbersStrings = oddNumbers.toArray(String[]::new);
        generatedGame.evenNumbersStrings = evenNumbers.toArray(String[]::new);
        generatedGame.primeNumbersStrings = primeNumbers.toArray(String[]::new);

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
        return new GameMode[]{
            new GameMode("Mega-Sena", 60, 6, 20, false, 5.00),
            new GameMode("Lotofácil", 25, 15, 20, false, 3.00),
            new GameMode("Quina", 80, 5, 15, false, 2.50),
            new GameMode("Lotomania", 100, 50, 50, true, 3.00),
            new GameMode("Timemania", 80, 10, 10, false, 3.50),
            new GameMode("Dupla-Sena", 50, 6, 15, false, 2.50),
            new GameMode("Dia de Sorte", 31, 7, 15, false, 2.50),
            new GameMode("Super-Sete", 10, 7, 21, true, 2.50)
        };
    }

    public static GameMode gameModeConstructor(
            String nameString,
            Integer totalNumbersInteger,
            Integer minSelectionsInteger,
            Integer maxSelectionsInteger,
            Boolean include00Boolean,
            Double cardPriceDouble
    ) {
        GameMode gameMode = new GameMode(nameString, totalNumbersInteger, minSelectionsInteger, maxSelectionsInteger, include00Boolean, cardPriceDouble);
        return gameMode;
    }

    public static void categorizeNumbers(String[] numbers, ArrayList<String> oddNumbers, ArrayList<String> evenNumbers, ArrayList<String> primeNumbers) {
        for (String number : numbers) {
            int num = Integer.parseInt(number);
            if (num % 2 == 0) {
                evenNumbers.add(number);
            } else {
                oddNumbers.add(number);
            }
            if (isPrime(num)) {
                primeNumbers.add(number);
            }
        }
    }

    private static int findFirstEmptyIndex(String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static class LuckNumber {

        private String numberString;
        private float numbersPercentageFloat;

        public LuckNumber(String numberString, float numbersPercentageFloat) {
            this.numberString = numberString;
            this.numbersPercentageFloat = numbersPercentageFloat;
        }
    }

    public static class GameMode {

        private String nameString;
        private Integer totalNumbersInteger;
        private Integer minSelectionsInteger;
        private Integer maxSelectionsInteger;
        private Boolean include00Boolean;
        private LuckNumber[] numbersLuckNumber;
        private Double cardPriceDouble;
        private Integer superSevenCollumsInteger = 7;
        private String[] luckyDayMonthsString = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

        public GameMode(String nameString, Integer totalNumbersInteger, Integer minSelectionsInteger,
                Integer maxSelectionsInteger, Boolean include00Boolean, Double cardPriceDouble) {
            this.nameString = nameString;
            this.totalNumbersInteger = totalNumbersInteger;
            this.minSelectionsInteger = minSelectionsInteger;
            this.maxSelectionsInteger = maxSelectionsInteger;
            this.include00Boolean = include00Boolean;
            this.cardPriceDouble = cardPriceDouble;
            this.superSevenCollumsInteger = 7;
            initializeNumbersLuckNumber();
        }

        private void initializeNumbersLuckNumber() {
            this.numbersLuckNumber = new LuckNumber[totalNumbersInteger];
            for (int i = 0; i < totalNumbersInteger; i++) {
                String numberString;
                if (nameString.equals("Super-Sete")) {
                    numberString = String.valueOf(i);
                } else {
                    if (include00Boolean && i == 0) {
                        numberString = String.format("%02d", i);
                    } else {
                        numberString = String.format("%02d", i + 1);
                    }
                }
                this.numbersLuckNumber[i] = new LuckNumber(numberString, 1.0f);
            }
        }
    }

    public static class GeneratedGame {

        private String gameMode;
        private String[] generatedNumber;
        private String[][] generatedNumbers;
        private String luckyDayMonthString;
        private String[] oddNumbersStrings;
        private String[] evenNumbersStrings;
        private String[] primeNumbersStrings;

        public GeneratedGame(String gameMode) {
            this.gameMode = gameMode;
        }
    }

    public static class GeneratedGameFile {

        private String gameMode;
        private GeneratedGame[] generatedGames;
        private float oddNumbersPercentageFloat;
        private float evenNumbersPercentageFloat;
        private float primeNumbersPercentageFloat;
        private NumberPresence[] numberPresences;

        public GeneratedGameFile(GameMode gameMode, GeneratedGame[] generatedGames, int totalGames) {
            this.gameMode = gameMode.nameString;
            this.generatedGames = generatedGames;
        }

        public void calculateStatistics() {
            int totalNumbers = 0;
            int oddCount = 0;
            int evenCount = 0;
            int primeCount = 0;

            // Initialize numberPresences array
            int maxNumber = getMaxNumber();
            numberPresences = new NumberPresence[maxNumber];
            for (int i = 0; i < maxNumber; i++) {
                numberPresences[i] = new NumberPresence();
                numberPresences[i].numberString = String.format("%02d", i + 1);
                numberPresences[i].presenceCount = 0;
                numberPresences[i].percentageFloat = 0f;
            }

            for (GeneratedGame game : generatedGames) {
                String[] numbers = game.gameMode.equals("Super-Sete") ? flattenArray(game.generatedNumbers) : game.generatedNumber;

                for (String number : numbers) {
                    totalNumbers++;
                    int num = Integer.parseInt(number);

                    if (num % 2 != 0) {
                        oddCount++;
                    } else {
                        evenCount++;
                    }

                    if (App.isPrime(num)) {
                        primeCount++;
                    }

                    numberPresences[num - 1].presenceCount++;
                }
            }

            // Calculate percentages
            this.oddNumbersPercentageFloat = (float) oddCount / totalNumbers * 100;
            this.evenNumbersPercentageFloat = (float) evenCount / totalNumbers * 100;
            this.primeNumbersPercentageFloat = (float) primeCount / totalNumbers * 100;

            // Calculate number presence percentages
            for (NumberPresence np : numberPresences) {
                np.percentageFloat = (float) np.presenceCount / generatedGames.length * 100;
            }
        }

        private int getMaxNumber() {
            return switch (gameMode) {
                case "Mega-Sena" ->
                    60;
                case "Lotofácil" ->
                    25;
                case "Quina", "Timemania" ->
                    80;
                case "Lotomania" ->
                    100;
                case "Dupla-Sena" ->
                    50;
                case "Dia de Sorte" ->
                    31;
                case "Super-Sete" ->
                    10;
                default ->
                    0;
            };
        }

        private String[] flattenArray(String[][] array) {
            return Arrays.stream(array)
                    .flatMap(Arrays::stream)
                    .filter(Objects::nonNull)
                    .toArray(String[]::new);
        }
    }

    public static class NumberPresence {

        private String numberString;
        private int presenceCount;
        private float percentageFloat;
    }

}
