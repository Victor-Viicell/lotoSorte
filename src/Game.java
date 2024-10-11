
/**
 * Representa um jogo da loteria.
 * Esta classe encapsula todas as informações e operações relacionadas a um jogo específico.
 */
public class Game {

    /**
     * O modo de jogo selecionado.
     */
    public GameMode gameMode;

    /**
     * Matriz contendo os números dos jogos gerados.
     */
    public String[][] games;

    /**
     * Objeto contendo dados auxiliares para o jogo.
     */
    public Data data;

    /**
     * Objeto específico para o jogo Mais Milionária, se aplicável.
     */
    public MaisMilionaria maisMilionaria;

    /**
     * Objeto específico para o jogo Dia de Sorte, se aplicável.
     */
    public DiaDeSorte diaDeSorte;

    /**
     * Objeto específico para o jogo Super Sete, se aplicável.
     */
    public SuperSete superSete;

    /**
     * Quantidade de jogos a serem gerados.
     */
    public int amount;

    /**
     * Número de números em cada jogo.
     */
    public int numbers;

    /**
     * Custo total dos jogos gerados, formatado como string.
     */
    public String totalCost;

    /**
     * Construtor da classe Game.
     *
     * @param game O modo de jogo selecionado.
     * @param amount A quantidade de jogos a serem gerados.
     * @param numbers O número de números em cada jogo.
     * @param extraNumbers O número de números extras (usado apenas para o modo
     * "+Milionária").
     */
    public Game(GameMode game, int amount, int numbers, Integer extraNumbers) {
        // Inicializa as propriedades básicas do jogo
        this.gameMode = game;
        this.amount = amount;
        this.numbers = numbers;
        this.games = new String[amount][numbers];
        this.data = new Data();

        // Verifica o modo de jogo e realiza as inicializações específicas
        if (gameMode.name.equals("+Milionária") && extraNumbers != null) {
            // Inicializa o jogo Mais Milionária com números extras
            this.maisMilionaria = new MaisMilionaria();
            this.maisMilionaria.extraNumbers = extraNumbers;
        } else if (gameMode.name.equals("Dia de Sorte")) {
            // Inicializa o jogo Dia de Sorte
            this.diaDeSorte = new DiaDeSorte();
        } else if (gameMode.name.equals("Super-Sete")) {
            // Gera jogos específicos para o modo Super-Sete
            this.games = superSete.genGameSuperSete();
        } else {
            // Gera jogos para os demais modos
            this.games = GenGames();
        }

        // Calcula o custo total dos jogos gerados
        this.totalCost = getTotalCost();
    }

    /**
     * Calcula e formata o custo total dos jogos.
     *
     * @return Uma String representando o custo total formatado.
     */
    public final String getTotalCost() {
        // Calcula o custo base
        float cost = gameMode.costPerGame * amount;

        // Ajusta o custo para seleções extras
        if (numbers > gameMode.minSelections) {
            int extraSelections = numbers - gameMode.minSelections;
            int number = gameMode.minSelections + 1;
            for (int j = 1; j <= extraSelections; j++) {
                cost = ((cost * number) / j);
                number++;
            }
        } else {
            cost = gameMode.costPerGame * amount;
        }

        // Formata o custo com duas casas decimais
        String costString = String.format("%.2f", cost);

        // Substitui o ponto decimal por vírgula
        costString = costString.replace(".", ",");

        // Formata o custo com separadores de milhar
        StringBuilder formattedCost = new StringBuilder();
        int integerPartLength = costString.indexOf(",");
        int count = 0;

        // Insere pontos como separadores de milhar
        for (int i = integerPartLength - 1; i >= 0; i--) {
            formattedCost.insert(0, costString.charAt(i));
            count++;
            if (count == 3 && i > 0) {
                formattedCost.insert(0, ".");
                count = 0;
            }
        }

        // Adiciona a parte decimal ao custo formatado
        formattedCost.append(costString.substring(integerPartLength));
        costString = formattedCost.toString();

        return costString;
    }

    public class SuperSete {

        public String[][][] columnNumbers;

        /**
         * Gera jogos para o modo Super-Sete.
         *
         * @return Uma matriz bidimensional de Strings representando os jogos
         * gerados.
         */
        public final String[][] genGameSuperSete() {

            // Inicializa a matriz de jogos com o tamanho adequado
            String[][] ssGames = new String[amount][numbers];
            String[][][] columns = new String[amount][gameMode.superSete.columns.length][3];
            // Gera 'amount' jogos
            for (int i = 0; i < amount; i++) {
                columns[i] = gameMode.superSete.genSuperSevenBase(numbers);
                // Utiliza o método genSuperSeven da classe superSete para gerar cada jogo
                ssGames[i] = gameMode.superSete.genSuperSeven(numbers, columns[i]);
            }
            columnNumbers = columns;
            // Retorna a matriz com os jogos gerados
            return ssGames;
        }
    }

