import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.*;

public class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class);

    public static final String USER_NAME = "root";
    public static final String PASSWORD = "anobys335";
    public static final String URL = "jdbc:mysql://localhost:3306/statistics_db";
    public Connection connection;
    public DBConnection(){
        try {
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        } catch (Throwable cause) {
            LOGGER.error("Connection failed",cause);
        }
    }
    public void updateDB(PreparedStatement statement){
        try {
            statement.executeUpdate();
        } catch (Throwable cause) {
            LOGGER.error("Can't execute query", cause);
        }
    }
    public void createTable() throws SQLException {
        Statement statement = connection.createStatement();
        String query = "CREATE TABLE IF NOT EXISTS statistics " +
                "(id SERIAL PRIMARY KEY, " +
                " date DATE, " +
                " url VARCHAR(50), " +
                " word VARCHAR(50), " +
                " count INTEGER)";
        statement.executeUpdate(query);
    }
}
