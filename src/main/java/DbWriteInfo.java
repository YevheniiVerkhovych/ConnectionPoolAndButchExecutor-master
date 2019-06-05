import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;


public class DbWriteInfo implements  Runnable {
private int id;
    public DbWriteInfo(int id){
        this.id = id;
        }
    public void run() {
        FileInputStream fis;
        Properties property = new Properties();
        Connection connection = null;

        try {
            fis = new FileInputStream("src/main/resources/config.properties");
            property.load(fis);

        } catch (IOException e) {
            System.err.println("File with resources NOT FOUND!");
        }
            String query2 = property.getProperty("query2");
            String query3 = property.getProperty("query3");
            String query4 = property.getProperty("query4");
            String query5 = property.getProperty("query5");
        try {
            connection = ConnectionPool.getConnection();
        } catch (InterruptedException | SQLException e) {
            e.printStackTrace();
        }
        try {
            BatchQueriesExecutor.executeBatch(connection, new String[]{query2, query3, query4, query5});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