    /**
     * Gera uma matriz de jogos.
     *
     * Este método cria uma matriz bidimensional de jogos, onde cada jogo é
     * representado por uma série de números gerados de acordo com o modo de
     * jogo atual.
     *
     * @return Uma matriz de String contendo os jogos gerados. Cada linha
     * representa um jogo individual. O número de linhas é determinado pela
     * variável 'amount'. O número de colunas é determinado pela variável
     * 'numbers'.
     */
    public final String[][] GenGames() {
        String[][] gamesArray = new String[amount][numbers];
        for (int i = 0; i < amount; i++) {
            gamesArray[i] = gameMode.genNumbers(numbers);
        }
        return gamesArray;
    }

    /**
     * Classe que representa o jogo Mais Milionária.
     */
    public class MaisMilionaria {

        /**
         * Número de números extras (trevos) a serem gerados.
         */
        public Integer extraNumbers;

        /**
         * Matriz contendo os trevos gerados para cada jogo.
         */
        public String[][] trevos = genGameTrevos();

        /**
         * Gera os trevos para todos os jogos.
         *
         * @return Uma matriz de strings contendo os trevos gerados para cada
         * jogo.
         */
        public final String[][] genGameTrevos() {
            String[][] gameTrevos = new String[amount][extraNumbers];
            for (int i = 0; i < amount; i++) {
                gameTrevos[i] = gameMode.maisMilionaria.genTrevos(extraNumbers);
            }
            return gameTrevos;
        }
    }

    /**
     * Classe que representa o jogo Dia de Sorte.
     */
    public class DiaDeSorte {

        /**
         * Array de Strings que armazena os meses gerados para cada jogo.
         */
        public String[] month = genGameMonths();

        /**
         * Gera os meses para cada jogo do Dia de Sorte.
         *
         * @return Um array de Strings contendo os meses gerados para cada jogo.
         */
        public String[] genGameMonths() {
            String[] months = new String[amount];
            for (int i = 0; i < amount; i++) {
                months[i] = gameMode.diaDeSorte.genMonth();
            }
            return months;
        }
    }

    /**
     * Classe que representa os dados estatísticos do jogo.
     */
    public class Data {

        /**
         * Array de números pares
         */
        public EvenNumbers[] evenNumbers;
        /**
         * Array de números ímpares
         */
        public OddNumbers[] oddNumbers;
        /**
         * Array de números primos
         */
        public PrimeNumbers[] primeNumbers;
        /**
         * Total de números
         */
        public int totalNumbers;
        /**
         * Total de números pares
         */
        public int totalEvenNumbers;
        /**
         * Total de números ímpares
         */
        public int totalOddNumbers;
        /**
         * Total de números primos
         */
        public int totalPrimeNumbers;
        /**
         * Porcentagem de números pares
         */
        public Float evenNumberPercentage;
        /**
         * Porcentagem de números ímpares
         */
        public Float oddNumberPercentage;
        /**
         * Porcentagem de números primos
         */
        public Float primeNumberPercentage;

        /**
         * Construtor da classe Data. Inicializa todos os campos da classe.
         */
        public Data() {
            this.evenNumbers = getEvenNumbers();
            this.oddNumbers = getOddNumbers();
            this.primeNumbers = getPrimeNumbers();
            this.totalNumbers = getTotalNumbers();
            this.totalEvenNumbers = getTotalEvenNumbers();
            this.totalOddNumbers = getTotalOddNumbers();
            this.totalPrimeNumbers = getTotalPrimeNumbers();
            this.evenNumberPercentage = getEvenNumberPercentage();
            this.oddNumberPercentage = getOddNumberPercentage();
            this.primeNumberPercentage = getPrimeNumberPercentage();
        }

        /**
         * Obtém os números pares do jogo.
         *
         * @return Array de números pares
         */
        public final EvenNumbers[] getEvenNumbers() {
            this.evenNumbers = new EvenNumbers[gameMode.numbers.length];
            for (int i = 0; i < gameMode.numbers.length; i++) {
                if (gameMode.numbers[i] != null && gameMode.numbers[i].number != null) {
                    if (Integer.parseInt(gameMode.numbers[i].number) % 2 == 0) {
                        this.evenNumbers[i] = new EvenNumbers();
                        this.evenNumbers[i].evenNumber = gameMode.numbers[i].number;
                    }
                    for (String[] game : games) {
                        for (String gameNumber : game) {
                            if (gameNumber != null && gameNumber.equals(gameMode.numbers[i].number)) {
                                if (this.evenNumbers[i] != null) {
                                    this.evenNumbers[i].evenAmount++;
                                }
                            }
                        }
                    }
                }
            }
            return this.evenNumbers;
        }

        /**
         * Obtém os números ímpares do jogo.
         *
         * @return Array de números ímpares
         */
        public final OddNumbers[] getOddNumbers() {
            this.oddNumbers = new OddNumbers[gameMode.numbers.length];
            for (int i = 0; i < gameMode.numbers.length; i++) {
                if (gameMode.numbers[i] != null && gameMode.numbers[i].number != null) {
                    if (Integer.parseInt(gameMode.numbers[i].number) % 2 != 0) {
                        this.oddNumbers[i] = new OddNumbers();
                        this.oddNumbers[i].oddNumber = gameMode.numbers[i].number;
                    }
                    for (String[] game : games) {
                        for (String gameNumber : game) {
                            if (gameNumber != null && gameNumber.equals(gameMode.numbers[i].number)) {
                                if (this.oddNumbers[i] != null) {
                                    this.oddNumbers[i].oddAmount++;
                                }
                            }
                        }
                    }
                }
            }
            return this.oddNumbers;
        }

