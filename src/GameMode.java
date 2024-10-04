
/* GameConfig has
 * - name
 * - Playable Numbers
 * - Starts at 0? bool
 * - minimum Selections
 * - maximum Selections
 */
 /* Special Rules
 * - Super-Sete - has 7 colummns of 10 numbers, 0-9
 * - Dia de Sorte - select 1 from 12 months, and a number from 01-31
 * - +Milionaria - select 2 to 6 trevos, and 6 to 12 numbers from 01-50
 */
public class GameMode {

    public static GameModes.MaisMilhionaria maisMilhionaria = new GameModes().new MaisMilhionaria(
            new Config(
                    "+ Milionária",
                    50,
                    false,
                    6,
                    12,
                    6.00f));

    public static GameModes.CommonGame megaSena = new GameModes().new CommonGame(
            new Config(
                    "Mega-Sena",
                    60,
                    false,
                    6,
                    20,
                    5.00f));

    public static GameModes.CommonGame lotoFacil = new GameModes().new CommonGame(
            new Config(
                    "LotoFácil",
                    25,
                    false,
                    15,
                    20,
                    3.00f));

    public static GameModes.CommonGame quina = new GameModes().new CommonGame(
            new Config(
                    "Quina",
                    80,
                    false,
                    5,
                    15,
                    2.50f));

    public static GameModes.CommonGame lotoMania = new GameModes().new CommonGame(
            new Config(
                    "LotoMania",
                    100,
                    true,
                    50,
                    50,
                    3.00f));

    public static GameModes.CommonGame duplaSena = new GameModes().new CommonGame(
            new Config(
                    "Dupla-Sena",
                    80,
                    false,
                    5,
                    5,
                    2.50f));
    public static GameModes.DiaDeSorte diaDeSorte = new GameModes().new DiaDeSorte(
            new Config(
                    "Dia de Sorte",
                    31,
                    false,
                    7,
                    15,
                    2.50f));
    public static GameModes.SuperSete superSete = new GameModes().new SuperSete(
            new Config(
                    "Super Sete",
                    10,
                    true,
                    7,
                    21,
                    2.50f));

    public static class Config {

        public String name;
        public int playableNumbers;
        public boolean startsAtZero;
        public int minSelections;
        public int maxSelections;
        public float cardPrice;

        public Config(
                String name,
                int playableNumbers,
                boolean startsAtZero,
                int minSelections,
                int maxSelections,
                float cardPrice) {
            this.name = name;
            this.playableNumbers = playableNumbers;
            this.startsAtZero = startsAtZero;
            this.minSelections = minSelections;
            this.maxSelections = maxSelections;
            this.cardPrice = cardPrice;
        }

        public NumberClass[] ConstructNumbers(int playableNumbers) {
            NumberClass[] numbers = new NumberClass[playableNumbers];

            for (int i = 0; i < playableNumbers; i++) {
                if (startsAtZero && i == 0) {
                    numbers[i] = new NumberClass();
                    numbers[i].number = String.valueOf(i + 1);
                } else {
                    numbers[i] = new NumberClass();
                    numbers[i].number = String.valueOf(i);
                }
                numbers[i].percentage = 100f / playableNumbers;
                if (i < 9 || (startsAtZero && i < 10)) {
                    numbers[i].number = "0" + numbers[i].number;
                }
            }
            return numbers;
        }
    }

    public static class NumberClass {

        public String number;
        public Float percentage;
    }

    public static class GameModes {

        public class MaisMilhionaria {

            public Config config;
            public String[] trevos = {"1", "2", "3", "4", "5", "6"};
            public NumberClass[] numbers;

            public MaisMilhionaria(Config config) {
                this.config = config;
                this.numbers = config.ConstructNumbers(config.playableNumbers);
            }
        }

        public class DiaDeSorte {

            public Config config;
            public NumberClass[] numbers;
            public String[] months = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto",
                "Setembro", "Outubro", "Novembro", "Dezembro"};

            public DiaDeSorte(Config config) {
                this.config = config;
                this.numbers = config.ConstructNumbers(config.playableNumbers);
            }
        }

        public class SuperSete {

            public Config config;
            public String[][] columns;
            public NumberClass[] numbers;

            public SuperSete(Config config) {
                this.config = config;
                this.numbers = config.ConstructNumbers(config.playableNumbers);
                this.columns = new String[7][10];
                for (int i = 0; i < 7; i++) {
                    System.arraycopy(numbers, 0, columns[i], 0, 10);
                }
            }
        }

        public class CommonGame {

            public Config config;
            public NumberClass[] numbers;

            public CommonGame(Config config) {
                this.config = config;
                this.numbers = config.ConstructNumbers(config.playableNumbers);
            }
        }

    }
}
