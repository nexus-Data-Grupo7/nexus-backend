package sptech.school;

public class ResultadoRanking {
    private int idJogador;
    private int posicao;

    public ResultadoRanking(int idJogador, int posicao) {
        this.idJogador = idJogador;
        this.posicao = posicao;
    }

    public int getIdJogador() {
        return idJogador;
    }

    public int getPosicao() {
        return posicao;
    }
}