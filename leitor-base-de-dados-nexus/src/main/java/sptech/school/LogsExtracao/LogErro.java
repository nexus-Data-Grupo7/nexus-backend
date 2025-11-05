package sptech.school.LogsExtracao;

import sptech.school.DBConnection;

public class LogErro extends Log {

    public LogErro(String mensagem) {
        super(mensagem);
    }

    @Override
    public String getStatus() {
        return "ERRO";
    }

    @Override
    public void registrar(DBConnection dbConnection) {
        System.out.println(getMensagemFormatadaConsole());
        dbConnection.inserirLog(getStatus(), this.mensagem);
    }
}