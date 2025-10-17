package sptech.school;

import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String caminhoArquivo = "base de dados v1.xlsx";
        String caminhoDaPasta = "Historico";
        DBConnection dbConnection = new DBConnection();

        LeitorExcel leitorExcel = new LeitorExcel();

        dbConnection.InserirJogadores(leitorExcel.Extrairjogadores(caminhoArquivo));

        File pasta = new File(caminhoDaPasta);
        File[] listaDeItens = pasta.listFiles();

        System.out.println("Procurando por arquivos de Excel na pasta...");

        if (listaDeItens != null) {
            System.out.println("Pasta encontrada! Procurando por arquivos de Excel...");

            for (int i = 0; i < listaDeItens.length; i++) {
                File item = listaDeItens[i];

                if (item.isFile() && item.getName().toLowerCase().endsWith(".xlsx")) {
                    System.out.println("--- Encontrado! Processando o arquivo: " + item.getName() + " ---");
                    leitorExcel.ExtrairHistorico(item.getPath());
                }
            }
        } else {

            System.err.println("ERRO: A pasta não foi encontrada no caminho especificado: " + caminhoDaPasta);
            System.err.println("Por favor, verifique se o caminho está correto e a pasta existe.");
        }

        System.out.println("Processamento concluído.");
    }
}
