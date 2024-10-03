
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
    public static GameModes.CommonGame superSete = new GameModes().new CommonGame(
            new Config(
                    "Super Sete",
                    10,
                    true,
                    7,
                    21,
                    2.50f));

    public static class Config {

        private String name;
        private int playableNumbers;
        private boolean startsAtZero;
        private int minSelections;
        private int maxSelections;
        private float cardPrice;

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

        public String[] ConstructNumbers(int playableNumbers, Boolean extraDecimal) {
            String[] numbers = new String[playableNumbers];
            for (int i = 0; i < playableNumbers; i++) {
                // 00-50 and Starts at 0 bool
                String temp = "";
                if (startsAtZero) {
                    temp = String.valueOf(i);
                } else {
                    temp = String.valueOf(i + 1);
                }
                if (i > 10 && extraDecimal) {
                    numbers[i] = "0" + temp;
                } else {
                    numbers[i] = temp;
                }
            }
            return numbers;
        }
    }

    public static class GameModes {

        private class MaisMilhionaria {

            private Config config;
            private String[] trevos = {"1", "2", "3", "4", "5", "6"};
            private String[] numbers;

            public MaisMilhionaria(Config config) {
                this.config = config;
                this.numbers = config.ConstructNumbers(config.playableNumbers, true);
            }
        }

        private class DiaDeSorte {

            private Config config;
            private String[] numbers;
            private String[] months = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto",
                "Setembro", "Outubro", "Novembro", "Dezembro"};

            public DiaDeSorte(Config config) {
                this.config = config;
                this.numbers = config.ConstructNumbers(config.playableNumbers, true);
            }
        }

        private class CommonGame {

            private Config config;
            private String[] numbers;

            public CommonGame(Config config) {
                this.config = config;
                this.numbers = config.ConstructNumbers(config.playableNumbers, !config.name.equals("Super Sete"));
            }
        }

    }
}
