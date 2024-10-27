package com.app;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import com.app.GameMode.MaisMilionaria.Trevo;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Representa um jogo da loteria. Esta classe encapsula todas as informações e
 * operações relacionadas a um jogo específico.
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

    public int extraNumbersInput;

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
    /**
     * Construtor da classe Game. Inicializa um novo jogo com base nos
     * parâmetros fornecidos.
     *
     * @param game O modo de jogo selecionado.
     * @param amount A quantidade de jogos a serem gerados.
     * @param numbers O número de números em cada jogo.
     * @param extraNumbers O número de números extras (usado apenas para o modo
     * "+Milionária").
     * @throws IllegalArgumentException Se o modo de jogo for nulo.
     */
    public Game() {
    }

    public Game(GameMode game, int amount, int numbers, Integer extraNumbersInput) {
        if (game == null) {
            throw new IllegalArgumentException("GameMode cannot be null");
        }
        // Inicializa as propriedades básicas do jogo
        this.gameMode = game;
        this.amount = amount;
        this.numbers = numbers;
        this.extraNumbersInput = extraNumbersInput;
        this.games = new String[amount][numbers];

        // Verifica o modo de jogo e realiza as inicializações específicas
        switch (gameMode.name) {
            case "+Milionária":
                // Inicializa o jogo Mais Milionária
                this.maisMilionaria = new MaisMilionaria();
                this.games = genGames();
                break;
            case "Dia de Sorte":
                // Inicializa o jogo Dia de Sorte
                this.diaDeSorte = new DiaDeSorte();
                this.games = genGames();
                break;
            case "Super Sete":
                // Gera jogos específicos para o modo Super-Sete
                this.superSete = new SuperSete();
                this.games = this.superSete.genGameSuperSete();
                break;
            default:
                // Gera jogos para os demais modos
                this.games = genGames();
                break;
        }
        // Calcula o custo total dos jogos gerados
        this.totalCost = getTotalCost();
        this.data = new Data(game, this.games);
    }

    public void saveToFile(String fileName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        if (this.gameMode.maisMilionaria != null) {
            System.out.println("Mais Milionária Trevo probabilities during save:");
            for (GameMode.MaisMilionaria.Trevo trevo : this.gameMode.maisMilionaria.trevos) {
                System.out.println("Trevo " + trevo.trevo + ": " + String.format("%.2f%%", trevo.probability * 100));
            }
        }

        if (this.maisMilionaria != null) {
            for (Trevo trevo : this.gameMode.maisMilionaria.trevos) {
                if (trevo.probability == null) {
                    throw new IllegalStateException("Trevo probability cannot be null");
                }
            }
        }
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File(fileName), this);
        System.out.println("File saved successfully!");
    }

    public static Game loadFromFile(String fileName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(fileName), Game.class);
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

        return "R$" + costString;
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

        public SuperSete() {
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
    public final String[][] genGames() {
        String[][] gamesArray = new String[amount][numbers];
        for (int i = 0; i < amount; i++) {
            // Gera uma linha de números para o jogo atual
            String[] gameRow = gameMode.genNumbers(numbers);

            // Filtra valores nulos, ordena e converte de volta para um array
            gameRow = Arrays.stream(gameRow)
                    .filter(Objects::nonNull)
                    .sorted()
                    .toArray(String[]::new);

            // Atribui a linha gerada à matriz de jogos
            gamesArray[i] = gameRow;
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
        public Integer extraNumbers = 2;

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
        public MaisMilionaria() {
            this.extraNumbers = extraNumbersInput;
            this.trevos = genGameTrevos();
        }

        public final String[][] genGameTrevos() {
            if (extraNumbers == null) {
                throw new IllegalStateException("extraNumbers is not initialized");
            }
            String[][] gameTrevos = new String[amount][extraNumbers];
            for (int i = 0; i < amount; i++) {
                String[] trevosRow = gameMode.maisMilionaria.genTrevos(extraNumbers);
                gameTrevos[i] = Arrays.stream(trevosRow)
                        .filter(Objects::nonNull)
                        .toArray(String[]::new);
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

        public DiaDeSorte() {
        }
    }

    /**
     * Classe que representa os dados estatísticos do jogo.
     */
    public static class Data {

        @JsonIgnore
        public String[][] games;

        // Add getter
        public String[][] getGames() {
            return games;
        }

        // Add setter
        public void setGames(String[][] games) {
            this.games = games;
        }
        /**
         * Array de números pares
         */
        public EvenNumbers[] evenNumbers;
        /**
         * Array de números ímpares
         */
        public OddNumbers[] oddNumbers;
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
         * Porcentagem de números pares
         */
        public Float evenNumberPercentage;
        /**
         * Porcentagem de números ímpares
         */
        public Float oddNumberPercentage;

        /**
         * Construtor da classe Data. Inicializa todos os campos da classe.
         */
        public Data(GameMode gameMode, String[][] games) {
            this.games = games;
            this.evenNumbers = getEvenNumbers(gameMode);
            this.oddNumbers = getOddNumbers(gameMode);
            this.totalNumbers = getTotalNumbers();
            this.totalEvenNumbers = getTotalEvenNumbers();
            this.totalOddNumbers = getTotalOddNumbers();
            this.evenNumberPercentage = getEvenNumberPercentage();
            this.oddNumberPercentage = getOddNumberPercentage();
        }

        public Data() {
        }

        /**
         * Obtém os números pares do jogo.
         *
         * @return Array de números pares
         */
        public final EvenNumbers[] getEvenNumbers(GameMode gameMode) {
            if (gameMode == null || gameMode.numbers == null) {
                return new EvenNumbers[0];
            }
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
            return Arrays.stream(this.evenNumbers)
                    .filter(Objects::nonNull)
                    .toArray(EvenNumbers[]::new);
        }

        /**
         * Obtém os números ímpares do jogo.
         *
         * @return Array de números ímpares
         */
        public final OddNumbers[] getOddNumbers(GameMode gameMode) {
            if (gameMode == null || gameMode.numbers == null) {
                return new OddNumbers[0];
            }
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
            return Arrays.stream(this.oddNumbers)
                    .filter(Objects::nonNull)
                    .toArray(OddNumbers[]::new);
        }

        /**
         * Calcula o total de números no jogo.
         *
         * @return Total de números
         */
        public final int getTotalNumbers() {
            int totalNumber = 0;
            if (evenNumbers != null) {
                for (EvenNumbers evenNumber : evenNumbers) {
                    if (evenNumber != null) {
                        totalNumber += evenNumber.evenAmount;
                    }
                }
            }
            if (oddNumbers != null) {
                for (OddNumbers oddNumber : oddNumbers) {
                    if (oddNumber != null) {
                        totalNumber += oddNumber.oddAmount;
                    }
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
         * Verifica se um número é primo.
         *
         * @param num Número a ser verificado
         * @return true se o número for primo, false caso contrário
         */
        public static boolean isPrime(int num) {
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

        public static boolean isComposite(int num) {
            if (num <= 1) {
                return false;
            }
            for (int i = 2; i <= Math.sqrt(num); i++) {
                if (num % i == 0) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Classe interna que representa um número par.
         */
        public static class EvenNumbers {

            /**
             * Número par
             */
            public String evenNumber;
            /**
             * Quantidade de vezes que o número par aparece
             */
            public int evenAmount;

            public EvenNumbers() {
            }
        }

        /**
         * Classe interna que representa um número ímpar.
         */
        public static class OddNumbers {

            /**
             * Número ímpar
             */
            public String oddNumber;
            /**
             * Quantidade de vezes que o número ímpar aparece
             */
            public int oddAmount;

            public OddNumbers() {
            }
        }

        /**
         * Classe interna que representa um número primo.
         */
        public static class PrimeNumbers {

            /**
             * Número primo
             */
            public String primeNumber;
            /**
             * Quantidade de vezes que o número primo aparece
             */
            public int primeAmount;

            public PrimeNumbers() {
            }
        }
    }
}
