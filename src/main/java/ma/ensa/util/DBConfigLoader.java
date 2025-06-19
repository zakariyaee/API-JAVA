package ma.ensa.util;

import ma.ensa.db.ConnectionProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBConfigLoader {

    /**
     * Charge les propriétés de connexion à partir du fichier de configuration
     *
     * @param configPath Chemin vers le fichier de configuration
     * @return Propriétés de connexion
     */
    public ConnectionProperties loadConnectionProperties(String configPath,String prefix) {
        Properties props = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configPath)) {
            if (input == null) {
                throw new IOException("Fichier de configuration non trouvé: " + configPath);
            }

            props.load(input);

            return mapToConnectionProperties(props,prefix);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement de la configuration: " + e.getMessage(), e);
        }
    }

    private ConnectionProperties mapToConnectionProperties(Properties props,String prefix) {
        int port = Integer.parseInt(props.getProperty(prefix + "db.port", "0"));
        boolean autoCommit = Boolean.parseBoolean(props.getProperty("db.autoCommit", "true"));

        return ConnectionProperties.builder()
                .dbType(props.getProperty(prefix + "db.type"))
                .host(props.getProperty(prefix + "db.host"))
                .port(port)
                .database(props.getProperty(prefix + "db.database"))
                .username(props.getProperty(prefix + "db.username"))
                .password(props.getProperty(prefix + "db.password"))
                .autoCommit(autoCommit)
                .build();
    }
}
