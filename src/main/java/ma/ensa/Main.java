package ma.ensa;

import ma.ensa.db.*;
import ma.ensa.util.DBConfigLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private static final List<ConnectionFactory> factories = new ArrayList<>();

    static {
        // Enregistrer les factories disponibles
        factories.add(new MySQLConnectionFactory());
        factories.add(new PostgreSQLConnectionFactory());
        factories.add(new SQLServerConnectionFactory());
    }

    public static void main(String[] args) {
        // Exemple 1: Connexion via propriétés codées en dur
        //exampleWithHardcodedProperties();

        // Exemple 2: Connexion via fichier de configuration
       // exampleWithConfigFile();
        //exemplePostgreSQL();
        exemplSQLSERVER();
    }

/*    private static void exampleWithHardcodedProperties() {
        System.out.println("Exemple avec propriétés codées en dur:");

        // Créer les propriétés de connexion
        ConnectionProperties properties = ConnectionProperties.builder()
                .dbType("mysql")
                .host("localhost")
                .port(3306)
                .database("test_db")
                .username("root")
                .password("")
                .build();

        // Sélectionner la factory appropriée
        ConnectionFactory factory = factories.stream()
                .filter(f -> f.supports(properties.getDbType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Type de base de données non supporté: " + properties.getDbType()));

        // Créer le gestionnaire de base de données
        try (DatabaseManager dbManager = new DatabaseManagerImpl(factory)) {
            // Se connecter à la base de données
            dbManager.connect(properties);

            // Exécuter une requête SELECT
            List<Map<String, Object>> results = dbManager.executeQuery("SELECT * FROM users WHERE age > ?", 18);

            // Afficher les résultats
            for (Map<String, Object> row : results) {
                System.out.println(row);
            }

            // Exécuter une requête UPDATE
            int rowsAffected = dbManager.executeUpdate("UPDATE users SET name = ? WHERE id = ?", "John Smith", 1);

            System.out.println("Lignes affectées: " + rowsAffected);
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    private static void exampleWithConfigFile() {
        System.out.println("\nExemple avec fichier de configuration:");

        try {
            // Charger les propriétés à partir du fichier de configuration
            DBConfigLoader configLoader = new DBConfigLoader();
            ConnectionProperties properties = configLoader.loadConnectionProperties("db.properties","mysql.");

            // Sélectionner la factory appropriée
            ConnectionFactory factory = factories.stream()
                    .filter(f -> f.supports(properties.getDbType()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Type de base de données non supporté: " + properties.getDbType()));

            // Créer le gestionnaire de base de données
            try (DatabaseManager dbManager = new DatabaseManagerImpl(factory)) {
                // Se connecter à la base de données
                dbManager.connect(properties);

                // Commencer une transaction
                dbManager.beginTransaction();

                try {
                    // Exécuter plusieurs requêtes dans une transaction
                    dbManager.executeUpdate("INSERT INTO users (id, name, age) VALUES (?, ?, ?)", 4, "Alice", 22);
                    dbManager.executeUpdate("UPDATE users SET age = age + 1 WHERE age < ?", 30);

                    // Valider la transaction
                    dbManager.commitTransaction();
                } catch (Exception e) {
                    // Annuler la transaction en cas d'erreur
                    dbManager.rollbackTransaction();
                    throw e;
                }

                // Afficher les utilisateurs après la transaction
                List<Map<String, Object>> users = dbManager.executeQuery("SELECT * FROM users ORDER BY id");
                for (Map<String, Object> user : users) {
                    System.out.println(user);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }*/
    /*private static void exemplePostgreSQL(){
        System.out.println("\n Exemple de POSTGRESQL:");
        try{
        DBConfigLoader configLoader = new DBConfigLoader();
        ConnectionProperties properties = configLoader.loadConnectionProperties("db.properties","POSTGRE.");
            ConnectionFactory factory = factories.stream()
                    .filter(f -> f.supports(properties.getDbType()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Type de base de données non supporté: " + properties.getDbType()));
            try (DatabaseManager dbManager = new DatabaseManagerImpl(factory)) {
                // Se connecter à la base de données
                dbManager.connect(properties);

                // Commencer une transaction
                dbManager.beginTransaction();

                try {
                    // Exécuter plusieurs requêtes dans une transaction
                    dbManager.executeUpdate("INSERT INTO users (id, name) VALUES (?, ?)", 4, "Alice");
                    dbManager.executeUpdate("UPDATE users SET name = 'zaki' WHERE id < ?", 30);

                    // Valider la transaction
                    dbManager.commitTransaction();
                } catch (Exception e) {
                    // Annuler la transaction en cas d'erreur
                    dbManager.rollbackTransaction();
                    throw e;
                }

                // Afficher les utilisateurs après la transaction
                List<Map<String, Object>> users = dbManager.executeQuery("SELECT * FROM users ORDER BY id");
                for (Map<String, Object> user : users) {
                    System.out.println(user);
                }
            }


            }
    catch (Exception e){
        System.err.println("Erreur: " + e.getMessage());}
    }*/
    private static void exemplSQLSERVER(){
        System.out.println("\n Exemple de SQLSERVER:");
        try{
            DBConfigLoader configLoader = new DBConfigLoader();
            ConnectionProperties properties = configLoader.loadConnectionProperties("db.properties","sql.");
            ConnectionFactory factory = factories.stream()
                    .filter(f -> f.supports(properties.getDbType()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Type de base de données non supporté: " + properties.getDbType()));
            try (DatabaseManager dbManager = new DatabaseManagerImpl(factory)) {
                // Se connecter à la base de données
                dbManager.connect(properties);

                // Commencer une transaction
                dbManager.beginTransaction();

                try {
                    // Exécuter plusieurs requêtes dans une transaction
                    dbManager.executeUpdate("INSERT INTO users (id, name) VALUES (?, ?)", 4, "Alice");
                    dbManager.executeUpdate("UPDATE users SET name = ? WHERE id < ?", "zaki", 30);

                    // Valider la transaction
                    dbManager.commitTransaction();
                } catch (Exception e) {
                    // Annuler la transaction en cas d'erreur
                    dbManager.rollbackTransaction();
                    throw e;
                }

                // Afficher les utilisateurs après la transaction
                List<Map<String, Object>> users = dbManager.executeQuery("SELECT * FROM users");
                for (Map<String, Object> user : users) {
                    System.out.println(user);
                }
            }


        }
        catch (Exception e){
            System.err.println("Erreur: " + e.getMessage());}
    }

}

