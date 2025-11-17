package sptech.school;

import java.time.LocalDate;

public class Partida {
    private String nomePlayer;
    private Integer funcao;
    private String campeao;
    private String kda;
    private Integer cs;
    private Double csPorMin;
    private String runas;
    private String feitico;
    private String resultado;
    private Double duracao;
    private Integer kill;
    private Integer death;
    private Integer assists;
    private LocalDate dataPartida;

    public Partida(String nomePlayer, Integer funcao, String campeao, String kda, Integer cs, Double csPorMin, String runas, String feitico, String resultado, Double duracao, Integer kill, Integer death, Integer assists, LocalDate dataPartida) {
        this.nomePlayer = nomePlayer;
        this.funcao = funcao;
        this.campeao = campeao;
        this.kda = kda;
        this.cs = cs;
        this.csPorMin = csPorMin;
        this.runas = runas;
        this.feitico = feitico;
        this.resultado = resultado;
        this.duracao = duracao;
        this.kill = kill;
        this.death = death;
        this.assists = assists;
        this.dataPartida = dataPartida;
    }

    public void setDataPartida(LocalDate dataPartida) {
        this.dataPartida = dataPartida;
    }

    public void setNomePlayer(String nomePlayer) {
        this.nomePlayer = nomePlayer;
    }

    public void setFuncao(Integer funcao) {
        this.funcao = funcao;
    }

    public void setCampeao(String campeao) {
        this.campeao = campeao;
    }

    public void setKda(String kda) {
        this.kda = kda;
    }

    public void setCs(Integer cs) {
        this.cs = cs;
    }

    public void setCsPorMin(Double csPorMin) {
        this.csPorMin = csPorMin;
    }

    public void setRunas(String runas) {
        this.runas = runas;
    }

    public void setFeitico(String feitico) {
        this.feitico = feitico;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public void setDuracao(Double duracao) {
        this.duracao = duracao;
    }

    public void setKill(Integer kill) {
        this.kill = kill;
    }

    public void setDeath(Integer death) {
        this.death = death;
    }

    public void setAssists(Integer assists) {
        this.assists = assists;
    }

    public LocalDate getDataPartida() {
        return dataPartida;
    }

    public String getNomePlayer() {
        return nomePlayer;
    }

    public Integer getFuncao() {
        return funcao;
    }

    public String getCampeao() {
        return campeao;
    }

    public String getKda() {
        return kda;
    }

    public Integer getCs() {
        return cs;
    }

    public Double getCsPorMin() {
        return csPorMin;
    }

    public String getRunas() {
        return runas;
    }

    public String getFeitico() {
        return feitico;
    }

    public String getResultado() {
        return resultado;
    }

    public Double getDuracao() {
        return duracao;
    }

    public Integer getKill() {
        return kill;
    }

    public Integer getDeath() {
        return death;
    }

    public Integer getAssists() {
        return assists;
    }

    @Override
    public String toString() {
        return "Partida{" +
                "nomePlayer='" + nomePlayer + '\'' +
                ", funcao=" + funcao +
                ", campeao='" + campeao + '\'' +
                ", kda='" + kda + '\'' +
                ", cs=" + cs +
                ", csPorMin=" + csPorMin +
                ", runas='" + runas + '\'' +
                ", feitico='" + feitico + '\'' +
                ", resultado='" + resultado + '\'' +
                ", duracao=" + duracao +
                ", kill=" + kill +
                ", death=" + death +
                ", assists=" + assists +
                '}';
    }
}
