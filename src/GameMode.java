
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Representa um modo de jogo com suas características específicas.
 */
public class GameMode {

    /**
     * Nome do modo de jogo
     */
    public String name;

    /**
     * Quantidade de números jogáveis
     */
    public Integer playableNumbers;

    /**
     * Número mínimo de seleções permitidas
     */
    public Integer minSelections;

    /**
     * Número máximo de seleções permitidas
     */
    public Integer maxSelections;

    /**
     * Array de números disponíveis para o jogo
     */
    public Numbers[] numbers;

    /**
     * Indica se os números devem ser exibidos com casas decimais
     */
    public Boolean displayDecimal;

    /**
     * Indica se a contagem dos números deve começar do zero
     */
    public Boolean startAtZero;

    public float costPerGame;

    /**
     * Instância do jogo Mais Milionária
     */
    public MaisMilionaria maisMilionaria;

    /**
     * Instância do jogo Dia de Sorte
     */
    public DiaDeSorte diaDeSorte;

    /**
     * Instância do jogo Super Sete
     */
    public SuperSete superSete;

    /**
     * Construtor da classe GameMode.
     *
     * @param name Nome do modo de jogo
     * @param playableNumbers Quantidade de números jogáveis
     * @param minSelections Número mínimo de seleções permitidas
     * @param maxSelections Número máximo de seleções permitidas
     * @param displayDecimal Indica se os números devem ser exibidos com casas
     * decimais
     * @param startAtZero Indica se a contagem dos números deve começar do zero
     * @param costPerGame Custo por jogo
     */
    public GameMode(String name, Integer playableNumbers, Integer minSelections, Integer maxSelections,
            Boolean displayDecimal, Boolean startAtZero, float costPerGame) {
        this.name = name;
        this.playableNumbers = playableNumbers;
        this.minSelections = minSelections;
        this.maxSelections = maxSelections;
        this.displayDecimal = displayDecimal;
        this.startAtZero = startAtZero;
        this.numbers = getNumbers(this.playableNumbers, this.displayDecimal, this.startAtZero);
        this.maisMilionaria = new MaisMilionaria();
        this.diaDeSorte = new DiaDeSorte();
        this.superSete = new SuperSete();
        this.costPerGame = costPerGame;
    }

    /**
     * Gera um array de números aleatórios com base nas configurações do modo de
     * jogo.
     *
     * @param selections O número de seleções desejadas.
     * @return Um array de Strings contendo os números selecionados.
     */
    public final String[] genNumbers(Integer selections) {
        // Inicializa o array de seleção com o tamanho especificado
        String[] selection = new String[selections];

        // Verifica se o número de seleções está dentro dos limites permitidos
        if (selections <= maxSelections && selections >= minSelections) {
            Random random = new Random();
            java.util.Set<String> selectedNumbers = new java.util.HashSet<>();

            // Continua selecionando números até atingir a quantidade desejada
            while (selectedNumbers.size() < selections) {
                // Gera um número aleatório entre 0 e 1
                float randomPercentage = random.nextFloat();
                // Seleciona um índice aleatório dentro do intervalo de números jogáveis
                int randomIndex = random.nextInt(playableNumbers);

                // Verifica se o número selecionado deve ser incluído com base em sua probabilidade
                if (numbers[randomIndex].probability > randomPercentage) {
                    // Tenta adicionar o número ao conjunto de números selecionados
                    if (selectedNumbers.add(numbers[randomIndex].number)) {
                        // Se o número foi adicionado com sucesso, inclui-o no array de seleção
                        selection[selectedNumbers.size() - 1] = numbers[randomIndex].number;
                    }
                }
            }
        }

        // Ordena o array de seleção em ordem crescente
        java.util.Arrays.sort(selection);

        // Retorna o array de números selecionados
        return selection;
    }

    /**
     * Gera um array de objetos Numbers com base nos parâmetros fornecidos.
     *
     * @param playableNumbers O número total de números jogáveis.
     * @param displayDecimal Indica se os números menores que 10 devem ser
     * exibidos com zero à esquerda.
     * @param startAtZero Indica se a contagem deve começar de 0 ou 1.
     * @return Um array de objetos Numbers contendo os números gerados e suas
     * probabilidades.
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

    /**
     * Classe interna que representa um número e sua probabilidade associada.
     */
    public class Numbers {

