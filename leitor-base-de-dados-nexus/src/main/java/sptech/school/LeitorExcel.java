package sptech.school;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LeitorExcel {

    private void log(String mensagem) {
        String dataHora = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        System.out.println("[" + dataHora + "] " + mensagem);
    }

    private void logErro(String mensagem) {
        String dataHora = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        System.err.println("[ERRO " + dataHora + "] " + mensagem);
    }

    public List<Jogador> Extrairjogadores(String caminhoArquivo) {
        List<Jogador> jogadores = new ArrayList<>();

        try (
                InputStream arquivo = new FileInputStream(caminhoArquivo);
                Workbook workbook = new XSSFWorkbook(arquivo)
        ) {
            Sheet sheet = workbook.getSheetAt(0);

            log("Iniciando leitura de jogadores do arquivo: " + caminhoArquivo);

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                if (i == 0) {
                    log("Pulando linha de cabeçalho...");
                    continue;
                }
                log("Lendo linha " + i + "...");

                String nickName = null;
                String fullName = null;
                LocalDate birthDate = null;
                int age = 0;
                String country = null;
                String team = null;
                String role = null;
                String liquipediaLink = null;
                String twitter = null;
                String twitch = null;
                String instagram = null;
                Double totalWinnings = null;
                Integer regiao = null;
                Integer eloDivisao = null;
                Integer elo = null;

                if (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.STRING) {
                    nickName = row.getCell(0).getStringCellValue();
                }

                if (row.getCell(1) != null && row.getCell(1).getCellType() == CellType.STRING) {
                    fullName = row.getCell(1).getStringCellValue();
                }

                if (row.getCell(2) != null) {
                    if (row.getCell(2).getCellType() == CellType.NUMERIC) {
                        birthDate = row.getCell(2).getLocalDateTimeCellValue().toLocalDate();
                    } else if (row.getCell(2).getCellType() == CellType.STRING) {
                        String dataStr = row.getCell(2).getStringCellValue().trim();
                        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        try {
                            birthDate = LocalDate.parse(dataStr, fmt);
                        } catch (java.time.format.DateTimeParseException e) {
                            log("Erro ao analisar a data: " + dataStr);
                        }
                    }
                }

                if (birthDate != null) {
                    age = (int) ChronoUnit.YEARS.between(birthDate, LocalDate.now());
                }

                if (row.getCell(4) != null && row.getCell(4).getCellType() == CellType.STRING) {
                    country = row.getCell(4).getStringCellValue();
                }

                if (row.getCell(5) != null && row.getCell(5).getCellType() == CellType.STRING) {
                    team = row.getCell(5).getStringCellValue();
                }

                if (row.getCell(6) != null && row.getCell(6).getCellType() == CellType.STRING) {
                    role = row.getCell(6).getStringCellValue();
                }

                if (row.getCell(7) != null && row.getCell(7).getCellType() == CellType.STRING) {
                    liquipediaLink = row.getCell(7).getStringCellValue();
                }

                if (row.getCell(8) != null && row.getCell(8).getCellType() == CellType.STRING) {
                    twitter = row.getCell(8).getStringCellValue();
                }

                if (row.getCell(9) != null && row.getCell(9).getCellType() == CellType.STRING) {
                    twitch = row.getCell(9).getStringCellValue();
                }

                if (row.getCell(10) != null && row.getCell(10).getCellType() == CellType.STRING) {
                    instagram = row.getCell(10).getStringCellValue();
                }

                if (row.getCell(11) != null) {
                    if (row.getCell(11).getCellType() == CellType.STRING) {
                        String valorStr = row.getCell(11).getStringCellValue();
                        valorStr = valorStr.replaceAll("[^\\d.]", "");
                        try {
                            totalWinnings = Double.parseDouble(valorStr);
                        } catch (NumberFormatException e) {
                            log("Erro ao converter valor de ganhos: " + valorStr);
                        }
                    } else if (row.getCell(11).getCellType() == CellType.NUMERIC) {
                        totalWinnings = row.getCell(11).getNumericCellValue();
                    }
                }

                if (row.getCell(12).getCellType() == CellType.STRING) {
                    if (row.getCell(12).getStringCellValue().toLowerCase().equals("brasil")) {
                        regiao = 1;
                    } else if (row.getCell(12).getStringCellValue().toLowerCase().equals("américa do norte")) {
                        regiao = 2;
                    } else if (row.getCell(12).getStringCellValue().toLowerCase().equals("américa latina norte")) {
                        regiao = 3;
                    } else if (row.getCell(12).getStringCellValue().toLowerCase().equals("américa latina sul")) {
                        regiao = 4;
                    } else if (row.getCell(12).getStringCellValue().toLowerCase().equals("coreia")) {
                        regiao = 5;
                    } else if (row.getCell(12).getStringCellValue().toLowerCase().equals("europa nórdica e leste")) {
                        regiao = 6;
                    } else if (row.getCell(12).getStringCellValue().toLowerCase().equals("europa ocidental")) {
                        regiao = 7;
                    }
                }

                if (row.getCell(13).getCellType() == CellType.STRING) {
                    if (row.getCell(13).getStringCellValue().toLowerCase().equals("i")) {
                        eloDivisao = 1;
                    } else if (row.getCell(13).getStringCellValue().toLowerCase().equals("ii")) {
                        eloDivisao = 2;
                    } else if (row.getCell(13).getStringCellValue().toLowerCase().equals("iii")) {
                        eloDivisao = 3;
                    } else if (row.getCell(13).getStringCellValue().toLowerCase().equals("iv")) {
                        eloDivisao = 4;
                    } else if (row.getCell(13).getStringCellValue().toLowerCase().equals("v")) {
                        eloDivisao = 5;
                    }
                } else if (row.getCell(13).getCellType() == CellType.NUMERIC) {
                    if (row.getCell(13).getNumericCellValue() == 1) {
                        eloDivisao = 1;
                    } else if (row.getCell(13).getNumericCellValue() == 2) {
                        eloDivisao = 2;
                    } else if (row.getCell(13).getNumericCellValue() == 3) {
                        eloDivisao = 3;
                    } else if (row.getCell(13).getNumericCellValue() == 4) {
                        eloDivisao = 4;
                    } else if (row.getCell(13).getNumericCellValue() == 5) {
                        eloDivisao = 5;
                    }
                }

                if (row.getCell(14).getCellType() == CellType.STRING) {
                    String valor = row.getCell(14).getStringCellValue().toLowerCase();
                    if (valor.equals("challenger") || valor.equals("desafiante")) {
                        elo = 10;
                    } else if (valor.equals("grandmaster") || valor.equals("grao-mestre") || valor.equals("graomestre")) {
                        elo = 9;
                    } else if (valor.equals("master") || valor.equals("mestre")) {
                        elo = 8;
                    } else if (valor.equals("diamond") || valor.equals("diamante")) {
                        elo = 7;
                    } else if (valor.equals("emerald") || valor.equals("esmeralda")) {
                        elo = 6;
                    } else if (valor.equals("platinum") || valor.equals("platina")) {
                        elo = 5;
                    } else if (valor.equals("gold") || valor.equals("ouro")) {
                        elo = 4;
                    } else if (valor.equals("silver") || valor.equals("prata")) {
                        elo = 3;
                    } else if (valor.equals("bronze")) {
                        elo = 2;
                    } else if (valor.equals("iron") || valor.equals("ferro")) {
                        elo = 1;
                    }
                }

                if (nickName != null) {
                    Jogador novoJogador = new Jogador(nickName, fullName, birthDate, age, country, team, role, liquipediaLink, twitter, twitch, instagram, totalWinnings, regiao, eloDivisao, elo);
                    jogadores.add(novoJogador);
                    log("Jogador adicionado: " + nickName);
                }
            }

            log("Total de jogadores lidos: " + jogadores.size());
            return jogadores;

        } catch (Exception e) {
            log("Erro ao ler jogadores: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Partida> ExtrairHistorico(String caminhoArquivo) {
        List<Partida> historico = new ArrayList<>();

        try (
                InputStream arquivo = new FileInputStream(caminhoArquivo);
                Workbook workbook = new XSSFWorkbook(arquivo);
        ) {
            Sheet sheet = workbook.getSheetAt(0);

            File arquivoObject = new File(caminhoArquivo);
            String nomeDoArquivo = arquivoObject.getName();

            String nomePlayer = nomeDoArquivo.replace("relatorio_", "").replace(".xlsx", "");

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                if (i == 0) {
                    log("Pulando linha de cabeçalho...");
                    continue;
                }

                log("Lendo linha " + i + " do histórico...");

                Integer funcao = null;
                String campeao = null;
                String kda = null;
                Integer cs = null;
                Double csPorMin = null;
                String runas = null;
                String feitico = null;
                Boolean resultado = null;
                String resultadoPartida = null;
                Double duracao = null;
                Integer kill = null;
                Integer death = null;
                Integer assists = null;

                if (row.getCell(0).getCellType() == CellType.STRING) {
                    if (row.getCell(0).getStringCellValue().toLowerCase().equals("top lane")
                            || row.getCell(0).getStringCellValue().toLowerCase().equals("top")
                            || row.getCell(0).getStringCellValue().toLowerCase().equals("rota superior")
                            || row.getCell(0).getStringCellValue().toLowerCase().equals("topo")) {
                        funcao = 1;
                    } else if (row.getCell(0).getStringCellValue().toLowerCase().equals("jungle")
                            || row.getCell(0).getStringCellValue().toLowerCase().equals("selva")
                            || row.getCell(0).getStringCellValue().toLowerCase().equals("caçador")) {
                        funcao = 2;
                    } else if (row.getCell(0).getStringCellValue().toLowerCase().equals("mid lane")
                            || row.getCell(0).getStringCellValue().toLowerCase().equals("mid")
                            || row.getCell(0).getStringCellValue().toLowerCase().equals("rota do meio")
                            || row.getCell(0).getStringCellValue().toLowerCase().equals("meio")) {
                        funcao = 3;
                    } else if (row.getCell(0).getStringCellValue().toLowerCase().equals("bot lane")
                            || row.getCell(0).getStringCellValue().toLowerCase().equals("bot")
                            || row.getCell(0).getStringCellValue().toLowerCase().equals("rota inferior")
                            || row.getCell(0).getStringCellValue().toLowerCase().equals("atirador")) {
                        funcao = 4;
                    } else if (row.getCell(0).getStringCellValue().toLowerCase().equals("support")
                            || row.getCell(0).getStringCellValue().toLowerCase().equals("suporte")
                            || row.getCell(0).getStringCellValue().toLowerCase().equals("sup")) {
                        funcao = 5;
                    }
                }

                if (row.getCell(1).getCellType() == CellType.STRING) {
                    campeao = row.getCell(1).getStringCellValue().toLowerCase();
                }

                if (row.getCell(2).getCellType() == CellType.STRING) {
                    kda = row.getCell(2).getStringCellValue();
                }

                if (row.getCell(8).getCellType() == CellType.NUMERIC) {
                    Double valorDouble = row.getCell(8).getNumericCellValue();
                    cs = valorDouble.intValue();
                }

                if (row.getCell(3).getCellType() == CellType.STRING) {
                    String texto = row.getCell(3).getStringCellValue();
                    if (texto.contains("(") && texto.contains("/")) {
                        String numeroStr = texto.substring(texto.indexOf('(') + 1, texto.indexOf('/')).trim();
                        try {
                            csPorMin = Double.parseDouble(numeroStr.replace(",", "."));
                        } catch (NumberFormatException e) {
                            logErro("Erro ao converter csPorMin: " + numeroStr);
                        }
                    }
                }

                if (row.getCell(4).getCellType() == CellType.STRING) {
                    runas = row.getCell(4).getStringCellValue();
                }
                if (row.getCell(5).getCellType() == CellType.STRING) {
                    feitico = row.getCell(5).getStringCellValue();
                }

                if (row.getCell(6).getCellType() == CellType.STRING) {
                    if (row.getCell(6).getStringCellValue().toLowerCase().equals("vitória")
                            || row.getCell(6).getStringCellValue().toLowerCase().equals("vitoria")
                            || row.getCell(6).getStringCellValue().toLowerCase().equals("win")) {
                        resultado = true;
                    } else if (row.getCell(6).getStringCellValue().toLowerCase().equals("derrota")
                            || row.getCell(6).getStringCellValue().toLowerCase().equals("defeat")) {
                        resultado = false;
                    }
                }

                if (row.getCell(7).getCellType() == CellType.STRING) {
                    String[] partes = row.getCell(7).getStringCellValue().split(":");
                    int horas = 0, minutos = 0, segundos = 0;

                    if (partes.length == 2) {
                        minutos = Integer.parseInt(partes[0]);
                        segundos = Integer.parseInt(partes[1]);
                    } else if (partes.length == 3) {
                        horas = Integer.parseInt(partes[0]);
                        minutos = Integer.parseInt(partes[1]);
                        segundos = Integer.parseInt(partes[2]);
                    }

                    duracao = (double) horas * 3600 + minutos * 60 + segundos;
                }

                if (row.getCell(8).getCellType() == CellType.NUMERIC) {
                    kill = (int) row.getCell(8).getNumericCellValue();
                }

                if (row.getCell(9).getCellType() == CellType.NUMERIC) {
                    death = (int) row.getCell(8).getNumericCellValue();
                }

                if (row.getCell(10).getCellType() == CellType.NUMERIC) {
                    assists = (int) row.getCell(8).getNumericCellValue();
                }

                if (funcao == null) {
                    log("A variável 'funcao' está nula.");
                } else if (campeao == null) {
                    log("A variável 'campeao' está nula.");
                } else if (kda == null) {
                    log("A variável 'kda' está nula.");
                } else if (cs == null) {
                    log("A variável 'cs' está nula.");
                } else if (csPorMin == null) {
                    log("A variável 'csPorMin' está nula.");
                } else if (runas == null) {
                    log("A variável 'runas' está nula.");
                } else if (feitico == null) {
                    log("A variável 'feitico' está nula.");
                } else if (resultado == null) {
                    log("A variável 'resultado' está nula.");
                } else if (duracao == null) {
                    log("A variável 'duracao' está nula.");
                } else if (kill == null) {
                    log("A variável 'kill' está nula.");
                } else if (death == null) {
                    log("A variável 'death' está nula.");
                } else if (assists == null) {
                    log("A variável 'assists' está nula.");
                } else {
                    if (resultado) {
                        resultadoPartida = "VITORIA";
                    } else {
                        resultadoPartida = "DERROTA";
                    }

                    Partida novaPartida = new Partida(nomePlayer, funcao, campeao, kda, cs, csPorMin, runas, feitico,
                            resultadoPartida, duracao, kill, death, assists);
                    historico.add(novaPartida);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < historico.size(); i++) {
            log("Partida carregada: " + historico.get(i));
        }

        DBConnection dbConnection = new DBConnection();
        dbConnection.InserirDesempenhoPartida(historico);
        log("Inserção no banco concluída com " + historico.size() + " partidas.");
        return historico;
    }

}