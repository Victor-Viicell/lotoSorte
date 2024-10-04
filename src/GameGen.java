
import java.util.Random;

public class GameGen extends GameMode {

    public static class GenerateGames {

        public static String[] NumberClassSelection(Object gameModes, int numberOfSelections) {
            String[] result = new String[1];
            if (gameModes instanceof GameModes.MaisMilhionaria maisMilhionaria) {
                GameModes.MaisMilhionaria gameMode = maisMilhionaria;
                result = new String[numberOfSelections];
                for (int i = 0; i < numberOfSelections; i++) {
                    String[] selectedNumbers = new String[gameMode.config.playableNumbers];
                    Random random = new Random();
                    float randomNumber = random.nextFloat() * 100;
                    int randomSelection = random.nextInt(gameMode.config.playableNumbers);
                    if (gameMode.numbers[randomSelection].percentage > randomNumber) {
                        selectedNumbers[i] = gameMode.numbers[randomSelection].number;
                    }
                    result = selectedNumbers;
                }
                return result;
            } else if (gameModes instanceof GameModes.DiaDeSorte gameMode) {
                result = new String[numberOfSelections];
                for (int i = 0; i < numberOfSelections; i++) {
                    String[] selectedNumbers = new String[gameMode.config.playableNumbers];
                    Random random = new Random();
                    float randomNumber = random.nextInt(101);
                    int randomSelection = random.nextInt(gameMode.config.playableNumbers);
                    if (randomNumber <= gameMode.numbers[randomSelection].percentage) {
                        selectedNumbers[i] = gameMode.numbers[randomSelection].number;
                    }
                    result = selectedNumbers;
                }
                return result;
            } else if (gameModes instanceof GameModes.SuperSete gameMode) {
                result = new String[numberOfSelections];
                for (int i = 0; i < numberOfSelections; i++) {
                    String[] selectedNumbers = new String[gameMode.config.playableNumbers];
                    Random random = new Random();
                    float randomNumber = random.nextInt(101);
                    int randomSelection = random.nextInt(gameMode.config.playableNumbers);
                    if (randomNumber <= gameMode.numbers[randomSelection].percentage) {
                        selectedNumbers[i] = gameMode.numbers[randomSelection].number;
                    }
                    result = selectedNumbers;
                }
                return result;
            } else if (gameModes instanceof GameModes.CommonGame gameMode) {
                result = new String[numberOfSelections];
                for (int i = 0; i < numberOfSelections; i++) {
                    String[] selectedNumbers = new String[gameMode.config.playableNumbers];
                    Random random = new Random();
                    float randomNumber = random.nextInt(101);
                    int randomSelection = random.nextInt(gameMode.config.playableNumbers);
                    if (randomNumber <= gameMode.numbers[randomSelection].percentage) {
                        selectedNumbers[i] = gameMode.numbers[randomSelection].number;
                    }
                    result = selectedNumbers;
                }
                return result;
            } else {
                result[0] = "Invalid Game Mode";
            }
            return result;
        }

        private static int validateNumberOfSelections(int numberOfNumbers, int minSelections, int maxSelections) {
            if (numberOfNumbers <= maxSelections && numberOfNumbers >= minSelections) {
                return numberOfNumbers;
            } else if (numberOfNumbers > maxSelections) {
                return maxSelections;
            } else {
                return minSelections;
            }
        }

        public static Datas DataGen(Object genGame) {
            Datas data = new Datas();
            if (genGame instanceof GenMaisMilhionaria) {
                // Lógica para MaisMilhionaria
            } else if (genGame instanceof GenDiaDeSorte) {
                // Lógica para DiaDeSorte
            } else if (genGame instanceof GenSuperSete) {
                // Lógica para SuperSete
            } else if (genGame instanceof GenCommonGame) {
                // Lógica para CommonGame
            } else {
                throw new IllegalArgumentException("Modo de jogo inválido");
            }

            return data;
        }

        public static class Datas {

            public String[] evenNumbers;
            public String[] oddNumbers;
            public String[] primeNumbers;
            public Double evenNumbersPercentage;
            public Double oddNumbersPercentage;
            public Double primeNumbersPercentage;
            public Double totalPrice;
        }

        public static class GenMaisMilhionaria {

