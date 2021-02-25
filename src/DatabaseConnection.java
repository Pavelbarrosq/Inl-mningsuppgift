import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public Connection databaseLink;

    public Connection getConnection() {

        //LOCAL HOST
        String databaseNameLocal = "shoestore";
        String databaseUserLocal = "root";
        String passwordLocal = "isLegitPassword=true";

        String urlToOwn = "jdbc:mysql://localhost/" + databaseNameLocal;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(urlToOwn, databaseUserLocal, passwordLocal);
            return databaseLink;

        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }

        return null;

    }
}
