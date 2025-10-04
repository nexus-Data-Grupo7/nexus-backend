package sptech.school;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Leitor {

    public List<Player> extrairPlayer(String nomeArquivo, InputStream arquivo) {
        try {
            System.out.println("\nIniciando leitura do arquivo %s\n".formatted(nomeArquivo));

            Workbook workbook;
            if (nomeArquivo.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(arquivo);
            } else {
                workbook = new HSSFWorkbook(arquivo);
            }

            Sheet sheet = workbook.getSheetAt(0);

            List<Player> playersExtraidos = new ArrayList<>();

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    System.out.println("\nLendo cabeçalho");

                    for (int i = 0; i < 7; i++) {
                        String coluna = row.getCell(i).getStringCellValue();
                        System.out.println("Coluna " + (i + 1) + ": " + coluna);
                    }

                    System.out.println("--------------------");
                    continue;
                }

                System.out.println("Lendo linha " + row.getRowNum());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                Player player = new Player();
                player.setNick_name(row.getCell(0).getStringCellValue());
                player.setFull_name(row.getCell(1).getStringCellValue());
                if (row.getCell(2) == null){
                    player.setBirth_date(null);
                }else {
                    switch (row.getCell(2).getCellType()) {
                        case STRING -> {
                            String valor = row.getCell(2).getStringCellValue().trim();
                            try {
                                player.setBirth_date(LocalDate.parse(valor, formatter));
                            } catch (DateTimeParseException e) {
                                System.out.println("Data inválida encontrada: " + valor);
                                player.setBirth_date(null);
                            }
                        }
                        case NUMERIC -> {
                            player.setBirth_date(converterDate(row.getCell(2).getDateCellValue()));
                        }
                    }
                }
                if (row.getCell(3) == null) {
                    player.setAge(null);
                } else {
                    switch (row.getCell(3).getCellType()) {
                        case STRING -> {
                            String valor = row.getCell(3).getStringCellValue().trim();
                            if (valor.isEmpty()) {
                                player.setAge(null);
                            } else {
                                player.setAge(Integer.parseInt(valor));
                            }
                        }
                        case NUMERIC -> player.setAge((int) row.getCell(3).getNumericCellValue());
                    }
                }
                player.setCountry_full(row.getCell(4).getStringCellValue());
                player.setTeam(row.getCell(5).getStringCellValue());
                player.setRole(row.getCell(6).getStringCellValue());

                playersExtraidos.add(player);
            }

            workbook.close();

            System.out.println("\nLeitura do arquivo finalizada\n");

            return playersExtraidos;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private LocalDate converterDate(Date data) {
        return data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
