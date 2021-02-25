import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static DatabaseConnection connectNow = new DatabaseConnection();

    public static void main(String[] args) {

    intro();

    }

    public static void intro() {
        Scanner input = new Scanner(System.in);
        boolean verify = false;
        String currentUsername = null;
        boolean restart = false;

        do {

            do {
                System.out.println("Please enter your username:");
                String username = input.next().strip();
                System.out.println("Enter you password:");
                String password = input.next().strip();

                verify = verifyLogin(username, password);

                if (verify) {
                    currentUsername = username;
                }

            } while (!verify);

            optionSelection(currentUsername, restart);

        } while (!restart);

        
    }

    public static void optionSelection(String username, boolean exit) {
        String answer = "n";
        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Select an option");
            System.out.println("1) Create a order.\n"
                    + "2) See your order/s. \n" +
                    "3) Quit");
            int inputInt = sc.nextInt();

            switch (inputInt) {
                case 1:
                    System.out.println("Select a shoe you want to buy.");
                    showShoes();
                    int selectedShoe = sc.nextInt();

                    int userID = printUserID(username);

                    if(addToCart(userID, selectedShoe, null)) {
                        System.out.println("Product added successfully");
                    }
                    break;

                case 2:
                    // Se alla orders
                    showAllOrders(username);
                    break;
                case 3:
                    System.exit(1);
            }
        }

    }

    public static void showAllOrders(String username) {

        Connection connectDB = connectNow.getConnection();

        String orders = "Select DISTINCT fullname, brandName, orderCollectionID, orderDate FROM Orders " +
        "JOIN OrderCollection OC on Orders.orderID = OC.orderID " +
        "JOin Product P on OC.productID = P.productID " +
        "JOIN Brand B on B.brandID = P.brandID " +
        "JOIN Customer C on C.customerID = Orders.customerID " +
        "WHERE username = '" + username + "'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(orders);

            while (resultSet.next()) {
                String fullName = resultSet.getString("fullName");
                String brandName = resultSet.getString("brandName");
                String orderCollectionID = resultSet.getString("orderCollectionID");
                String orderDate = resultSet.getString("orderDate");

                System.out.println("Name: " + fullName + ". Product: " + brandName + ". Order: " + orderCollectionID + ". Date: " + orderDate);
            }

            statement.close();
            connectDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void showShoes() {

        Connection connectDB = connectNow.getConnection();

        String allShoes = "SELECT Brand.brandName, Product.price, Product.productID FROM Brand " +
        "INNER JOIN Product ON Brand.brandID = Product.brandID " +
        "INNER JOIN Stock ON Product.productID = Stock.productID " +
        "WHERE Stock.quantity > 0 " +
        "ORDER BY Product.productID ASC";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet resultSet = statement.executeQuery(allShoes);

            while (resultSet.next()) {
                String brand = resultSet.getString("brandName");
                String price = resultSet.getString("price");
                String productId = resultSet.getString("productID");

                System.out.println(productId + ") Brand: " + brand + " Price: " + price);
            }

            statement.close();
            connectDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public static Integer printUserID(String user) {

        Connection connectDB = connectNow.getConnection();
        
        String users = "Select * from Customer where Customer.username = '" + user + "'";


        try {
            Statement statement = connectDB.createStatement();
            ResultSet result = statement.executeQuery(users);

            if (result.next()) {
                return result.getInt("customerID");
            }

            statement.close();
            connectDB.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }


    public static boolean verifyLogin(String username1, String password1) {

        Connection connectDB = connectNow.getConnection();
        String verifyQuery = "SELECT count(1) FROM Customer WHERE username = '" + username1 + "' AND password ='" + password1 + "'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyQuery);

            //Finds the query matching to verifyLogin.
            while (queryResult.next()) {
                System.out.println(queryResult);
                if (queryResult.getInt(1) == 1) {

                    System.out.println("Logged in successfully with user: ");

                    return true;


                } else {
                    System.out.println("Invalid login");
                    return false;
                }
            }

            statement.close();
            connectDB.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Something went wrong");
        }

        return false;
    }

    public static Boolean addToCart(int customerID, int productID, Integer orderID) {
        Connection connectDB = connectNow.getConnection();
        try {
            CallableStatement stm = connectDB.prepareCall("{call addToCart(?, ?, ?)}");

            stm.setInt(1, customerID);
            stm.setInt(2, productID);

            if(orderID == null) {
                stm.setInt(3, -1);
            } else {
                stm.setInt(3, orderID);
            }

            boolean nextResultSet = stm.execute();

            while (true) {
                if (!nextResultSet) {

                    return true;
                }
                nextResultSet = stm.getMoreResults();

                stm.close();
                connectDB.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }
}


