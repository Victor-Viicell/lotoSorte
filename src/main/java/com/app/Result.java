package com.app;

import java.util.Arrays;
import java.util.Objects;

/**
 * Representa o resultado de um jogo de loteria.
 */
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
    public MaisMilionaria maisMilhionaria;

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
     * Construtor para criar um novo resultado de jogo.
     *
     * @param game O jogo associado a este resultado.
     * @param championNumbers Os números vencedores do jogo.
     * @param championTrevos Os trevos vencedores, se aplicável.
     * @param luckyMonth O mês da sorte, se aplicável.
     */
    public Result(Game game, String[] championNumbers, String[] championTrevos, String luckyMonth) {
        this.game = game;
        this.championNumbers = championNumbers;
        this.championTrevos = championTrevos;
        this.luckyMonth = luckyMonth;

        // Inicializa a instância apropriada com base no modo de jogo
        switch (game.gameMode.name) {
            case "+Milionária":
                this.maisMilhionaria = new MaisMilionaria();
                break;
            case "Mega-Sena":
                this.megaSena = new MegaSena();
                break;
            case "Loto-Fácil":
                this.lotoFacil = new LotoFacil();
                break;
            case "Quina":
                this.quina = new Quina();
                break;
            case "Loto-Mania":
                this.lotoMania = new LotoMania();
                break;
            case "Dupla-Sena":
                this.duplaSena = new DuplaSena();
                break;
            case "Dia de Sorte":
                this.diaDeSorte = new DiaDeSorte();
                break;
            case "Super-Sete":
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
    public class BaseGame {

        /**
         * Array de strings para armazenar os números do jogo. Cada elemento do
         * array representa um número escolhido ou sorteado.
         */
        public String[] numbers;
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

        // Verifica se o modo de jogo é "Super-Sete"
        if (game.gameMode.name.equals("Super-Sete")) {
            // Loop para verificar cada jogo
            for (int i = 0; i < game.amount; i++) {
                currentPoints = 0;
                // Verifica cada coluna do jogo Super-Sete
                for (int j = 0; j < game.superSete.columnNumbers[i].length; j++) {
                    // Compara os números da coluna com o número campeão correspondente
                    for (String item : game.superSete.columnNumbers[i][j]) {
                        if (item == null ? championNumbers[j] == null : item.equals(championNumbers[j])) {
                            currentPoints++;
                            break;
                        }
                    }
                }
                // Se o jogo atingir o número de pontos necessários, adiciona ao resultado
                if (currentPoints == point) {
                    result[i] = new BaseGame();
                    result[i].numbers = game.gameMode.superSete.genSuperSeven(game.numbers, game.superSete.columnNumbers[i]);
                } else {
                    result[i] = null;
                }
            }
        } else {
            // Loop para verificar cada jogo nos outros modos de jogo
            for (int i = 0; i < game.amount; i++) {
                currentPoints = 0;
                // Verifica cada número do jogo
                for (int j = 0; j < game.numbers; j++) {
                    // Compara o número do jogo com os números campeões
                    for (String championNumber : championNumbers) {
                        if (championNumber == null ? game.games[i][j] == null
                                : championNumber.equals(game.games[i][j])) {
                            currentPoints++;
                            break;
                        }
                    }
                }
                // Se o jogo atingir o número de pontos necessários, adiciona ao resultado
                if (currentPoints == point) {
                    result[i] = new BaseGame();
                    result[i].numbers = game.games[i];
                } else {
                    result[i] = null;
                }
            }
        }

        // Remove os elementos nulos do array de resultado
        result = Arrays.stream(result).filter(Objects::nonNull).toArray(BaseGame[]::new);
        return result;
    }

    /**
     * Representa uma aposta na loteria Mais Milionária.
     */
    public class MMilionaria {

        /**
         * Array contendo os números escolhidos para a aposta.
         */
        public String[] numbers;

        /**
         * Array contendo os trevos escolhidos para a aposta. Os trevos são
         * símbolos adicionais que podem aumentar o prêmio.
         */
        public String[] trevos;

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
        MMilionaria[] result = new MMilionaria[game.amount];
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
                result[i] = new MMilionaria(); // Cria uma nova instância de MMilionaria
                result[i].numbers = game.games[i];
                result[i].trevos = game.maisMilionaria.trevos[i];
            } else {
                result[i] = null;
            }
        }

        // Remove os elementos nulos do array de resultados
        result = Arrays.stream(result).filter(Objects::nonNull).toArray(MMilionaria[]::new);
        return result;
    }

    /**
     * Representa uma jogada da loteria "Dia de Sorte".
     */
    public class DSorte {

        /**
         * Array contendo os números escolhidos para a jogada.
         */
        public String[] numbers;

        /**
         * O mês escolhido para a jogada.
         */
        public String month;
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