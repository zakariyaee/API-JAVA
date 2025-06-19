# API de Connexion à la Base de Données

## Description

Cette API fournit une interface simple et flexible pour se connecter à différentes bases de données relationnelles et exécuter des requêtes SQL. Actuellement, l'API prend en charge les systèmes de gestion de bases de données suivants :

- MySQL
- PostgreSQL
- SQL Server
## voici la structure de mon API
ma/
└── ensa/
├── db/
│   ├
│   │├── ConnectionFactory.java
│   │├── MySQLConnectionFactory.java
│   │├── PostgreSQLConnectionFactory.java
│   │ └── SQLServerConnectionFactory.java
│   ├|
│   │├── DatabaseConnectionException.java
│   │└── QueryExecutionException.java
│   ├|
│   │└── ConnectionProperties.java
│   ├|
│   └── ResultSetMapper.java
│   ├|── DatabaseManager.java
│   └── DatabaseManagerImpl.java
├── util/
│   └── DBConfigLoader.java
|    |-ResultSetMapper
└── Main.java
resources/
└── db.properties

## Installation

Pour utiliser cette API dans votre projet, vous devez :

1. Ajouter le fichier JAR de l'API à votre projet
2. Configurer les propriétés de connexion à votre base de données
3. ajouter la dependance convenable a votre fichier .XMl (pour MySQL pas necessaire)

### Ajouter le JAR à votre projet

Si vous utilisez Maven, ajoutez la dépendance suivante à votre fichier `pom.xml` :

```xml
<dependency>
    <groupId>ma.ensa</groupId>
    <artifactId>db-connector</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/db-connector.jar</systemPath>
</dependency>
```

Pour Gradle, ajoutez ceci à votre fichier `build.gradle` :

```groovy
dependencies {
    implementation files('lib/db-connector.jar')
}
```

Sinon, vous pouvez simplement ajouter le JAR au classpath de votre projet.

## Configuration

Créez un fichier `db.properties` dans le répertoire des ressources de votre projet avec le contenu suivant :

```properties
# Type de base de données (mysql, postgresql, sqlserver)
db.type=mysql
db.host=localhost
db.port=3306
db.database=nom_de_votre_base
db.username=votre_utilisateur
db.password=votre_mot_de_passe
db.autoCommit=true
```

## Utilisation

### Établir une connexion

```java
import ma.ensa.db.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ExempleConnexion {
    
    public static void main(String[] args) {
        try {
            // Charger les propriétés de connexion
            Properties props = new Properties();
            try (InputStream input = ExempleConnexion.class.getClassLoader().getResourceAsStream("db.properties")) {
                props.load(input);
            }
            
            // Créer les propriétés de connexion
            ConnectionProperties connectionProps = ConnectionProperties.builder()
                .dbType(props.getProperty("db.type"))
                .host(props.getProperty("db.host"))
                .port(Integer.parseInt(props.getProperty("db.port")))
                .database(props.getProperty("db.database"))
                .username(props.getProperty("db.username"))
                .password(props.getProperty("db.password"))
                .autoCommit(Boolean.parseBoolean(props.getProperty("db.autoCommit", "true")))
                .build();
            
            // Créer la factory appropriée selon le type de base de données
            ConnectionFactory factory = null;
            String dbType = connectionProps.getDbType().toLowerCase();
            
            if ("mysql".equals(dbType)) {
                factory = new MySQLConnectionFactory();
            } else if ("postgresql".equals(dbType)) {
                factory = new PostgreSQLConnectionFactory();
            } else if ("sqlserver".equals(dbType)) {
                factory = new SQLServerConnectionFactory();
            } else {
                throw new IllegalArgumentException("Type de base de données non supporté: " + dbType);
            }
            
            // Créer le gestionnaire de base de données
            try (DatabaseManager dbManager = new DatabaseManagerImpl(factory)) {
                // Se connecter à la base de données
                boolean connected = dbManager.connect(connectionProps);
                
                if (connected) {
                    System.out.println("Connexion établie avec succès !");
                    // Faire des opérations sur la base de données...
                } else {
                    System.out.println("Échec de la connexion.");
                }
            }
        } catch (IOException | DatabaseConnectionException e) {
            e.printStackTrace();
        }
    }
}
```

