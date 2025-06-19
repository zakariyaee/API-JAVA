package ma.ensa.db;

import ma.ensa.util.ResultSetMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DatabaseManagerImpl implements DatabaseManager {

    private final ConnectionFactory connectionFactory;
    private Connection connection;

    public DatabaseManagerImpl(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public boolean connect(ConnectionProperties properties) {
        if (connection != null) {
            close();
        }

        connection = connectionFactory.createConnection(properties);
        return connection != null;
    }

    @Override
    public List<Map<String, Object>> executeQuery(String query, Object... params) {
        checkConnection();

        try (PreparedStatement stmt = prepareStatement(query, params);
             ResultSet rs = stmt.executeQuery()) {

            return ResultSetMapper.toMapList(rs);
        } catch (SQLException e) {
            throw new QueryExecutionException("Erreur lors de l'exécution de la requête: " + e.getMessage(), e);
        }
    }

    @Override
    public int executeUpdate(String query, Object... params) {
        checkConnection();

        try (PreparedStatement stmt = prepareStatement(query, params)) {
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new QueryExecutionException("Erreur lors de l'exécution de la mise à jour: " + e.getMessage(), e);
        }
    }

    @Override
    public void beginTransaction() {
        checkConnection();

        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new QueryExecutionException("Erreur lors du début de la transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public void commitTransaction() {
        checkConnection();

        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new QueryExecutionException("Erreur lors de la validation de la transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public void rollbackTransaction() {
        checkConnection();

        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new QueryExecutionException("Erreur lors de l'annulation de la transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public Connection getConnection() {
        checkConnection();
        return connection;
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                // Ignorer l'exception lors de la fermeture
            } finally {
                connection = null;
            }
        }
    }

    private void checkConnection() {
        if (connection == null) {
            throw new DatabaseConnectionException("Aucune connexion établie. Veuillez appeler connect() d'abord.");
        }
    }

    private PreparedStatement prepareStatement(String query, Object... params) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);

        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        return stmt;
    }
}
