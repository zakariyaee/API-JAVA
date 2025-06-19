package ma.ensa.db;

import java.sql.Connection;

public interface ConnectionFactory {

    /**
     * Crée une connexion à la base de données
     *
     * @param properties Propriétés de connexion
     * @return Une connexion JDBC
     */
    Connection createConnection(ConnectionProperties properties);

    /**
     * Vérifie si ce factory peut gérer le type de base de données spécifié
     *
     * @param dbType Type de base de données
     * @return true si ce factory peut gérer ce type
     */
    boolean supports(String dbType);
}
