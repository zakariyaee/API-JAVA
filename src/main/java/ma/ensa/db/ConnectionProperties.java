package ma.ensa.db;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class ConnectionProperties {
    private String dbType;        // mysql, postgresql, sqlserver, oracle
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    @Builder.Default
    private boolean autoCommit = true;
}