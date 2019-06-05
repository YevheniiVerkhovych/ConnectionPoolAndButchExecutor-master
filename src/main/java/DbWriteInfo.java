import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class DbWriteInfo implements  Runnable {
private int id;
List<String> list = new ArrayList<>();
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
            list.add(property.getProperty("query2"));
            list.add(property.getProperty("query3"));

        try {
            connection = ConnectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            BatchQueriesExecutor.executeBatch(connection, list);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