### Exécuter des requêtes SELECT

```java
// Exécuter une requête SELECT simple
List<Map<String, Object>> resultats = dbManager.executeQuery("SELECT * FROM utilisateurs");

// Afficher les résultats
for (Map<String, Object> ligne : resultats) {
    System.out.println("ID: " + ligne.get("id"));
    System.out.println("Nom: " + ligne.get("nom"));
    System.out.println("Email: " + ligne.get("email"));
}

// Exécuter une requête avec des paramètres
List<Map<String, Object>> utilisateurs = dbManager.executeQuery(
    "SELECT * FROM utilisateurs WHERE age > ? AND actif = ?", 
    18, true
);
```

### Exécuter des requêtes de modification (INSERT, UPDATE, DELETE)

```java
// Insérer un nouvel enregistrement
int lignesAffectees = dbManager.executeUpdate(
    "INSERT INTO utilisateurs (nom, email, age) VALUES (?, ?, ?)",
    "Jean Dupont", "jean.dupont@example.com", 25
);

// Mettre à jour des enregistrements
int lignesMisesAJour = dbManager.executeUpdate(
    "UPDATE utilisateurs SET actif = ? WHERE derniere_connexion < ?",
    false, java.sql.Date.valueOf("2023-01-01")
);

// Supprimer des enregistrements
int lignesSupprimees = dbManager.executeUpdate(
    "DELETE FROM utilisateurs WHERE actif = ?",
    false
);
```

### Utiliser les transactions

```java
try {
    // Démarrer une transaction
    dbManager.beginTransaction();
    
    // Exécuter plusieurs opérations
    dbManager.executeUpdate("INSERT INTO commandes (client_id, montant) VALUES (?, ?)", 1, 99.99);
    int commandeId = /* récupérer l'ID de la commande insérée */;
    dbManager.executeUpdate("INSERT INTO details_commande (commande_id, produit_id, quantite) VALUES (?, ?, ?)", 
                           commandeId, 101, 2);
    dbManager.executeUpdate("UPDATE stocks SET quantite = quantite - ? WHERE produit_id = ?", 2, 101);
    
    // Valider la transaction
    dbManager.commitTransaction();
} catch (Exception e) {
    // En cas d'erreur, annuler la transaction
    dbManager.rollbackTransaction();
    throw e;
}
```

## Gestion des erreurs

L'API utilise deux types d'exceptions principales :

1. `DatabaseConnectionException` : Lancée lorsqu'il y a un problème de connexion à la base de données
2. `QueryExecutionException` : Lancée lorsqu'il y a un problème lors de l'exécution d'une requête SQL

Exemple de gestion des erreurs :

```java
try {
    dbManager.connect(connectionProps);
    dbManager.executeQuery("SELECT * FROM table_inexistante");
} catch (DatabaseConnectionException e) {
    System.err.println("Erreur de connexion : " + e.getMessage());
} catch (QueryExecutionException e) {
    System.err.println("Erreur d'exécution de requête : " + e.getMessage());
    // Vous pouvez obtenir l'exception SQL d'origine
    if (e.getCause() instanceof SQLException) {
        SQLException sqlEx = (SQLException) e.getCause();
        System.err.println("Code d'erreur SQL : " + sqlEx.getErrorCode());
    }
}
```

## Remarques importantes

- N'oubliez pas de fermer la connexion après utilisation en appelant la méthode `close()` ou en utilisant un bloc try-with-resources.
- Pour des raisons de sécurité, ne stockez jamais les mots de passe en clair dans votre code ou dans des fichiers de configuration versionnés.
- L'API s'occupe automatiquement de la prévention des injections SQL en utilisant des requêtes préparées.

## Extension de l'API

Si vous souhaitez ajouter le support pour d'autres systèmes de bases de données, vous pouvez créer une nouvelle classe qui implémente l'interface `ConnectionFactory`.
