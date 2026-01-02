import java.sql.Connection;
import java.sql.DriverManager;

public class DB {
    private static final String URL = "jdbc:sqlite:taskflow.db";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL);
    }
}
