
public class GameGen extends GameMode {

    public static class GenerateGames {

        public static class Datas {

            public String[] evenNumbers;
            public String[] oddNumbers;
            public String[] primeNumbers;
            public Double evenNumbersPercentage;
            public Double oddNumbersPercentage;
            public Double primeNumbersPercentage;
            public Double totalPrice;

            public Datas(Object genGame, Object gameMode) {
                if (genGame instanceof GenMaisMilhionaria && gameMode instanceof GameModes.MaisMilhionaria) {

                } else if (genGame instanceof GenDiaDeSorte && gameMode instanceof GameModes.DiaDeSorte) {

                } else if (genGame instanceof GenSuperSete && gameMode instanceof GameModes.SuperSete) {

                } else if (genGame instanceof GenCommonGame && gameMode instanceof GameModes.CommonGame) {

                } else {
                    throw new Error("Invalid Game Mode or Game");
                }
            }

            public static class GenMaisMilhionaria {

                public String name;
                public int numberOfGames;
                public int numberOfNumbers;
                public int numberOfTrevos;
                public String[] generatedNumbers;
                public String[] generatedTrevos;
                public Datas datas;

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
                    // Make this a function
                    // if (numberOfNumbers <= gameMode.config.maxSelections
                    //         && numberOfNumbers >= gameMode.config.minSelections) {
                    //     this.numberOfNumbers = numberOfNumbers;
                    // } else {
                    //     if (numberOfNumbers > gameMode.config.maxSelections) {
                    //         this.numberOfNumbers = gameMode.config.maxSelections;
                    //     } else if (numberOfNumbers < gameMode.config.minSelections) {
                    //         this.numberOfNumbers = gameMode.config.minSelections;
                    //     }
                    // }
                    // Get a random number from gameMode.numbers numberOfNumbers times
                }
            }

            public static class GenDiaDeSorte {

                public String name;
                public int numberOfGames;
                public int numberOfNumbers;
                public String[] generatedNumbers;
                public String generatedMonth;
                public Datas datas;
            }

            public static class GenSuperSete {

                public String name;
                public int numberOfGames;
                public int numberOfNumbers;
                public int numberOfColumns = 7;
                public String[][] generatedNumbers;
                public Datas datas;

            }

            public static class GenCommonGame {

                public String name;
                public int numberOfGames;
                public int numberOfNumbers;
                public String[] generatedNumbers;
                public Datas datas;
            }

        }
    }
}
