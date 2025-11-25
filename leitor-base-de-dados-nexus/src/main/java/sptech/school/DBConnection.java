package sptech.school;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;
import sptech.school.LogsExtracao.LogErro;
import sptech.school.LogsExtracao.LogInfo;
import sptech.school.LogsExtracao.LogSucesso;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;

public class DBConnection {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public DBConnection() {
        String host = System.getenv("DB_HOST");
        String bd = System.getenv("MYSQL_DATABASE");
        String user_bd = System.getenv("MYSQL_USER");
        String senha_bd = System.getenv("MYSQL_PASSWORD");

        if (host == null || bd == null || user_bd == null || senha_bd == null) {
            System.err.println("ERRO CRÍTICO: Variáveis de ambiente do banco de dados (DB_HOST, MYSQL_DATABASE, MYSQL_USER, MYSQL_PASSWORD) não estão definidas!");
            throw new IllegalStateException("Variáveis de ambiente do banco de dados não configuradas.");
        }

        BasicDataSource basicDataSource = new BasicDataSource();
        String url = String.format("jdbc:mysql://%s:3306/%s", host, bd);
        System.out.println("[BOOTSTRAP] Conectando ao banco de dados: " + url);
        basicDataSource.setUrl(url);
        basicDataSource.setUsername(user_bd);
        basicDataSource.setPassword(senha_bd);
        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        this.dataSource = basicDataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    public JdbcTemplate getConnection() {
        return this.jdbcTemplate;
    }

