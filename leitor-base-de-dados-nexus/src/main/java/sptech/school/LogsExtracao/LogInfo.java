package sptech.school.LogsExtracao;

import sptech.school.DBConnection;

public class LogInfo extends Log {

    public LogInfo(String mensagem) {
        super(mensagem);
    }

    @Override
    public String getStatus() {
        return "INFO";
    }

    @Override
    public void registrar(DBConnection dbConnection) {
        System.out.println(getMensagemFormatadaConsole());
        dbConnection.inserirLog(getStatus(), this.mensagem);
    }
}