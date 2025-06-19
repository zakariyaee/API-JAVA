package ma.ensa.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLServerConnectionFactory implements ConnectionFactory {

    private static final String JDBC_URL_TEMPLATE = "jdbc:sqlserver://%s:%d;databaseName=%s;encrypt=false";

    @Override
    public Connection createConnection(ConnectionProperties properties) {
        String url = String.format(
                JDBC_URL_TEMPLATE,
                properties.getHost(),
                properties.getPort(),
                properties.getDatabase()
        );

        try {
            Connection connection = DriverManager.getConnection(
                    url,
                    properties.getUsername(),
                    properties.getPassword()
            );

            connection.setAutoCommit(properties.isAutoCommit());
            return connection;
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Échec de connexion à SQL Server: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(String dbType) {
        return "sqlserver".equalsIgnoreCase(dbType);
    }
}
