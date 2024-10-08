
public class Game {

    public GameMode gameMode;
    public String[][] games;
    public Data data;
    public MaisMilionaria maisMilhionaria;
    public DiaDeSorte diaDeSorte;
    public SuperSete superSete;
    public int amount;
    public int numbers;

    public Game(GameMode game, int amount, int numbers, Integer extraNumbers) {
        this.gameMode = game;
        this.amount = amount;
        this.numbers = numbers;
        if (gameMode.name.equals("+Milionária") && extraNumbers != null) {
            this.maisMilhionaria = new MaisMilionaria();
            this.maisMilhionaria.extraNumbers = extraNumbers;
        } else if (gameMode.name.equals("Dia de Sorte")) {
            this.diaDeSorte = new DiaDeSorte();
        } else if (gameMode.name.equals("Super-Sete")) {
            this.superSete = new SuperSete();
            this.superSete.extraNumbers = extraNumbers;
        }
        if (!gameMode.name.equals("Super-Sete")) {
            this.games = GenGames();
        }
    }

    public final String[][] GenGames() {
        String[][] gamesArray = new String[amount][numbers];
        for (int i = 0; i < amount; i++) {
            gamesArray[i] = gameMode.genNumbers(numbers);
        }
        return gamesArray;
    }

    public class MaisMilionaria {

        public Integer extraNumbers;
        public String[][] trevos = genGameTrevos();

        public final String[][] genGameTrevos() {
            String[][] gameTrevos = new String[amount][extraNumbers];
            for (int i = 0; i < amount; i++) {
                gameTrevos[i] = gameMode.maisMilionaria.genTrevos(extraNumbers);
            }
            return gameTrevos;
        }
    }

    public class DiaDeSorte {

        public String[] month = genGameMonths();

        public String[] genGameMonths() {
            String[] months = new String[amount];
            for (int i = 0; i < amount; i++) {
                months[i] = gameMode.diaDeSorte.genMonth();
            }
            return months;
        }
    }

    public class SuperSete {

        public Integer extraNumbers;
        public String[][][] games;

        public String[][][] genGameSuperSete() {
            String[][][] ssGames = new String[amount][gameMode.superSete.columns.length][3];
            for (int i = 0; i < amount; i++) {
                ssGames[i] = gameMode.superSete.genSuperSeven(extraNumbers);
            }
            return ssGames;
        }
    }

    public class Data {

        public EvenNumbers[] evenNumbers;
        public OddNumbers[] oddNumbers;
        public PrimeNumbers[] primeNumbers;
        public Float evenNumberPercentage;
        public Float oddNumberPercentage;
        public Float primeNumberPercentage;

        public final EvenNumbers[] getEvenNumbers() {
            return evenNumbers;
        }

        public class EvenNumbers {

            public String evenNumber;
            public int evenAmount;

        }

        public class OddNumbers {

            public String oddNumber;
            public int oddAmount;
        }

        public class PrimeNumbers {

            public String primeNumber;
            public int primeAmount;
        }

    }
}
