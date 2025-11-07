package sptech.school;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import sptech.school.LogsExtracao.LogErro;
import sptech.school.LogsExtracao.LogInfo;
import sptech.school.LogsExtracao.LogSucesso;

import javax.sql.DataSource;
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

        // Usa INSERT IGNORE para evitar duplicatas com base em (game_name, tagline)
        String sql = """
                    INSERT IGNORE INTO jogador
                    (id_conta, id_organizacao, id_regiao, id_elo, game_name, tagline, nome, divisao, pontos_liga)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        for (Jogador j : jogadores) {
            try {
                // ✅ Garante que tagline nunca é nula (para manter UNIQUE funcionando)
                String taglineSegura = "";

                int linhasAfetadas = jdbc.update(sql,
                        null, // id_conta
                        null, // id_organizacao
                        j.getRegiao(),
                        j.getElo(),
                        j.getNickName(), // game_name
                        taglineSegura,   // sempre string vazia
                        j.getFullName(),
                        j.getEloDivisao(),
                        null // pontos_liga
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
            return 0;
        }
    }

    public void AdicionarPartida(Double duracao) {
        JdbcTemplate jdbc = getConnection();
        String sql = "INSERT INTO partida (datahora_inicio, duracao_segundos) VALUES (NOW(), ?)";
        jdbc.update(sql, duracao);
    }

    public Integer BuscarUltimaPartidaId() {
        JdbcTemplate jdbc = getConnection();
        String sql = "SELECT id_partida FROM partida ORDER BY id_partida DESC LIMIT 1";

        try {
            return jdbc.queryForObject(sql, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            new LogErro("Nenhuma partida encontrada na tabela 'partida' (está vazia?)").registrar(this);
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
                        id_jogador,
                        id_partida,
                        id_campeao,
                        id_funcao,
                        resultado,
                        abates,
                        mortes,
                        assistencias,
                        cs_num,
                        cs_por_minuto,
                        runa_principal,
                        feiticos
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        for (Partida p : partidas) {
            AdicionarPartida(p.getDuracao());
            Integer idUltimaPartida = BuscarUltimaPartidaId();
            Integer idCampeao = BuscarIdCampeao(p.getCampeao());

            jdbc.update(sql,
                    idJogador,
                    idUltimaPartida,
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

            new LogInfo("Limpando 'jogador_estatistica'...").registrar(this);
            jdbc.execute("TRUNCATE TABLE jogador_estatistica;");

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
                    INSERT INTO log (log_time, status_log, mensagem)
                    VALUES (NOW(), ?, ?)
                """;

        try {
            String msg = (mensagem != null && mensagem.length() > 500) ? mensagem.substring(0, 500) : mensagem;
            jdbc.update(sql, status, msg);
        } catch (Exception e) {
            System.err.println("--- FALHA CRÍTICA AO INSERIR LOG NO BANCO ---");
            System.err.println("Status: " + status);
            System.err.println("Mensagem: " + mensagem);
            e.printStackTrace();
            System.err.println("---------------------------------------------");
        }
    }
}
