package lotoSorte;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Representa o resultado de um jogo de loteria.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Result {

    /**
     * O jogo associado a este resultado.
     */
    public Game game;

    /**
     * Os números vencedores do jogo.
     */
    public String[] championNumbers;

    /**
     * Instância de MaisMilionaria, se aplicável.
     */
    public MaisMilionaria maisMilionaria;

    /**
     * Instância de MegaSena, se aplicável.
     */
    public MegaSena megaSena;

    /**
     * Instância de LotoFacil, se aplicável.
     */
    public LotoFacil lotoFacil;

    /**
     * Instância de Quina, se aplicável.
     */
    public Quina quina;

    /**
     * Instância de LotoMania, se aplicável.
     */
    public LotoMania lotoMania;

    /**
     * Instância de DuplaSena, se aplicável.
     */
    public DuplaSena duplaSena;

    /**
     * Instância de DiaDeSorte, se aplicável.
     */
    public DiaDeSorte diaDeSorte;

    /**
     * Instância de SuperSete, se aplicável.
     */
    public SuperSete superSete;

    /**
     * Os trevos vencedores, se aplicável.
     */
    public String[] championTrevos;

    /**
     * O mês da sorte, se aplicável.
     */
    public String luckyMonth;

    /**
     * Simula um resultado de jogo com base no modo de jogo fornecido.
     *
     * @param gameMode O modo de jogo que será utilizado para a simulação. Este
     * parâmetro define as regras e configurações específicas do jogo.
     *
     * @return Retorna uma nova instância de Game (jogo) configurada com os
     * parâmetros do modo de jogo especificado, incluindo a quantidade mínima de
     * seleções e números extras (trevos) quando aplicável.
     *
     * @apiNote Este método é especialmente útil para criar simulações de jogos,
     * principalmente para o modo "+Milionária" que possui regras específicas
     * para números extras (trevos).
     */
    public Game simulateResult(GameMode gameMode) {
        Game simGame;
        simGame = switch (gameMode.name) {
            case "+Milionária" ->
                new Game(gameMode, 1, gameMode.minSelections, gameMode.maisMilionaria.minTrevos);
            case "Dia de Sorte" ->
                new Game(gameMode, 1, gameMode.minSelections, 1);
            case "Lotomania" ->
                new Game(gameMode, 1, 20, 0);  // Fixed to generate 50 numbers for Lotomania
            default ->
                new Game(gameMode, 1, gameMode.minSelections, 0);
        };
        return simGame;
    }

    public Result() {
        this.championNumbers = new String[0];
        this.championTrevos = new String[0];
    }

    /**
     * Construtor para criar um novo resultado de jogo.
     *
     * @param game O jogo associado a este resultado.
     * @param championNumbers Os números vencedores do jogo.
     * @param championTrevos Os trevos vencedores, se aplicável.
     * @param luckyMonth O mês da sorte, se aplicável.
     */
    public Result(Game game, String[] championNumbers, String[] championTrevos, String luckyMonth) {
        this.game = game;
        this.championNumbers = championNumbers != null ? championNumbers : new String[0];
        this.championTrevos = championTrevos != null ? championTrevos : new String[0];
        this.luckyMonth = luckyMonth;

        // Inicializa a instância apropriada com base no modo de jogo
        switch (game.gameMode.name) {
            case "+Milionária":
                this.maisMilionaria = new MaisMilionaria();
                break;
            case "Mega-Sena":
                this.megaSena = new MegaSena();
                break;
            case "Lotofácil":
                this.lotoFacil = new LotoFacil();
                break;
            case "Quina":
                this.quina = new Quina();
                break;
            case "Lotomania":
                this.lotoMania = new LotoMania();
                break;
            case "Dupla Sena":
                this.duplaSena = new DuplaSena();
                break;
            case "Dia de Sorte":
                this.diaDeSorte = new DiaDeSorte();
                break;
            case "Super Sete":
                this.superSete = new SuperSete();
                break;
            default:
                // Não faz nada se o modo de jogo não for reconhecido
                break;
        }
    }

    /**
     * Classe base para representar um jogo genérico. Esta classe serve como uma
     * estrutura básica para diferentes tipos de jogos.
     */
    public static class BaseGame {

        /**
         * Array de strings para armazenar os números do jogo. Cada elemento do
         * array representa um número escolhido ou sorteado.
         */
        public String[] numbers;

        public BaseGame() {
        }
    }

    public void saveToFile(String fileName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File(fileName), this);
    }

    public static Result loadFromFile(String fileName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        return mapper.readValue(new File(fileName), Result.class);
    }

    /**
     * Verifica os jogos e retorna um array de BaseGame com os jogos que atendem
     * ao critério de pontos.
     *
     * @param point O número de pontos necessários para um jogo ser considerado
     * vencedor.
     * @return Um array de BaseGame contendo os jogos vencedores.
     */
    public final BaseGame[] checkGames(int point) {
        BaseGame[] result = new BaseGame[game.amount];
        int currentPoints;

        if (game.gameMode.name.equals("Super Sete")) {
            for (int i = 0; i < game.amount; i++) {
                currentPoints = 0;
                String[] currentGame = game.games[i];
                int currentIndex = 0;

                for (int col = 0; col < 7; col++) {
                    // Skip the separator
                    currentIndex++;

                    // Find the end of current column
                    int columnEnd = currentIndex;
                    while (columnEnd < currentGame.length && !currentGame[columnEnd].equals("|")) {
                        columnEnd++;
                    }

                    // Check numbers in this column against the champion number for this column
                    boolean matchFound = false;
                    for (int j = currentIndex; j < columnEnd; j++) {
                        if (currentGame[j].equals(championNumbers[col])) {
                            matchFound = true;
                            break;
                        }
                    }

                    if (matchFound) {
                        currentPoints++;
                    }

                    // Move to start of next column
                    currentIndex = columnEnd;
                }

                if (currentPoints == point) {
                    result[i] = new BaseGame();
                    result[i].numbers = currentGame;
                } else {
                    result[i] = null;
                }
            }
        } else {
            // Original code for other game modes
            for (int i = 0; i < game.amount; i++) {
                currentPoints = 0;
                for (int j = 0; j < game.numbers; j++) {
                    for (String championNumber : championNumbers) {
                        if (championNumber == null ? game.games[i][j] == null
                                : championNumber.equals(game.games[i][j])) {
                            currentPoints++;
                            break;
                        }
                    }
                }
                if (currentPoints == point) {
                    result[i] = new BaseGame();
                    result[i].numbers = game.games[i];
                } else {
                    result[i] = null;
                }
            }
        }

        result = Arrays.stream(result).filter(Objects::nonNull).toArray(BaseGame[]::new);
        return result;
    }

    /**
     * Representa uma aposta na loteria Mais Milionária. Esta classe é definida
     * como estática, permitindo seu uso sem a necessidade de instanciar a
     * classe externa.
     */
    public static class MMilionaria {

        /**
         * Array contendo os números escolhidos para a aposta. Cada elemento do
         * array é uma String representando um número da aposta.
         */
        public String[] numbers;

        /**
         * Array contendo os trevos escolhidos para a aposta. Os trevos são
         * símbolos adicionais que podem aumentar o prêmio. Cada elemento do
         * array é uma String representando um trevo.
         */
        public String[] trevos;

        /**
         * Construtor padrão da classe MMilionaria. Este construtor não recebe
         * parâmetros e não inicializa os arrays. Os arrays numbers e trevos
         * devem ser inicializados separadamente após a criação do objeto.
         */
        public MMilionaria() {
        }
    }

    /**
     * Verifica os resultados do jogo Mais Milionária.
     *
     * @param point Número de acertos necessários nos números principais.
     * @param extra Número de acertos necessários nos números extras (trevos).
     * @param orNone Se true, considera válido mesmo sem acertar os números
     * extras.
     * @return Um array de MMilionaria contendo os jogos vencedores.
     */
    public final MMilionaria[] checkMMilionaria(int point, int extra, boolean orNone) {
        // Inicializa o array de resultados com o tamanho igual à quantidade de jogos
        MMilionaria[] resultMilionarias = new MMilionaria[game.amount];
        int currentPoints;
        int trevoPoints;

        // Itera sobre todos os jogos
        for (int i = 0; i < game.amount; i++) {
            currentPoints = 0;
            // Verifica os acertos nos números principais
            for (int j = 0; j < game.numbers; j++) {
                for (String championNumber : championNumbers) {
                    if (championNumber == null ? game.games[i][j] == null
                            : championNumber.equals(game.games[i][j])) {
                        currentPoints++;
                        break;
                    }
                }
            }

            trevoPoints = 0;
            // Verifica os acertos nos números extras (trevos)
            for (int j = 0; j < game.maisMilionaria.extraNumbers; j++) {
                for (String championTrevo : championTrevos) {
                    if (championTrevo == null ? game.maisMilionaria.trevos[i][j] == null : championTrevo.equals(game.maisMilionaria.trevos[i][j])) {
                        trevoPoints++;
                        break;
                    }
                }
            }

            // Verifica se o jogo é vencedor com base nos critérios estabelecidos
            if (currentPoints == point && (trevoPoints == extra || orNone)) {
                resultMilionarias[i] = new MMilionaria(); // Cria uma nova instância de MMilionaria
                resultMilionarias[i].numbers = game.games[i];
                resultMilionarias[i].trevos = game.maisMilionaria.trevos[i];
            } else {
                resultMilionarias[i] = null;
            }
        }

        // Remove os elementos nulos do array de resultMilionariasados
        resultMilionarias = Arrays.stream(resultMilionarias).filter(Objects::nonNull).toArray(MMilionaria[]::new);
        return resultMilionarias;
    }

    /**
     * Representa uma jogada da loteria "Dia de Sorte".
     */
    public static class DSorte {

        /**
         * Array contendo os números escolhidos para a jogada.
         */
        public String[] numbers;

        /**
         * O mês escolhido para a jogada.
         */
        public String month;

        public DSorte() {
        }
    }

    /**
     * Verifica os jogos da Dia de Sorte e retorna os que atendem aos critérios
     * especificados.
     *
     * @param point Número de pontos (acertos) necessários para considerar um
     * jogo vencedor.
     * @return Um array de DSorte contendo os jogos vencedores.
     */
    public final DSorte[] checkDSorte(int point) {
        // Cria um array para armazenar os resultados
        DSorte[] result = new DSorte[game.amount];
        int currentPoints;

        // Itera sobre todos os jogos
        for (int i = 0; i < game.amount; i++) {
            currentPoints = 0;
            // Verifica os números do jogo atual
            for (int j = 0; j < game.numbers; j++) {
                for (String championNumber : championNumbers) {
                    // Compara o número do jogo com o número sorteado
                    if (championNumber == null ? game.games[i][j] == null
                            : championNumber.equals(game.games[i][j])) {
                        currentPoints++;
                        break;
                    }
                }
            }

            // Verifica se o jogo atende aos critérios de vitória
            if (currentPoints == point && (game.diaDeSorte.month[i] == null ? luckyMonth == null : game.diaDeSorte.month[i].equals(luckyMonth))) {
                // Cria um novo objeto DSorte para o jogo vencedor
                result[i] = new DSorte();
                result[i].numbers = game.games[i];
                result[i].month = game.diaDeSorte.month[i];
            } else {
                result[i] = null;
            }
        }

        // Remove os elementos nulos do array e retorna o resultado final
        result = Arrays.stream(result).filter(Objects::nonNull).toArray(DSorte[]::new);
        return result;
    }

    public class MaisMilionaria {

        public MMilionaria[] faixa10; //2 prognósticos certos + acerto de 1 trevo
        public MMilionaria[] faixa9; //2 prognósticos certos + acerto de 2 trevos
        public MMilionaria[] faixa8; //3 prognósticos certos + acerto de 1 trevo
        public MMilionaria[] faixa7; //3 prognósticos certos + acerto de 2 trevos
        public MMilionaria[] faixa6; //4 prognósticos certos + acerto de 1 ou nenhum trevo
        public MMilionaria[] faixa5; //4 prognósticos certos + acerto de 2 trevos
        public MMilionaria[] faixa4; //5 prognósticos certos + acerto de 1 ou nenhum trevo
        public MMilionaria[] faixa3; //5 prognósticos certos + acerto de 2 trevos
        public MMilionaria[] faixa2; //6 prognósticos certos + acerto de 1 ou nenhum trevo
        public MMilionaria[] faixa1; //6 prognósticos certos + acerto de 2 trevos

        public MaisMilionaria() {
            this.faixa10 = checkMMilionaria(2, 1, false);
            this.faixa9 = checkMMilionaria(2, 2, false);
            this.faixa8 = checkMMilionaria(3, 1, false);
            this.faixa7 = checkMMilionaria(3, 2, false);
            this.faixa6 = checkMMilionaria(4, 1, true);
            this.faixa5 = checkMMilionaria(4, 2, false);
            this.faixa4 = checkMMilionaria(5, 1, true);
            this.faixa3 = checkMMilionaria(5, 2, false);
            this.faixa2 = checkMMilionaria(6, 1, true);
            this.faixa1 = checkMMilionaria(6, 2, false);
        }
    }

    public class MegaSena {

        public BaseGame[] faixa3; //4 números (Quadra)
        public BaseGame[] faixa2; //5 números (Quina)
        public BaseGame[] faixa1; //6 números sorteados (Sena)

        public MegaSena() {
            this.faixa3 = checkGames(4);
            this.faixa2 = checkGames(5);
            this.faixa1 = checkGames(6);
        }
    }

    public class LotoFacil {

        public BaseGame[] faixa5; //11 prognósticos certos entre os 15 sorteados
        public BaseGame[] faixa4; //12 prognósticos certos entre os 15 sorteados
        public BaseGame[] faixa3; //13 prognósticos certos entre os 15 sorteados
        public BaseGame[] faixa2; //14 prognósticos certos entre os 15 sorteados
        public BaseGame[] faixa1; //15 prognósticos certos entre os 15 sorteados

        public LotoFacil() {
            this.faixa5 = checkGames(11);
            this.faixa4 = checkGames(12);
            this.faixa3 = checkGames(13);
            this.faixa2 = checkGames(14);
            this.faixa1 = checkGames(15);
        }
    }

    public class Quina {

        public BaseGame[] faixa4; //2 prognósticos certos – duque
        public BaseGame[] faixa3; //3 prognósticos certos – terno
        public BaseGame[] faixa2; //4 prognósticos certos – quadra
        public BaseGame[] faixa1; //5 prognósticos certos – quina

        public Quina() {
            this.faixa4 = checkGames(2);
            this.faixa3 = checkGames(3);
            this.faixa2 = checkGames(4);
            this.faixa1 = checkGames(5);
        }

    }

    public class LotoMania {

        public BaseGame[] faixa7; //nenhum dos 20 números sorteados
        public BaseGame[] faixa6; //15 dos 20 números sorteados
        public BaseGame[] faixa5; //16 dos 20 números sorteados
        public BaseGame[] faixa4; //17 dos 20 números sorteados
        public BaseGame[] faixa3; //18 dos 20 números sorteados
        public BaseGame[] faixa2; //19 dos 20 números sorteados 
        public BaseGame[] faixa1; //20 números sorteados

        public LotoMania() {
            this.faixa7 = checkGames(0);
            this.faixa6 = checkGames(15);
            this.faixa5 = checkGames(16);
            this.faixa4 = checkGames(17);
            this.faixa3 = checkGames(18);
            this.faixa2 = checkGames(19);
            this.faixa1 = checkGames(20);
        }

    }

    public class DuplaSena {

        public BaseGame[] faixa4; //acertadores de 3 números
        public BaseGame[] faixa3; //acertadores de 4 números
        public BaseGame[] faixa2; //acertadores de 5 números
        public BaseGame[] faixa1; //acertadores de 6 números

        public DuplaSena() {
            this.faixa4 = checkGames(3);
            this.faixa3 = checkGames(4);
            this.faixa2 = checkGames(5);
            this.faixa1 = checkGames(6);
        }
    }

    public class DiaDeSorte {

        public DSorte[] faixa5; //Mês da Sorte sorteado
        public DSorte[] faixa4; //4 números sorteados + Mês da Sorte
        public DSorte[] faixa3; //5 números sorteados + Mês da Sorte
        public DSorte[] faixa2; //6 números sorteados + Mês da Sorte
        public DSorte[] faixa1; //7 números sorteados + Mês da Sorte

        public DiaDeSorte() {
            this.faixa5 = checkDSorte(0);
            this.faixa4 = checkDSorte(4);
            this.faixa3 = checkDSorte(5);
            this.faixa2 = checkDSorte(6);
            this.faixa1 = checkDSorte(7);
        }
    }

    public class SuperSete {

        public BaseGame[] faixa5; //3 colunas com o acerto do número sorteado
        public BaseGame[] faixa4; //4 colunas com o acerto do número sorteado
        public BaseGame[] faixa3; //5 colunas com o acerto do número sorteado
        public BaseGame[] faixa2; //6 colunas com o acerto do número sorteado
        public BaseGame[] faixa1; //7 colunas com o acerto do número sorteado

        public SuperSete() {
            this.faixa5 = checkGames(3);
            this.faixa4 = checkGames(4);
            this.faixa3 = checkGames(5);
            this.faixa2 = checkGames(6);
            this.faixa1 = checkGames(7);
        }
    }
}
