package sptech.school;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        String nomeArquivo = "base de dados v1.xlsx";

        Path caminho = Path.of(nomeArquivo);
        InputStream arquivo = Files.newInputStream(caminho);

        Leitor leitor = new Leitor();
        List<Player> playersExtraidos = leitor.extrairPlayer(nomeArquivo, arquivo);


        arquivo.close();

        System.out.println("Players Extraídos:");
        for (Player player : playersExtraidos) {
            System.out.println(player);
        }

    }

}
