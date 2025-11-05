package sptech.school.LogsExtracao;

import sptech.school.DBConnection;

public class LogSucesso extends Log {

    public LogSucesso(String mensagem) {
        super(mensagem);
    }

    @Override
    public String getStatus() {
        return "SUCESSO";
    }

    @Override
    public void registrar(DBConnection dbConnection) {
        System.out.println(getMensagemFormatadaConsole());
        dbConnection.inserirLog(getStatus(), this.mensagem);
    }
}