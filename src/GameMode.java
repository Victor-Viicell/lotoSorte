
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameMode {

    public String name;
    public Integer playableNumbers;
    public Integer minSelections;
    public Integer maxSelections;
    public Numbers[] numbers;
    public Boolean displayDecimal;
    public Boolean startAtZero;
    public MaisMilhionaria maisMilhionaria;
    public DiaDeSorte diaDeSorte;
    public SuperSete superSete;

    /**
     * Initializes a new instance of the GameMode class with the specified
     * parameters.
     *
     * @param name The name of the game mode.
     * @param playableNumbers The number of playable numbers.
     * @param minSelections The minimum number of selections required.
     * @param maxSelections The maximum number of selections allowed.
     * @param displayDecimal Indicates whether to display numbers less than 10
     * with a leading zero.
     * @param startAtZero Indicates whether the numbering should start from 0 or
     * 1.
     */
    public GameMode(String name, Integer playableNumbers, Integer minSelections, Integer maxSelections, Boolean displayDecimal, Boolean startAtZero) {
        this.name = name;
        this.playableNumbers = playableNumbers;
        this.minSelections = minSelections;
        this.maxSelections = maxSelections;
        this.displayDecimal = displayDecimal;
        this.startAtZero = startAtZero;
        this.numbers = getNumbers(this.playableNumbers, this.displayDecimal, this.startAtZero);
        this.maisMilhionaria = new MaisMilhionaria();
        this.diaDeSorte = new DiaDeSorte();
        this.superSete = new SuperSete();
    }

    public final String[] genNumbers(Integer selections) {
        String[] selection = new String[selections];
        if (selections <= maxSelections && selections >= minSelections) {
            Random random = new Random();
            java.util.Set<String> selectedNumbers = new java.util.HashSet<>();
            while (selectedNumbers.size() < selections) {
                float randomPercentage = random.nextFloat();
                int randomIndex = random.nextInt(playableNumbers);
                if (numbers[randomIndex].probability > randomPercentage) {
                    if (selectedNumbers.add(numbers[randomIndex].number)) {
                        selection[selectedNumbers.size() - 1] = numbers[randomIndex].number;
                    }
                }
            }
        }
        java.util.Arrays.sort(selection);
        return selection;
    }

    /**
     * Generates an array of Numbers objects with the specified number of
     * playable numbers.
     *
     * @param playableNumbers The number of playable numbers.
     * @param displayDecimal Indicates whether to display numbers less than 10
     * with a leading zero.
     * @param iniciarDoZero Indicates whether the numbering should start from 0
     * or 1.
     * @return An array of Numbers objects representing the playable numbers.
     */
    public final Numbers[] getNumbers(Integer playableNumbers, Boolean displayDecimal, Boolean startAtZero) {
        Numbers[] numerosGerados = new Numbers[playableNumbers + 1];
        int deslocamento = startAtZero ? 0 : 1;
        for (int i = 0; i < playableNumbers; i++) {
            numerosGerados[i] = new Numbers();
            int valor = i + deslocamento;
            if (displayDecimal && valor < 10) {
                numerosGerados[i].number = String.format("%02d", valor);
            } else {
                numerosGerados[i].number = String.format("%d", valor);
            }
            numerosGerados[i].probability = 1.0f;
        }
        return numerosGerados;
    }

    public class Numbers {

        public String number;
        public Float probability;
    }

    public class MaisMilhionaria {

        public Integer minTrevos = 2;
        public Integer maxTrevos = 6;
        public Trevo[] trevos;

        public class Trevo {

            public String trevo;
            public Float probability;
        }

        public MaisMilhionaria() {
            this.trevos = getTrevo();
        }

        public final Trevo[] getTrevo() {
            Trevo[] trevo = new Trevo[maxTrevos];
            for (int i = 0; i < maxTrevos; i++) {
                trevo[i] = new Trevo();  // Create a new Trevo object
                trevo[i].trevo = String.format("%01d", i + 1);
                trevo[i].probability = 1.0f;
            }
            return trevo;
        }

        public final String[] genTrevos(Integer selections) {
            String[] selection = new String[selections];
            if (selections >= minTrevos && selections <= maxTrevos) {
                Random random = new Random();
                java.util.Set<String> selectedTrevos = new java.util.HashSet<>();
                while (selectedTrevos.size() < selections) {
                    float randomPercentage = random.nextFloat();
                    int randomIndex = random.nextInt(maxTrevos);
                    if (trevos[randomIndex].probability > randomPercentage) {
                        if (selectedTrevos.add(trevos[randomIndex].trevo)) {
                            selection[selectedTrevos.size() - 1] = trevos[randomIndex].trevo;
                        }
                    }
                }
            } else {
                System.err.println("Not allowed amount of Trevos");
            }
            java.util.Arrays.sort(selection);
            return selection;
        }
    }

    public class DiaDeSorte {

        public Months[] months;

        public class Months {

            public String month;
            public Float probability;
        }

        public DiaDeSorte() {
            this.months = getMonths();
        }

        public final Months[] getMonths() {
            String[] monthNames = {
                "Janeiro",
                "Fevereiro",
                "Março",
                "Abril",
                "Maio",
                "Junho",
                "Julho",
                "Agosto",
                "Setembro",
                "Outubro",
                "Novembro",
                "Dezembro"
            };
            Months[] monthsArray = new Months[monthNames.length];

            for (int i = 0; i < monthNames.length; i++) {
                monthsArray[i] = new Months();
                monthsArray[i].month = monthNames[i];
                monthsArray[i].probability = 1.0f;
            }
            return monthsArray;
        }

        /**
         * Gera um mês aleatório com base nas probabilidades definidas.
         *
         * Este método utiliza um gerador de números aleatórios para selecionar
         * um mês da lista de meses disponíveis. A seleção é feita considerando
         * a probabilidade associada a cada mês.
         *
         * @return Uma String contendo o nome do mês selecionado aleatoriamente.
         */
        public final String genMonth() {
            String month = null;
            Random random = new Random();
            while (month == null) {
                float randomPercentage = random.nextFloat();
                int randomIndex = random.nextInt(months.length);
                if (months[randomIndex].probability > randomPercentage) {
                    month = months[randomIndex].month;
                }
            }
            return month;
        }
    }

    public class SuperSete {

        public Columns[] columns;

        public class Columns {

            public Numbers[] numbers = getNumbers(10, false, true);
            public Integer columnSelectionLimit = 3;
            public Integer columnSelecteds = 0;
            public Float probability = 1.0f;
        }

        public Columns() {

        }

        public final Columns[] getColumns() {

        }

        public final String[][] genSuperSeven(Integer selections) {
            String[][] selection = new String[columns.length][3];
            if (selections <= maxSelections && selections >= minSelections) {
                Random random = new Random();
                int totalSelected = 0;
                List<Integer> columnOrder = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6));

                // First, ensure at least one selection per column
                Collections.shuffle(columnOrder);
                for (int col : columnOrder) {
                    selection[col][0] = getRandomNumber(random, col);
                    columns[col].columnSelecteds = 1;
                    totalSelected++;
                }

                // Then, ensure at least two selections per column if possible
                if (totalSelected + columns.length <= selections) {
                    Collections.shuffle(columnOrder);
                    for (int col : columnOrder) {
                        selection[col][1] = getRandomNumber(random, col, selection[col][0]);
                        columns[col].columnSelecteds = 2;
                        totalSelected++;
                        if (totalSelected == selections) {
                            break;
                        }
                    }
                }

                // Finally, fill the remaining selections
                while (totalSelected < selections) {
                    Collections.shuffle(columnOrder);
                    for (int col : columnOrder) {
                        if (columns[col].columnSelecteds < columns[col].columnSelectionLimit) {
                            String number = getRandomNumber(random, col,
                                    selection[col][0], selection[col][1]);
                            if (number != null) {
                                selection[col][columns[col].columnSelecteds] = number;
                                columns[col].columnSelecteds++;
                                totalSelected++;
                                if (totalSelected == selections) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return selection;
        }

        public final String getRandomNumber(Random random, Integer colIndex, String... exclude) {
            List<String> excludeList = Arrays.asList(exclude);
            for (int i = 0; i < 100; i++) { // Limit attempts to avoid infinite loop
                float randomPercentage = random.nextFloat();
                int randomIndex = random.nextInt(columns[colIndex].numbers.length);
                if (columns[colIndex].numbers[randomIndex].probability > randomPercentage
                        && !excludeList.contains(columns[colIndex].numbers[randomIndex].number)) {
                    return columns[colIndex].numbers[randomIndex].number;
                }
            }
            return null;
        }
    }

}
