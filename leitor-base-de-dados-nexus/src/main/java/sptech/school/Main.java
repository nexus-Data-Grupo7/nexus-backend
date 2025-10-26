package sptech.school;

import sptech.school.aws.S3Service;
// 1. Importa a sua nova classe de Log
import sptech.school.LogsExtracao.Log;

import java.io.InputStream;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Nome do seu bucket S3 (substitua pelo seu)
        String bucketName = "s3-nexus-teste";

        // Cria os objetos de serviço
        S3Service s3Service = new S3Service();
        DBConnection dbConnection = new DBConnection();
        LeitorExcel leitorExcel = new LeitorExcel();

        dbConnection.limparBanco();
        // 2. Logs atualizados
        Log.info("==== INÍCIO DO PROCESSAMENTO ====");

        // LER O ARQUIVO PRINCIPAL
        String arquivoBase = "base de dados v1.xlsx";

        Log.info("Buscando o arquivo principal no S3: " + arquivoBase);

        try {
            // Baixa o arquivo como InputStream
            InputStream arquivoBaseStream = s3Service.getFileAsInputStream(bucketName, arquivoBase);

            Log.info("Arquivo encontrado! Lendo jogadores...");
            dbConnection.InserirJogadores(leitorExcel.Extrairjogadores(arquivoBaseStream));

            arquivoBaseStream.close();
        } catch (Exception e) {
            Log.erro("Erro ao ler o arquivo base: " + e.getMessage());
        }

        // LER OS HISTÓRICOS
        Log.info("\nBuscando arquivos na pasta 'Historico/' do S3...");

        try {
            // Lista todos os arquivos dentro da pasta "Historico/"
            List<String> arquivosHistorico = s3Service.listObjects(bucketName, "Historico/");

            if (arquivosHistorico == null || arquivosHistorico.isEmpty()) {
                Log.info("Nenhum arquivo encontrado na pasta 'Historico/'.");
            } else {
                for (String key : arquivosHistorico) {
                    // Ignora "arquivos" que são apenas a própria pasta
                    if (key.toLowerCase().endsWith(".xlsx")) {
                        Log.info("Processando arquivo de histórico: " + key);

                        InputStream historicoStream = s3Service.getFileAsInputStream(bucketName, key);

                        leitorExcel.ExtrairHistorico(historicoStream, key);

                        historicoStream.close();
                    }
                }
            }

        } catch (Exception e) {
            Log.erro("Erro ao processar arquivos da pasta 'Historico/': " + e.getMessage());
        }

        Log.sucesso("\n==== PROCESSAMENTO CONCLUÍDO ====");
    }
}