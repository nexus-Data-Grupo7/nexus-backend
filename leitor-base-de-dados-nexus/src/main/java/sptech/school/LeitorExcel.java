package sptech.school;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sptech.school.LogsExtracao.Log;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class LeitorExcel {

    // -------------------- MAPAS DE CONVERSÃO --------------------
    private static final Map<String, Integer> REGIOES = Map.of(
            "brasil", 1,
            "américa do norte", 2,
            "américa latina norte", 3,
            "américa latina sul", 4,
            "coreia", 5,
            "europa nórdica e leste", 6,
            "europa ocidental", 7
    );

    private static final Map<String, Integer> ELO_DIVISAO_MAP = Map.of(
            "i", 1, "ii", 2, "iii", 3, "iv", 4, "v", 5
    );

    private static final Map<String, Integer> ELO_MAP = Map.ofEntries(
            Map.entry("challenger", 10), Map.entry("desafiante", 10),
            Map.entry("grandmaster", 9), Map.entry("grao-mestre", 9), Map.entry("graomestre", 9),
            Map.entry("master", 8), Map.entry("mestre", 8),
            Map.entry("diamond", 7), Map.entry("diamante", 7),
            Map.entry("emerald", 6), Map.entry("esmeralda", 6),
            Map.entry("platinum", 5), Map.entry("platina", 5),
            Map.entry("gold", 4), Map.entry("ouro", 4),
            Map.entry("silver", 3), Map.entry("prata", 3),
            Map.entry("bronze", 2),
            Map.entry("iron", 1), Map.entry("ferro", 1)
    );

    // -------------------- MÉTODO 1: EXTRAI JOGADORES --------------------
    public List<Jogador> Extrairjogadores(InputStream inputStream) {
        List<Jogador> jogadores = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Log.info("Iniciando leitura de jogadores do arquivo: " + inputStream);

            int i = 0;
            for (Row row : sheet) {
                if (row == null) {
                    i++;
                    continue;
                }

                if (i == 0) {
                    Log.info("Pulando linha de cabeçalho...");
                    i++;
                    continue;
                }

                Log.info("Lendo linha " + i + "...");

                String nickName = getStringCell(row, 0);
                String fullName = getStringCell(row, 1);
                LocalDate birthDate = parseDateCell(row.getCell(2));
                int age = (birthDate != null) ? (int) ChronoUnit.YEARS.between(birthDate, LocalDate.now()) : 0;
                String country = getStringCell(row, 4);
                String team = getStringCell(row, 5);
                String role = getStringCell(row, 6);
                String liquipediaLink = getStringCell(row, 7);
                String twitter = getStringCell(row, 8);
                String twitch = getStringCell(row, 9);
                String instagram = getStringCell(row, 10);
                Double totalWinnings = parseDoubleCell(row.getCell(11));
                Integer regiao = parseRegiaoCell(row.getCell(12));
                Integer eloDivisao = parseEloDivisaoCell(row.getCell(13));
                Integer elo = parseEloCell(row.getCell(14));

                if (nickName != null) {
                    Jogador novoJogador = new Jogador(nickName, fullName, birthDate, age, country,
                            team, role, liquipediaLink, twitter, twitch, instagram,
                            totalWinnings, regiao, eloDivisao, elo);
                    jogadores.add(novoJogador);
                    Log.sucesso("Jogador adicionado: " + nickName);
                }

                i++;
            }

            Log.sucesso("Total de jogadores lidos: " + jogadores.size());
            return jogadores;

        } catch (Exception e) {
            Log.erro("Erro ao ler jogadores: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // -------------------- MÉTODO 2: EXTRAI HISTÓRICO --------------------
    public List<Partida> ExtrairHistorico(InputStream inputStream, String nomeDoArquivoS3) {
        List<Partida> historico = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            String nomeBaseDoArquivo = new File(nomeDoArquivoS3).getName();
            String nomePlayer = nomeBaseDoArquivo.replace("relatorio_", "").replace(".xlsx", "").trim();

            Log.info("Iniciando leitura do histórico do arquivo: " + nomeBaseDoArquivo);
            Log.info("Nome do jogador detectado: " + nomePlayer);

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                if (i == 0) {
                    Log.info("Pulando linha de cabeçalho...");
                    continue;
                }

                Log.info("Lendo linha " + i + " do histórico...");

                Integer funcao = parseFuncaoCell(row.getCell(0));
                String campeao = getStringCell(row, 1);
                String kda = getStringCell(row, 2);
                Double csPorMin = parseCsPorMinCell(row.getCell(3));
                String runas = getStringCell(row, 4);
                String feitico = getStringCell(row, 5);
                Boolean resultadoBool = parseResultadoCell(row.getCell(6));
                Double duracao = parseDuracaoCell(row.getCell(7));
                Integer kill = getIntCell(row.getCell(8));
                Integer death = getIntCell(row.getCell(9));
                Integer assists = getIntCell(row.getCell(10));

                if (funcao == null) Log.erro("A variável 'funcao' está nula.");
                if (campeao == null) Log.erro("A variável 'campeao' está nula.");
                if (kda == null) Log.erro("A variável 'kda' está nula.");
                if (csPorMin == null) Log.erro("A variável 'csPorMin' está nula.");
                if (runas == null) Log.erro("A variável 'runas' está nula.");
                if (feitico == null) Log.erro("A variável 'feitico' está nula.");
                if (resultadoBool == null) Log.erro("A variável 'resultado' está nula.");
                if (duracao == null) Log.erro("A variável 'duracao' está nula.");
                if (kill == null) Log.erro("A variável 'kill' está nula.");
                if (death == null) Log.erro("A variável 'death' está nula.");
                if (assists == null) Log.erro("A variável 'assists' está nula.");

                if (funcao != null && campeao != null && kda != null && csPorMin != null &&
                        runas != null && feitico != null && resultadoBool != null &&
                        duracao != null && kill != null && death != null && assists != null) {

                    String resultado = resultadoBool ? "VITORIA" : "DERROTA";

                    Partida novaPartida = new Partida(
                            nomePlayer, funcao, campeao, kda, null, csPorMin, runas, feitico,
                            resultado, duracao, kill, death, assists
                    );

                    historico.add(novaPartida);
                    Log.sucesso("Partida carregada: ");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DBConnection dbConnection = new DBConnection();
        dbConnection.InserirDesempenhoPartida(historico);
        Log.sucesso("Inserção no banco concluída com " + historico.size() + " partidas.");
        return historico;
    }

    // -------------------- MÉTODOS AUXILIARES --------------------
    private String getStringCell(Row row, int index) {
        Cell cell = row.getCell(index);
        return (cell != null && cell.getCellType() == CellType.STRING) ? cell.getStringCellValue() : null;
    }

    private Integer getIntCell(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) return (int) cell.getNumericCellValue();
        return null;
    }

    private LocalDate parseDateCell(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) return cell.getLocalDateTimeCellValue().toLocalDate();
            else if (cell.getCellType() == CellType.STRING) {
                return LocalDate.parse(cell.getStringCellValue().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
        } catch (Exception e) {
            Log.erro("Erro ao analisar a data: " + (cell != null ? cell.getStringCellValue() : "null"));
        }
        return null;
    }

    private Double parseDoubleCell(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
            if (cell.getCellType() == CellType.STRING) {
                String valorStr = cell.getStringCellValue().replaceAll("[^\\d.]", "");
                return Double.parseDouble(valorStr);
            }
        } catch (NumberFormatException e) {
            Log.erro("Erro ao converter valor: " + cell.getStringCellValue());
        }
        return null;
    }

    private Integer parseRegiaoCell(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.STRING) return null;
        return REGIOES.getOrDefault(cell.getStringCellValue().toLowerCase(), null);
    }

    private Integer parseEloDivisaoCell(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING)
            return ELO_DIVISAO_MAP.getOrDefault(cell.getStringCellValue().toLowerCase(), null);
        if (cell.getCellType() == CellType.NUMERIC) return (int) cell.getNumericCellValue();
        return null;
    }

    private Integer parseEloCell(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.STRING) return null;
        return ELO_MAP.getOrDefault(cell.getStringCellValue().toLowerCase(), null);
    }

    private Integer parseFuncaoCell(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.STRING) return null;
        String valor = cell.getStringCellValue().toLowerCase();
        return switch (valor) {
            case "top lane", "top", "rota superior", "topo" -> 1;
            case "jungle", "selva", "caçador" -> 2;
            case "mid lane", "mid", "rota do meio", "meio" -> 3;
            case "bot lane", "bot", "rota inferior", "atirador" -> 4;
            case "support", "suporte", "sup" -> 5;
            default -> null;
        };
    }

    private Double parseCsPorMinCell(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.STRING) return null;
        String texto = cell.getStringCellValue();
        if (texto.contains("(") && texto.contains("/")) {
            try {
                return Double.parseDouble(texto.substring(texto.indexOf('(') + 1, texto.indexOf('/')).replace(",", "."));
            } catch (NumberFormatException e) {
                Log.erro("Erro ao converter csPorMin: " + texto);
            }
        }
        return null;
    }

    private Boolean parseResultadoCell(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.STRING) return null;
        String valor = cell.getStringCellValue().toLowerCase();
        if (valor.equals("vitória") || valor.equals("vitoria") || valor.equals("win")) return true;
        if (valor.equals("derrota") || valor.equals("defeat")) return false;
        return null;
    }

    private Double parseDuracaoCell(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.STRING) return null;
        String[] partes = cell.getStringCellValue().split(":");
        int horas = 0, minutos = 0, segundos = 0;
        try {
            if (partes.length == 2) {
                minutos = Integer.parseInt(partes[0]);
                segundos = Integer.parseInt(partes[1]);
            } else if (partes.length == 3) {
                horas = Integer.parseInt(partes[0]);
                minutos = Integer.parseInt(partes[1]);
                segundos = Integer.parseInt(partes[2]);
            }
        } catch (NumberFormatException e) {
            Log.erro("Erro ao converter duracao: " + cell.getStringCellValue());
        }
        return (double) horas * 3600 + minutos * 60 + segundos;
    }

}
