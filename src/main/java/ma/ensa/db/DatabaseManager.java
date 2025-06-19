package ma.ensa.db;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface DatabaseManager extends AutoCloseable {

    /**
     * Établit une connexion à la base de données
     *
     * @param properties Propriétés de connexion
     * @return true si la connexion est établie avec succès
     */
    boolean connect(ConnectionProperties properties);

    /**
     * Exécute une requête SELECT et retourne les résultats
     *
     * @param query Requête SQL à exécuter
     * @param params Paramètres de la requête (optionnel)
     * @return Liste de Map contenant les résultats (nom_colonne -> valeur)
     */
    List<Map<String, Object>> executeQuery(String query, Object... params);

    /**
     * Exécute une requête de modification (INSERT, UPDATE, DELETE)
     *
     * @param query Requête SQL à exécuter
     * @param params Paramètres de la requête (optionnel)
     * @return Nombre de lignes affectées
     */
    int executeUpdate(String query, Object... params);

    /**
     * Commence une transaction
     */
    void beginTransaction();

    /**
     * Valide la transaction en cours
     */
    void commitTransaction();

    /**
     * Annule la transaction en cours
     */
    void rollbackTransaction();

    /**
     * Obtient la connexion JDBC sous-jacente
     *
     * @return L'objet Connection
     */
    Connection getConnection();

    /**
     * Ferme la connexion à la base de données
     */
    @Override
    void close();
}