        /**
         * O número representado como uma string.
         */
        public String number;

        /**
         * A probabilidade associada ao número.
         */
        public Float probability;
    }

    /**
     * Classe que representa o jogo Mais Milionária.
     */
    public class MaisMilionaria {

        /**
         * Número mínimo de trevos que podem ser selecionados.
         */
        public Integer minTrevos = 2;

        /**
         * Número máximo de trevos que podem ser selecionados.
         */
        public Integer maxTrevos = 6;

        /**
         * Array de objetos Trevo que representa os trevos disponíveis no jogo.
         */
        public Trevo[] trevos;

        /**
         * Classe interna que representa um trevo individual no jogo.
         */
        public class Trevo {

            /**
             * Identificador do trevo.
             */
            public String trevo;

            /**
             * Probabilidade associada ao trevo.
             */
            public Float probability;
        }

        /**
         * Construtor da classe MaisMilhionaria. Inicializa o array de trevos
         * chamando o método getTrevo().
         */
        public MaisMilionaria() {
            this.trevos = getTrevo();
        }

        /**
         * Gera um array de objetos Trevo com o número máximo de trevos
         * especificado.
         *
         * @return Um array de objetos Trevo representando os trevos
         * disponíveis.
         */
        public final Trevo[] getTrevo() {
            // Cria um novo array de Trevo com tamanho igual a maxTrevos
            Trevo[] trevo = new Trevo[maxTrevos];

            // Itera sobre o array, criando e configurando cada objeto Trevo
            for (int i = 0; i < maxTrevos; i++) {
                trevo[i] = new Trevo();  // Cria um novo objeto Trevo
                trevo[i].trevo = String.format("%01d", i + 1);  // Define o número do trevo (começando de 1)
                trevo[i].probability = 1.0f;  // Define a probabilidade inicial como 1.0
            }

            // Retorna o array de trevos gerado
            return trevo;
        }

        /**
         * Gera uma seleção aleatória de trevos.
         *
         * @param selections O número de trevos a serem selecionados.
         * @return Um array de Strings contendo os trevos selecionados.
         */
        public final String[] genTrevos(int selections) {
            // Inicializa o array que irá armazenar os trevos selecionados
            String[] selection = new String[selections];

            // Verifica se o número de seleções está dentro do intervalo permitido
            if (selections >= minTrevos && selections <= maxTrevos) {
                // Cria um objeto Random para gerar números aleatórios
                Random random = new Random();

                // Usa um Set para garantir que não haja trevos duplicados
                java.util.Set<String> selectedTrevos = new java.util.HashSet<>();

                // Continua selecionando trevos até atingir o número desejado
                while (selectedTrevos.size() < selections) {
                    // Gera um número aleatório entre 0 e 1
                    float randomPercentage = random.nextFloat();

                    // Escolhe um índice aleatório dentro do intervalo de trevos disponíveis
                    int randomIndex = random.nextInt(maxTrevos);

                    // Verifica se o trevo deve ser selecionado com base em sua probabilidade
                    if (trevos[randomIndex].probability > randomPercentage) {
                        // Tenta adicionar o trevo ao conjunto de trevos selecionados
                        if (selectedTrevos.add(trevos[randomIndex].trevo)) {
                            // Se o trevo foi adicionado com sucesso, adiciona-o também ao array de seleção
                            selection[selectedTrevos.size() - 1] = trevos[randomIndex].trevo;
                        }
                    }
                }
            } else {
                // Imprime uma mensagem de erro se o número de seleções não for permitido
                System.err.println("Not allowed amount of Trevos");
            }

            // Ordena o array de trevos selecionados
            java.util.Arrays.sort(selection);

            // Retorna o array de trevos selecionados
            return selection;
        }
    }

    /**
     * Classe que representa o jogo Dia de Sorte.
     */
    public class DiaDeSorte {

        /**
         * Array de objetos Months que armazena os meses do jogo.
         */
        public Months[] months;