    public void InserirJogadores(List<Jogador> jogadores) {
        JdbcTemplate jdbc = getConnection();

        String sql = """
                    INSERT IGNORE INTO jogador
                    (id_conta, id_organizacao, id_regiao, id_elo, game_name, tagline, nome, divisao, pontos_liga, premiacao, idade)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        for (Jogador j : jogadores) {
            try {
                String taglineSegura = "";

                String divisaoString = null;
                if (j.getEloDivisao() != null) {
                    divisaoString = switch (j.getEloDivisao()) {
                        case 1 -> "I";
                        case 2 -> "II";
                        case 3 -> "III";
                        case 4 -> "IV";
                        default -> null;
                    };
                }

                int linhasAfetadas = jdbc.update(sql,
                        null,
                        null,
                        j.getRegiao(),
                        j.getElo(),
                        j.getNickName(),
                        taglineSegura,
                        j.getFullName(),
                        divisaoString,
                        null,
                        j.getTotalWinnings(),
                        j.getAge()
                );

                if (linhasAfetadas == 0) {
                    new LogInfo("Jogador " + j.getNickName() + " já existe (inserção ignorada).").registrar(this);
                } else {
                    new LogSucesso("Jogador " + j.getNickName() + " inserido com sucesso.").registrar(this);
                }

            } catch (Exception e) {
                new LogErro("Erro ao inserir jogador " + j.getNickName() + ": " + e.getMessage()).registrar(this);
            }
        }
    }

    public String BuscarJogadorId(String nomejogador) {
        JdbcTemplate jdbc = getConnection();
        String sql = "SELECT id_jogador FROM jogador WHERE LOWER(game_name) = LOWER(?)";

        try {
            return jdbc.queryForObject(sql, String.class, nomejogador);
        } catch (EmptyResultDataAccessException e) {
            new LogErro("Nenhum jogador encontrado com o nome: " + nomejogador).registrar(this);
            return null;
        }
    }

    public Integer BuscarIdCampeao(String nomeCampeao) {
        JdbcTemplate jdbc = getConnection();
        String sql = "SELECT id_campeao FROM campeao WHERE LOWER(nome_campeao) = LOWER(?)";

        try {
            return jdbc.queryForObject(sql, Integer.class, nomeCampeao);
        } catch (EmptyResultDataAccessException e) {
            new LogErro("Nenhum campeão encontrado com o nome: " + nomeCampeao).registrar(this);

            return null;
        }
    }

    public void AdicionarPartida(LocalDate dataPartida, Double duracao) {
        JdbcTemplate jdbc = getConnection();
        String sql = "INSERT INTO partida (datahora_inicio, duracao_segundos) VALUES (?, ?)";
        jdbc.update(sql, dataPartida, duracao);
    }

    public Integer BuscarIdPartidaEspecifica(LocalDate data, Double duracao) {
        JdbcTemplate jdbc = getConnection();

        String sql = "SELECT id_partida FROM partida WHERE datahora_inicio = ? AND duracao_segundos = ? ORDER BY id_partida DESC LIMIT 1";

        try {
            return jdbc.queryForObject(sql, Integer.class, data, duracao);
        } catch (EmptyResultDataAccessException e) {
            new LogErro("Não foi possível encontrar o ID da partida recém inserida.").registrar(this);
            return null;
        }
    }

    public void InserirDesempenhoPartida(List<Partida> partidas) {
        if (partidas == null || partidas.isEmpty()) {
            new LogInfo("Nenhuma partida para inserir em InserirDesempenhoPartida.").registrar(this);
            return;
        }

        JdbcTemplate jdbc = getConnection();

        String idJogador = BuscarJogadorId(partidas.get(0).getNomePlayer());
        if (idJogador == null) {
            new LogErro("Não foi possível inserir desempenho. Jogador " + partidas.get(0).getNomePlayer() + " não encontrado.").registrar(this);
            return;
        }

        String sql = """
                    INSERT INTO desempenho_partida (
                        id_jogador, id_partida, id_campeao, id_funcao,
                        resultado, abates, mortes, assistencias,
                        cs_num, cs_por_minuto, runa_principal, feiticos
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        for (Partida p : partidas) {
            try {

                AdicionarPartida(p.getDataPartida(), p.getDuracao());

                Integer idPartida = BuscarIdPartidaEspecifica(p.getDataPartida(), p.getDuracao());

                Integer idCampeao = BuscarIdCampeao(p.getCampeao());

                if (idPartida == null) {
                    new LogErro("Falha ao recuperar ID da partida para o campeão " + p.getCampeao()).registrar(this);
                    continue;
                }

                if (idCampeao == null) {
                    new LogErro("Pulando partida com campeão '" + p.getCampeao() + "' (não encontrado).").registrar(this);
                    continue;
                }

                jdbc.update(sql,
                        idJogador,
                        idPartida,
                        idCampeao,
                        p.getFuncao(),
                        p.getResultado(),
                        p.getKill(),
                        p.getDeath(),
                        p.getAssists(),
                        p.getCs(),
                        p.getCsPorMin(),
                        p.getRunas(),
                        p.getFeitico()
                );
            } catch (Exception e) {
                new LogErro("Falha grave ao inserir partida (Jogador: %s, Campeão: %s): %s"
                        .formatted(p.getNomePlayer(), p.getCampeao(), e.getMessage()))
                        .registrar(this);
            }
        }
    }

    public void limparBanco() {
        JdbcTemplate jdbc = getConnection();
        new LogInfo("Iniciando limpeza das tabelas transacionais...").registrar(this);

        try {
            jdbc.execute("SET FOREIGN_KEY_CHECKS = 0;");

            new LogInfo("Limpando 'partida'...").registrar(this);
            jdbc.execute("TRUNCATE TABLE partida;");

            new LogInfo("Limpando 'desempenho_partida'...").registrar(this);
            jdbc.execute("TRUNCATE TABLE desempenho_partida;");

        } catch (Exception e) {
            new LogErro("Erro durante a limpeza do banco: " + e.getMessage()).registrar(this);
        } finally {
            new LogInfo("Reabilitando chaves estrangeiras...").registrar(this);
            jdbc.execute("SET FOREIGN_KEY_CHECKS = 1;");
            new LogSucesso("Tabelas do banco de dados limpas.").registrar(this);
        }
    }

    public void inserirLog(String status, String mensagem) {
        JdbcTemplate jdbc = getConnection();

        String sql = """
                    INSERT INTO log (id_conta, status_log, mensagem)
                    VALUES (?, ?, ?)
                """;

        try {
            String msg = (mensagem != null && mensagem.length() > 500) ? mensagem.substring(0, 500) : mensagem;

            jdbc.update(sql, null, status, msg);

        } catch (Exception e) {
            System.err.println("--- FALHA CRÍTICA AO INSERIR LOG NO BANCO ---");
            System.err.println("Status: " + status);
            System.err.println("Mensagem: " + mensagem);
            e.printStackTrace();
            System.err.println("---------------------------------------------");
        }
    }

    public void gerarHistoricoRanking() {
        JdbcTemplate jdbc = getConnection();

        String sql = """
        WITH RankedPlayers AS (
            SELECT 
                j.id_jogador,
                ROUND(COALESCE(SUM(dp.abates), 0) / NULLIF(COUNT(dp.id_partida), 0), 3) AS kda,
                ROUND(
                    (SUM(CASE WHEN dp.resultado = 'vitoria' THEN 1 ELSE 0 END) / 
                    NULLIF(COUNT(dp.id_partida), 0)) * 100, 2
                ) AS taxa_vitorias,
                COALESCE(j.pontos_total, 0) AS premiacao,
                j.ordem_elo,
                j.divisao,
                ROW_NUMBER() OVER (
                    ORDER BY 
                        j.ordem_elo DESC,
                        divisao ASC,
                        premiacao DESC,
                        taxa_vitorias DESC,
                        kda DESC
                ) AS posicao_ranking
            FROM jogador j
            LEFT JOIN desempenho_partida dp
                ON dp.id_jogador = j.id_jogador
            GROUP BY 
                j.id_jogador
        )
        SELECT * FROM RankedPlayers;
    """;

        // 1) EXECUTA A QUERY
        List<ResultadoRanking> ranking = jdbc.query(sql, (rs, rowNum) ->
                new ResultadoRanking(
                        rs.getInt("id_jogador"),
                        rs.getInt("posicao_ranking")
                )
        );

        // 2) INSERE CADA LINHA NO HISTÓRICO
        String insertSql =
                "INSERT INTO ranking_historico (id_jogador, posicao, data_registro) " +
                        "VALUES (?, ?, NOW())";

        for (ResultadoRanking r : ranking) {
            jdbc.update(insertSql, r.getIdJogador(), r.getPosicao());
        }

        System.out.println("Ranking salvo no histórico com sucesso!");
    }

}