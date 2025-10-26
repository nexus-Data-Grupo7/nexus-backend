package sptech.school;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import sptech.school.LogsExtracao.Log;

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
            Log.erro("ERRO CRÍTICO: Variáveis de ambiente do banco de dados (DB_HOST, MYSQL_DATABASE, MYSQL_USER, MYSQL_PASSWORD) não estão definidas!");
            throw new IllegalStateException("Variáveis de ambiente do banco de dados não configuradas.");
        }

        BasicDataSource basicDataSource = new BasicDataSource();

        String url = String.format("jdbc:mysql://%s:3306/%s", host, bd);
        Log.info("Conectando ao banco de dados: " + url);
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
                INSERT INTO jogador
                (id_conta, id_organizacao, id_regiao, id_elo, game_name, tagline, nome, divisao, pontos_liga) VALUES
                (?,?,?,?,?,?,?,?,?)""";
        for (int i = 0; i < jogadores.size(); i++) {
            jdbc.update(sql, null, null, jogadores.get(i).getRegiao(), jogadores.get(i).getElo(), jogadores.get(i).getNickName(), null, jogadores.get(i).getFullName(), jogadores.get(i).getEloDivisao(), null);

            System.out.println("Jogador " + jogadores.get(i).getNickName() + " inserido com sucesso");
        }
    }

    public String BuscarJogadorId(String nomejogador) {
        JdbcTemplate jdbc = getConnection();
        String sql = "SELECT id_jogador FROM jogador WHERE LOWER(game_name) = LOWER(?)";

        try {
            return jdbc.queryForObject(sql, String.class, nomejogador);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Integer BuscarIdCampeao(String nomeCampeao) {
        JdbcTemplate jdbc = getConnection();

        String sql = "SELECT id_campeao FROM campeao WHERE LOWER(nome_campeao) = LOWER(?)";

        return jdbc.queryForObject(sql, Integer.class, nomeCampeao);
    }

    public void AdicionarPartida(Double duracao) {
        JdbcTemplate jdbc = getConnection();
        String sql = "INSERT INTO partida (datahora_inicio, duracao_segundos) VALUES" + "(?, ?)";

        jdbc.update(sql, null, duracao);
    }

    public Integer BuscarUltimaPartidaId() {
        JdbcTemplate jdbc = getConnection();
        String sql = "SELECT id_partida FROM partida ORDER BY id_partida DESC LIMIT 1";

        try {
            return jdbc.queryForObject(sql, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return null; // caso a tabela esteja vazia
        }
    }

    public void InserirDesempenhoPartida(List<Partida> partidas) {
        JdbcTemplate jdbc = getConnection();

        String idJogador = BuscarJogadorId(partidas.get(0).getNomePlayer());
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

        for (int i = 0; i < partidas.size(); i++) {
            Partida partidaAtual = partidas.get(i);
            AdicionarPartida(partidaAtual.getDuracao());

            jdbc.update(sql, idJogador, BuscarUltimaPartidaId(), BuscarIdCampeao(partidaAtual.getCampeao()), partidaAtual.getFuncao(), partidaAtual.getResultado(), partidaAtual.getKill(), partidaAtual.getDeath(), partidaAtual.getAssists(), partidaAtual.getCs(), partidaAtual.getCsPorMin(), partidaAtual.getRunas(), partidaAtual.getFeitico());

        }
    }

    public void limparBanco() {
        JdbcTemplate jdbc = getConnection();
        Log.info("Iniciando limpeza das tabelas transacionais...");

        try {
            // 1. Desabilita as chaves estrangeiras
            jdbc.execute("SET FOREIGN_KEY_CHECKS = 0;");

            // 2. Executa os TRUNCATEs (a ordem não importa aqui)
            Log.info("Limpando 'jogador'...");
            jdbc.execute("TRUNCATE TABLE jogador;");

            Log.info("Limpando 'partida'...");
            jdbc.execute("TRUNCATE TABLE partida;");

            Log.info("Limpando 'desempenho_partida'...");
            jdbc.execute("TRUNCATE TABLE desempenho_partida;");

            Log.info("Limpando 'jogador_estatistica'...");
            jdbc.execute("TRUNCATE TABLE jogador_estatistica;");

        } catch (Exception e) {
            Log.erro("Erro durante a limpeza do banco: " + e.getMessage());

        } finally {

            Log.info("Reabilitando chaves estrangeiras...");
            jdbc.execute("SET FOREIGN_KEY_CHECKS = 1;");
            Log.sucesso("Tabelas do banco de dados limpas.");
        }
    }
}