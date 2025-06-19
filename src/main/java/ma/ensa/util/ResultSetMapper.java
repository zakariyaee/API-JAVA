package ma.ensa.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultSetMapper {

    /**
     * Convertit un ResultSet en une liste de Map
     *
     * @param rs ResultSet à convertir
     * @return Liste de Map où chaque Map représente une ligne (clé = nom de colonne, valeur = valeur de colonne)
     * @throws SQLException si une erreur se produit lors de l'accès au ResultSet
     */
    public static List<Map<String, Object>> toMapList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                Object value = rs.getObject(i);
                row.put(columnName, value);
            }

            results.add(row);
        }

        return results;
    }}

