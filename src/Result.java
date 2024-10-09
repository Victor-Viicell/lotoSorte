
public class Result {

    public String[] charmpionNumbers;
    public MaisMilhionaria maisMilhionaria;
    public MegaSena megaSena;
    public LotoFacil lotoFacil;
    public Quina quina;
    public LotoMania lotoMania;
    public DuplaSena duplaSena;
    public DiaDeSorte diaDeSorte;
    public SuperSete superSete;

    public class MaisMilhionaria {

        public Integer faixa10; //2 prognósticos certos + acerto de 1 trevo
        public Integer faixa9; //2 prognósticos certos + acerto de 2 trevos
        public Integer faixa8; //3 prognósticos certos + acerto de 1 trevo
        public Integer faixa7; //3 prognósticos certos + acerto de 2 trevos
        public Integer faixa6; //4 prognósticos certos + acerto de 1 ou nenhum trevo
        public Integer faixa5; //4 prognósticos certos + acerto de 2 trevos
        public Integer faixa4; //5 prognósticos certos + acerto de 1 ou nenhum trevo
        public Integer faixa3; //5 prognósticos certos + acerto de 2 trevos
        public Integer faixa2; //6 prognósticos certos + acerto de 1 ou nenhum trevo
        public Integer faixa1; //6 prognósticos certos + acerto de 2 trevos
    }

    public class MegaSena {

        public Integer faixa3; //4 números (Quadra)
        public Integer faixa2; //5 números (Quina)
        public Integer faixa1; //6 números sorteados (Sena)
    }

    public class LotoFacil {

        public Integer faixa5; //11 prognósticos certos entre os 15 sorteados
        public Integer faixa4; //12 prognósticos certos entre os 15 sorteados
        public Integer faixa3; //13 prognósticos certos entre os 15 sorteados
        public Integer faixa2; //14 prognósticos certos entre os 15 sorteados
        public Integer faixa1; //15 prognósticos certos entre os 15 sorteados
    }

    public class Quina {

        public Integer faixa4; //2 prognósticos certos – duque
        public Integer faixa3; //3 prognósticos certos – terno
        public Integer faixa2; //4 prognósticos certos – quadra
        public Integer faixa1; //5 prognósticos certos – quina

    }

    public class LotoMania {

        public Integer faixa7; //nenhum dos 20 números sorteados
        public Integer faixa6; //15 dos 20 números sorteados
        public Integer faixa5; //16 dos 20 números sorteados
        public Integer faixa4; //17 dos 20 números sorteados
        public Integer faixa3; //18 dos 20 números sorteados
        public Integer faixa2; //19 dos 20 números sorteados 
        public Integer faixa1; //20 números sorteados

    }

    public class DuplaSena {

        public Integer faixa4; //acertadores de 3 números
        public Integer faixa3; //acertadores de 4 números
        public Integer faixa2; //acertadores de 5 números
        public Integer faixa1; //acertadores de 6 números
    }

    public class DiaDeSorte {

        public Integer faixa5; //Mês da Sorte sorteado
        public Integer faixa4; //4 números sorteados + Mês da Sorte
        public Integer faixa3; //5 números sorteados + Mês da Sorte
        public Integer faixa2; //6 números sorteados + Mês da Sorte
        public Integer faixa1; //7 números sorteados + Mês da Sorte

    }

    public class SuperSete {

        public Integer faixa5; //3 colunas com o acerto do número sorteado
        public Integer faixa4; //4 colunas com o acerto do número sorteado
        public Integer faixa3; //5 colunas com o acerto do número sorteado
        public Integer faixa2; //6 colunas com o acerto do número sorteado
        public Integer faixa1; //7 colunas com o acerto do número sorteado

    }
}
