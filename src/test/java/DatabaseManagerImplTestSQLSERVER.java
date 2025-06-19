import com.opencsv.CSVReader;
import ma.ensa.db.*;
import ma.ensa.util.DBConfigLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerImplTestSQLSERVER {
    private static DatabaseManager dbManager;
    @BeforeAll
    public static void setup(){
        DBConfigLoader loader=new DBConfigLoader();
        ConnectionProperties connexion=loader.loadConnectionProperties("db.properties","sql.");
        ConnectionFactory connexionfactory =new SQLServerConnectionFactory();
        dbManager = new DatabaseManagerImpl(connexionfactory);
        assertTrue(dbManager.connect(connexion));
        String createTableQuery = "IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'users' AND TABLE_SCHEMA = 'dbo') " +
                "BEGIN CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(100)) END";
        dbManager.executeUpdate(createTableQuery);


    }
    @Test
    public void testInsertUsersFromCsv() throws Exception{
        try (CSVReader reader = new CSVReader(new FileReader("src/test/resources/csv"))) {
            String[] nextLine;
            reader.readNext();
            dbManager.beginTransaction();
            try{
                while ((nextLine = reader.readNext()) != null) {
                    int id = Integer.parseInt(nextLine[0]);
                    String name = nextLine[1];
                    dbManager.executeUpdate("INSERT INTO users (id, name) VALUES (?, ?)", id, name);

                }
                dbManager.commitTransaction();}
            catch(Exception e){
                dbManager.rollbackTransaction();
                fail("Insert failed: " + e.getMessage());
            }
            List<Map<String, Object>> users=dbManager.executeQuery("SELECT * FROM users");
            assertFalse(users.isEmpty());

        }}
    @AfterAll
    public static void cleanup() {
        dbManager.executeUpdate("DELETE FROM users WHERE id = ?", 1);
        dbManager.close();
    }
    @BeforeEach
    public void cleanDatabase() {
        dbManager.executeUpdate("DELETE FROM users");
    }

}
