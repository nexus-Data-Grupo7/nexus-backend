package sptech.school;

import sptech.school.aws.S3Service;

// 1. Imports atualizados para as classes de Log específicas
import sptech.school.LogsExtracao.LogInfo;
import sptech.school.LogsExtracao.LogSucesso;
import sptech.school.LogsExtracao.LogErro;

import java.io.InputStream;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Nome do seu bucket S3
        String bucketName = "s3-nexus-teste";

        // Cria os objetos de serviço
        S3Service s3Service = new S3Service();
        DBConnection dbConnection = new DBConnection();

        // 2. LeitorExcel agora PRECISA receber a conexão
        //    para que ele possa registrar seus próprios logs internos.
        LeitorExcel leitorExcel = new LeitorExcel(dbConnection);

        dbConnection.limparBanco();

        // 3. Todas as chamadas de Log foram atualizadas

        SlackNotifier.enviarMensagem("[INFO] INÍCIO DO PROCESSAMENTO");
        new LogInfo("==== INÍCIO DO PROCESSAMENTO ====").registrar(dbConnection);

        // LER O ARQUIVO PRINCIPAL
        String arquivoBase = "base de dados v1.xlsx";

        SlackNotifier.enviarMensagem("[INFO] BUSCANDO ARQUIVO S3");
        new LogInfo("Buscando o arquivo principal no S3: " + arquivoBase).registrar(dbConnection);

        try {
            // Baixa o arquivo como InputStream
            InputStream arquivoBaseStream = s3Service.getFileAsInputStream(bucketName, arquivoBase);

            SlackNotifier.enviarMensagem("[INFO] ARQUIVO ENCONTRADO");
            new LogInfo("Arquivo encontrado! Lendo jogadores...").registrar(dbConnection);
            dbConnection.InserirJogadores(leitorExcel.Extrairjogadores(arquivoBaseStream));

            arquivoBaseStream.close();
        } catch (Exception e) {
            SlackNotifier.enviarMensagem("[ERROR] ERRO AO LER BASE DE DADOS");
            new LogErro("Erro ao ler o arquivo base: " + e.getMessage()).registrar(dbConnection);
        }

        SlackNotifier.enviarMensagem("[INFO] BUSCANDO ARQUIVOS DA PAGINA HISTORICO/ DO S3");
        new LogInfo("\nBuscando arquivos na pasta 'Historico/' do S3...").registrar(dbConnection);

        try {
            // Lista todos os arquivos dentro da pasta "Historico/"
            List<String> arquivosHistorico = s3Service.listObjects(bucketName, "Historico/");

            if (arquivosHistorico == null || arquivosHistorico.isEmpty()) {
                SlackNotifier.enviarMensagem("[INFO] NENHUM ARQUIVO ENCONTRADO PASTA HISTORICO/ DO S3 ");
                new LogInfo("Nenhum arquivo encontrado na pasta 'Historico/'.").registrar(dbConnection);
            } else {
                for (String key : arquivosHistorico) {
                    // Ignora "arquivos" que são apenas a própria pasta
                    if (key.toLowerCase().endsWith(".xlsx")) {

                        SlackNotifier.enviarMensagem("[INFO] PRACESSANDO ARQUIVOS DA PASTA HISTORICO/ DO S3");
                        new LogInfo("Processando arquivo de histórico: " + key).registrar(dbConnection);

                        InputStream historicoStream = s3Service.getFileAsInputStream(bucketName, key);

                        leitorExcel.ExtrairHistorico(historicoStream, key);

                        historicoStream.close();
                    }
                }
            }

        } catch (Exception e) {
            SlackNotifier.enviarMensagem("[ERROR] ERRO AO PROCESSAR ARQUIVOS DA PASTA HISTORICO/ DO S3");
            new LogErro("Erro ao processar arquivos da pasta 'Historico/': " + e.getMessage()).registrar(dbConnection);
        }

        SlackNotifier.enviarMensagem("[SUCESSO] PROCESSAMENTO CONCLUIDO");
        new LogSucesso("\n==== PROCESSAMENTO CONCLUÍDO ====").registrar(dbConnection);
    }
}