        /**
         * Classe interna que representa um mês no jogo Dia de Sorte.
         */
        public class Months {

            /**
             * Nome do mês.
             */
            public String month;

            /**
             * Probabilidade associada ao mês.
             */
            public Float probability;
        }

        /**
         * Construtor da classe DiaDeSorte. Inicializa o array de meses chamando
         * o método getMonths().
         */
        public DiaDeSorte() {
            this.months = getMonths();
        }

        /**
         * Obtém um array de objetos Months representando os meses do ano.
         *
         * @return Um array de objetos Months, cada um contendo o nome do mês e
         * sua probabilidade.
         */
        public final Months[] getMonths() {
            // Array com os nomes dos meses em português
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

            // Cria um array de objetos Months com o mesmo tamanho do array de nomes
            Months[] monthsArray = new Months[monthNames.length];

            // Preenche o array de Months com os nomes e probabilidades
            for (int i = 0; i < monthNames.length; i++) {
                monthsArray[i] = new Months();
                monthsArray[i].month = monthNames[i];
                monthsArray[i].probability = 1.0f; // Todos os meses têm a mesma probabilidade inicial
            }

            // Retorna o array preenchido
            return monthsArray;
        }

        /**
         * Gera aleatoriamente um mês com base nas probabilidades definidas.
         *
         * Este método utiliza um algoritmo de seleção aleatória ponderada para
         * escolher um mês. Cada mês tem uma probabilidade associada, e o método
         * continua gerando números aleatórios até que um mês seja selecionado
         * com base em sua probabilidade.
         *
         * @return Uma String contendo o nome do mês selecionado aleatoriamente.
         */
        public final String genMonth() {
            String month = null;
            Random random = new Random();
            while (month == null) {
                // Gera um número aleatório entre 0 e 1
                float randomPercentage = random.nextFloat();
                // Seleciona um índice aleatório do array de meses
                int randomIndex = random.nextInt(months.length);
                // Verifica se a probabilidade do mês é maior que o número aleatório gerado
                if (months[randomIndex].probability > randomPercentage) {
                    // Se for, seleciona este mês
                    month = months[randomIndex].month;
                }
            }
            // Retorna o mês selecionado
            return month;
        }
    }

    /**
     * Representa o jogo Super Sete. Esta classe encapsula a lógica e estrutura
     * do jogo Super Sete.
     */
    public class SuperSete {

        /**
         * Array de colunas que compõem o jogo Super Sete.
         */
        public Columns[] columns;

        /**
         * Construtor da classe SuperSete. Inicializa o array de colunas
         * chamando o método getColumns().
         */
        public SuperSete() {
            this.columns = getColumns();
        }

        /**
         * Classe interna que representa uma coluna do jogo Super Sete. Cada
         * coluna contém números, limites de seleção e probabilidades.
         */
        public class Columns {

            /**
             * Array de números disponíveis para seleção na coluna. É
             * inicializado chamando o método getNumbers() com parâmetros
             * específicos.
             */
            public Numbers[] numbers = getNumbers(10, false, true);

            /**
             * Limite máximo de números que podem ser selecionados nesta coluna.
             */
            public Integer columnSelectionLimit = 3;

            /**
             * Contador de números atualmente selecionados nesta coluna.
             */
            public Integer columnSelecteds = 0;

            /**
             * Probabilidade associada a esta coluna. Inicialmente definida como
             * 1.0 (100%).
             */
            public Float probability = 1.0f;

        }

        /**
         * Obtém um array de colunas para o jogo Super Sete.
         *
         * Este método cria e inicializa um array de 7 colunas, onde cada coluna
         * é representada por um objeto da classe Columns.
         *
         * @return Um array de Columns contendo 7 elementos, cada um
         * representando uma coluna do jogo Super Sete.
         */
        public final Columns[] getColumns() {
            // Cria um array de Columns com 7 elementos
            Columns[] columnsArray = new Columns[7];

            // Inicializa cada elemento do array com um novo objeto Columns
            for (int i = 0; i < 7; i++) {
                columnsArray[i] = new Columns();
            }

            // Retorna o array preenchido
            return columnsArray;
        }

