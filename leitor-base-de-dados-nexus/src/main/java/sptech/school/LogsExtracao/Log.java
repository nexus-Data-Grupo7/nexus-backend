package sptech.school.LogsExtracao;

import sptech.school.DBConnection; // (Do nosso exemplo anterior)
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Log {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    protected LocalDateTime data;
    protected String mensagem;

    public Log(String mensagem) {
        this.mensagem = mensagem;
        this.data = LocalDateTime.now();
    }

    public abstract String getStatus();

    public abstract void registrar(DBConnection dbConnection);


    // Método comum que formata a saída para o console
    protected String getMensagemFormatadaConsole() {
        String dataFormatada = this.data.format(FORMATTER);
        return String.format("[%s] [%s] %s", getStatus(), dataFormatada, this.mensagem);
    }
}