package tech.brownbear.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Loads database credentials, connect to the database using a Hikari connection pool.
 * Executes NamedPreparedStatements, Transactions and simple queries
 **/
public class DbContext implements AutoCloseable {
    protected static Logger logger = LoggerFactory.getLogger(DbContext.class);

    private static final String HOST_NAME = "127.0.0.1";
    private static final int PORT = 3306;

    private final String dbName;
    private final Credentials credentials;

    private final HikariDataSource ds;

    public DbContext(String dbName, CredentialsProvider credentialsProvider) {
        this.dbName = dbName;
        this.credentials = credentialsProvider.get();
        this.ds = connect();
    }

    private HikariDataSource connect() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + HOST_NAME + ":" + PORT + "/" + dbName);
        config.setUsername(credentials.getUserName());
        config.setPassword(credentials.getPassword());
        config.addDataSourceProperty("serverTimezone", "UTC");
        config.addDataSourceProperty("useSSL", "false");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("useLocalTransactionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        config.setMaximumPoolSize(20);
        //config.setMinimumIdle(0);
        config.setConnectionTimeout(2000);
        config.setLeakDetectionThreshold(2000);

        return new HikariDataSource(config);
    }

    public <T> List<T> executeQuery(String query, RowMapper<T> rowMapper) throws SQLException {
        try (Connection connection = ds.getConnection();
            ConnectionArtifacts artifacts = new ConnectionArtifacts()) {
            List<T> results = new ArrayList<>();
            artifacts.with(connection.prepareStatement(query));
            artifacts.with(artifacts.ps().executeQuery());
            int rowNo = 1;
            while (artifacts.rs().next()) {
                results.add(rowMapper.map(artifacts.rs(), rowNo++));
            }
            return results;
        }
    }
 
    public int executeUpdate(String query) throws SQLException {
        try (Connection connection = ds.getConnection();
            PreparedStatement ps = connection.prepareStatement(query)) {
            return ps.executeUpdate(query);
        }
    }

    public <T> List<T> executeQuery(RawPreparedStatement ps, RowMapper<T> rowMapper) throws SQLException {
        try (Connection connection = ds.getConnection();
             ConnectionArtifacts artifacts = new ConnectionArtifacts()) {
            return ps.executeQuery(connection, artifacts, rowMapper);
        }
    }

    public int executeUpdate(RawPreparedStatement ps) throws SQLException {
        try (Connection connection = ds.getConnection();
             ConnectionArtifacts artifacts = new ConnectionArtifacts()) {
            return ps.executeUpdate(connection, artifacts);
        }
    }

    public Integer executeInsert(RawPreparedStatement ps) throws SQLException {
        try (Connection connection = ds.getConnection();
             ConnectionArtifacts artifacts = new ConnectionArtifacts()) {
            return ps.executeInsert(connection, artifacts);
        }
    }

    public void executeBulkInsert(RawPreparedStatement ps) throws SQLException {
        try (Connection connection = ds.getConnection();
             ConnectionArtifacts artifacts = new ConnectionArtifacts()) {
            ps.executeBulkInsert(connection, artifacts);
        }
    }

    public <R> void runTransaction(Transaction<R> transaction, R result) throws SQLException {
        SQLException exception = null;
        Connection connection = null;
        ArtifactCollection artifacts = new ArtifactCollection();
        try {
            connection = ds.getConnection();
            connection.setAutoCommit(false);
            transaction.run(connection, artifacts, result);
            connection.commit(); 
        } catch (SQLException transactionException) {
            exception = transactionException;
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (Exception rollbackException) {
                logger.error("Issue rolling back connection", rollbackException);
            }
        } finally {
            Closeables.closeQuietly(artifacts);
            Closeables.closeQuietly(connection);
        }
        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public void close() {
        ds.close(); 
    }
}