        /**
         * Gera uma seleção de números para o jogo Super Sete.
         *
         * Este método cria uma matriz de seleções de números para o jogo Super
         * Sete, garantindo uma distribuição equilibrada entre as colunas e
         * evitando duplicatas.
         *
         * @param selections O número total de seleções a serem feitas.
         * @return Uma matriz de strings representando as seleções para cada
         * coluna.
         */
        public final String[][] genSuperSevenBase(int selections) {
            // Inicializa a matriz de seleção com o tamanho apropriado
            String[][] selection = new String[columns.length][3];
            Random random = new Random();
            int selected = 0;

            // Cria uma lista de índices de colunas disponíveis
            List<Integer> availableColumns = IntStream.range(0, columns.length).boxed().collect(Collectors.toList());

            // Primeira passagem: garante pelo menos uma seleção por coluna
            while (!availableColumns.isEmpty() && selected < selections) {
                int randomColumnIndex = random.nextInt(availableColumns.size());
                int columnIndex = availableColumns.remove(randomColumnIndex);
                int randomNumberIndex = random.nextInt(10);
                selection[columnIndex][0] = columns[columnIndex].numbers[randomNumberIndex].number;
                columns[columnIndex].columnSelecteds = 1;
                selected++;
            }

            // Segunda passagem: adiciona uma segunda seleção a cada coluna
            availableColumns = IntStream.range(0, columns.length).boxed().collect(Collectors.toList());
            while (!availableColumns.isEmpty() && selected < selections) {
                int randomColumnIndex = random.nextInt(availableColumns.size());
                int columnIndex = availableColumns.remove(randomColumnIndex);
                if (columns[columnIndex].columnSelecteds == 1) {
                    int randomNumberIndex;
                    do {
                        randomNumberIndex = random.nextInt(10);
                    } while (selection[columnIndex][0].equals(columns[columnIndex].numbers[randomNumberIndex].number));
                    selection[columnIndex][1] = columns[columnIndex].numbers[randomNumberIndex].number;
                    columns[columnIndex].columnSelecteds = 2;
                    selected++;
                }
            }

            // Terceira passagem: adiciona aleatoriamente uma terceira seleção às colunas até atingir o número de seleções
            availableColumns = IntStream.range(0, columns.length).boxed().collect(Collectors.toList());
            while (!availableColumns.isEmpty() && selected < selections) {
                int randomColumnIndex = random.nextInt(availableColumns.size());
                int columnIndex = availableColumns.remove(randomColumnIndex);
                if (columns[columnIndex].columnSelecteds == 2) {
                    int randomNumberIndex;
                    do {
                        randomNumberIndex = random.nextInt(10);
                    } while (selection[columnIndex][0].equals(columns[columnIndex].numbers[randomNumberIndex].number)
                            || selection[columnIndex][1].equals(columns[columnIndex].numbers[randomNumberIndex].number));
                    selection[columnIndex][2] = columns[columnIndex].numbers[randomNumberIndex].number;
                    columns[columnIndex].columnSelecteds = 3;
                    selected++;
                }
            }

            // Ordena os números dentro de cada coluna
            for (String[] column : selection) {
                Arrays.sort(column, Comparator.nullsLast(Comparator.naturalOrder()));
            }

            return selection;
        }

        /**
         * Gera uma sequência de números para o jogo Super Seven.
         *
         * @param selections O número de seleções a serem feitas.
         * @return Um array de Strings contendo os números selecionados.
         */
        public final String[] genSuperSeven(int selections) {
            // Gera a base do Super Seven usando o método auxiliar
            String[][] result = genSuperSevenBase(selections);

            // Converte o array bidimensional em um array unidimensional
            String[] flatResult = new String[result.length * 3];
            int index = 0;
            for (String[] column : result) {
                for (String number : column) {
                    // Adiciona apenas números não nulos ao array final
                    if (number != null) {
                        flatResult[index++] = number;
                    }
                }
            }

            // Remove elementos nulos e redimensiona o array para o tamanho exato
            return Arrays.copyOf(flatResult, index);
        }

    }

}
