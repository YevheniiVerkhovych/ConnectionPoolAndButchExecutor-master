import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPool {
    static int poolSize = 20;
    private static int timeOutMilSec = 3000;
    private static BlockingQueue<Connection> connectionsQueue = new ArrayBlockingQueue<>(poolSize);
    private static volatile long awaitingTime = System.currentTimeMillis();
    private static String url;
    private static String login;
    private static String password;


    static {
        System.out.println("Created!");
       ResourceBundle resourceBundle = ResourceBundle.getBundle("config");
       url = resourceBundle.getString("db.url");
       login = resourceBundle.getString("db.login");
       password = resourceBundle.getString("db.password");
            System.out.println("DB URL: " + url
                    + ", LOGIN: " + login
                    + ", PASSWORD: " + password);
            while (true) {
                try {
                    if (!connectionsQueue.offer(DriverManager.getConnection(url, login, password))) break;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

    }
    public static synchronized Connection getConnection() throws SQLException {

        if (!connectionsQueue.isEmpty()){return connectionsQueue.poll();}
        else {
            System.out.println("Run out of connections in pool!!!");                        ///////WARNING!
            return DriverManager.getConnection(url, login, password);
        }
    }
//    public static synchronized Connection getConnection() throws InterruptedException, SQLException { //What is the Object to synchronized to???
//
//        if (!connectionsQueue.isEmpty()){return connectionsQueue.poll();}
//        while ((System.currentTimeMillis()-awaitingTime) < timeOutMilSec) {
//        if (!connectionsQueue.isEmpty()){return connectionsQueue.take();}
//        }
//        awaitingTime = System.currentTimeMillis();
//        connectionsQueue.offer(DriverManager.getConnection(url, login, password));
//        return connectionsQueue.take();
//    }

    public static void closeConnection(Connection connection) throws SQLException {
        awaitingTime = System.currentTimeMillis();
        if (!connectionsQueue.offer(connection)) connection.close();
    }

    public static void getPoolSize(){
        System.out.println(connectionsQueue.size());
    }
}

class BatchQueriesExecutor {
   public static synchronized void executeBatch(Connection connection, List<String> list) throws SQLException {

        if(connection==null || list.size()==0) {return;} else {

            Statement statement = connection.createStatement();

            connection.setAutoCommit(false);

            Savepoint savepointOne = connection.setSavepoint("SavepointOne");

            try {
                for(String request : list) {
                    statement.execute(request);
                }
                connection.commit();

            } catch (SQLException e) {
                System.out.println("SQLException. Executing rollback to savepoint...");
                connection.rollback(savepointOne);
                statement.close();
            }

            connection.releaseSavepoint(savepointOne);

            statement.close();

            ConnectionPool.closeConnection(connection);

            System.out.println("Closing connection and statement and releasing resources...");
        }
   }
}