            public String name;
            public int numberOfGames;
            public int numberOfNumbers;
            public int numberOfTrevos;
            public String[][] generatedNumbers;
            public String[][] generatedTrevos;

            public GenMaisMilhionaria(GameModes.MaisMilhionaria gameMode, int numberOfGames, int numberOfNumbers, int numberOfTrevos) {
                this.name = gameMode.config.name;
                this.numberOfGames = numberOfGames;
                if (numberOfTrevos <= 6 && numberOfTrevos >= 2) {
                    this.numberOfTrevos = numberOfTrevos;
                } else {
                    if (numberOfTrevos > 6) {
                        this.numberOfTrevos = 6;
                    } else if (numberOfTrevos < 2) {
                        this.numberOfTrevos = 2;
                    }
                }
                this.numberOfNumbers = validateNumberOfSelections(
                        numberOfNumbers,
                        gameMode.config.minSelections,
                        gameMode.config.maxSelections);
                this.generatedNumbers = new String[this.numberOfGames][this.numberOfNumbers];
                this.generatedTrevos = new String[this.numberOfGames][this.numberOfTrevos];
                for (int i = 0; i < this.numberOfGames; i++) {
                    this.generatedNumbers[i] = NumberClassSelection(gameMode, this.numberOfNumbers);
                    for (int j = 0; j < this.numberOfTrevos; j++) {
                        Random random = new Random();
                        int randomSelection = random.nextInt(gameMode.trevos.length);
                        this.generatedTrevos[i][j] = gameMode.trevos[randomSelection];
                    }
                }
            }
        }

        public static class GenDiaDeSorte {

            public String name;
            public int numberOfGames;
            public int numberOfNumbers;
            public String[][] generatedNumbers;
            public String[] generatedMonth;

            public GenDiaDeSorte(GameModes.DiaDeSorte gameMode, int numberOfGames, int numberOfNumbers) {
                this.name = gameMode.config.name;
                this.numberOfGames = numberOfGames;
                this.numberOfNumbers = validateNumberOfSelections(
                        numberOfNumbers,
                        gameMode.config.minSelections,
                        gameMode.config.maxSelections);
                this.generatedNumbers = new String[this.numberOfGames][this.numberOfNumbers];
                this.generatedMonth = new String[this.numberOfGames];
                for (int i = 0; i < this.numberOfGames; i++) {
                    this.generatedNumbers[i] = NumberClassSelection(gameMode, this.numberOfNumbers);
                    Random random = new Random();
                    int randomSelection = random.nextInt(gameMode.months.length);
                    this.generatedMonth[i] = gameMode.months[randomSelection];
                }
            }
        }

        public static class GenSuperSete {

            public String name;
            public int numberOfGames;
            public int numberOfNumbers;
            public int numberOfColumns = 7;
            public String[][] generatedNumbers;

            // TODO:Terminar este e depois Datas
            public GenSuperSete(GameModes.SuperSete gameMode, int numberOfGames, int numberOfNumbers) {
                this.name = gameMode.config.name;
                this.numberOfGames = numberOfGames;
                this.numberOfNumbers = validateNumberOfSelections(
                        numberOfNumbers,
                        gameMode.config.minSelections,
                        gameMode.config.maxSelections);
                this.generatedNumbers = new String[this.numberOfGames][this.numberOfColumns];
                for (int i = 0; i < this.numberOfGames; i++) {
                    this.generatedNumbers[i] = NumberClassSelection(gameMode, this.numberOfNumbers);
                }
            }

        }

        public static class GenCommonGame {

            public String name;
            public int numberOfGames;
            public int numberOfNumbers;
            public String[][] generatedNumbers;

            public GenCommonGame(GameModes.CommonGame gameMode, int numberOfGames, int numberOfNumbers) {
                this.name = gameMode.config.name;
                this.numberOfGames = numberOfGames;
                this.numberOfNumbers = validateNumberOfSelections(
                        numberOfNumbers,
                        gameMode.config.minSelections,
                        gameMode.config.maxSelections);
                this.generatedNumbers = new String[this.numberOfGames][this.numberOfNumbers];
                for (int i = 0; i < this.numberOfGames; i++) {
                    this.generatedNumbers[i] = NumberClassSelection(gameMode, this.numberOfNumbers);
                }
            }
        }
    }
}