        /**
         * Obtém os números primos do jogo.
         *
         * @return Array de números primos
         */
        public final PrimeNumbers[] getPrimeNumbers() {
            this.primeNumbers = new PrimeNumbers[gameMode.numbers.length];
            for (int i = 0; i < gameMode.numbers.length; i++) {
                if (gameMode.numbers[i] != null && gameMode.numbers[i].number != null) {
                    int num = Integer.parseInt(gameMode.numbers[i].number);
                    if (isPrime(num)) {
                        this.primeNumbers[i] = new PrimeNumbers();
                        this.primeNumbers[i].primeNumber = gameMode.numbers[i].number;
                    }
                    for (String[] game : games) {
                        for (String gameNumber : game) {
                            if (gameNumber != null && gameNumber.equals(gameMode.numbers[i].number)) {
                                if (this.primeNumbers[i] != null) {
                                    this.primeNumbers[i].primeAmount++;
                                }
                            }
                        }
                    }
                }
            }
            return this.primeNumbers;
        }

        /**
         * Calcula o total de números no jogo.
         *
         * @return Total de números
         */
        public final int getTotalNumbers() {
            int totalNumber = 0;
            for (EvenNumbers evenNumber : evenNumbers) {
                if (evenNumber != null) {
                    totalNumber += evenNumber.evenAmount;
                }
            }
            for (OddNumbers oddNumber : oddNumbers) {
                if (oddNumber != null) {
                    totalNumber += oddNumber.oddAmount;
                }
            }
            for (PrimeNumbers primeNumber : primeNumbers) {
                if (primeNumber != null) {
                    totalNumber += primeNumber.primeAmount;
                }
            }
            return totalNumber;
        }

        /**
         * Calcula o total de números pares no jogo.
         *
         * @return Total de números pares
         */
        public final int getTotalEvenNumbers() {
            int totalEvenNumber = 0;
            for (EvenNumbers evenNumber : evenNumbers) {
                if (evenNumber != null) {
                    totalEvenNumber += evenNumber.evenAmount;
                }
            }
            return totalEvenNumber;
        }

        /**
         * Calcula o total de números ímpares no jogo.
         *
         * @return Total de números ímpares
         */
        public final int getTotalOddNumbers() {
            int totalOddNumber = 0;
            for (OddNumbers oddNumber : oddNumbers) {
                if (oddNumber != null) {
                    totalOddNumber += oddNumber.oddAmount;
                }
            }
            return totalOddNumber;
        }

        /**
         * Calcula o total de números primos no jogo.
         *
         * @return Total de números primos
         */
        public final int getTotalPrimeNumbers() {
            int totalPrimeNumber = 0;
            for (PrimeNumbers primeNumber : primeNumbers) {
                if (primeNumber != null) {
                    totalPrimeNumber += primeNumber.primeAmount;
                }
            }
            return totalPrimeNumber;
        }

        /**
         * Calcula a porcentagem de números pares no jogo.
         *
         * @return Porcentagem de números pares
         */
        public final Float getEvenNumberPercentage() {
            evenNumberPercentage = (float) totalEvenNumbers / totalNumbers * 100;
            return evenNumberPercentage;
        }

        /**
         * Calcula a porcentagem de números ímpares no jogo.
         *
         * @return Porcentagem de números ímpares
         */
        public final Float getOddNumberPercentage() {
            oddNumberPercentage = (float) totalOddNumbers / totalNumbers * 100;
            return oddNumberPercentage;
        }

        /**
         * Calcula a porcentagem de números primos no jogo.
         *
         * @return Porcentagem de números primos
         */
        public final Float getPrimeNumberPercentage() {
            primeNumberPercentage = (float) totalPrimeNumbers / totalNumbers * 100;
            return primeNumberPercentage;
        }

        /**
         * Verifica se um número é primo.
         *
         * @param num Número a ser verificado
         * @return true se o número for primo, false caso contrário
         */
        private boolean isPrime(int num) {
            if (num <= 1) {
                return false;
            }
            for (int i = 2; i <= Math.sqrt(num); i++) {
                if (num % i == 0) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Classe interna que representa um número par.
         */
        public class EvenNumbers {

            /**
             * Número par
             */
            public String evenNumber;
            /**
             * Quantidade de vezes que o número par aparece
             */
            public int evenAmount;
        }

        /**
         * Classe interna que representa um número ímpar.
         */
        public class OddNumbers {

            /**
             * Número ímpar
             */
            public String oddNumber;
            /**
             * Quantidade de vezes que o número ímpar aparece
             */
            public int oddAmount;
        }

        /**
         * Classe interna que representa um número primo.
         */
        public class PrimeNumbers {

            /**
             * Número primo
             */
            public String primeNumber;
            /**
             * Quantidade de vezes que o número primo aparece
             */
            public int primeAmount;
        }
    }
}
