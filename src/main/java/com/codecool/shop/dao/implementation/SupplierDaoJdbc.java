package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database using implementation of the SupplierDao interface.
 */
public class SupplierDaoJdbc implements SupplierDao {

    private static SupplierDaoJdbc instance = null;
    private static PropertiesReader propertiesReader = new PropertiesReader("connection.properties");


    private SupplierDaoJdbc() {
    }

    public static SupplierDaoJdbc getInstance() {
        if (instance == null) {
            instance = new SupplierDaoJdbc();
        }
        propertiesReader.readData();
        return instance;
    }

    public static void setPropertiesReader(String fileName) {
        SupplierDaoJdbc.propertiesReader = new PropertiesReader(fileName);
    }

    /**
     * Adds a row into the database's supplier table. The data for this row is coming from the parameter Supplier object.
     * @param supplier Supplier object
     */
    @Override
    public void add(Supplier supplier) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + propertiesReader.db_url + "/"
                            + propertiesReader.database,
                    propertiesReader.user,
                    propertiesReader.psw);
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("INSERT INTO Supplier (name, description) VALUES (?,?);");
            preparedStatement.setString(1, supplier.getName());
            preparedStatement.setString(2, supplier.getDescription());
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.getStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /**
     * Finds a row by it's id (primary key) in the database's supplier table, and returns it as a Supplier object.
     * @param id id primary key field in the supplier table
     * @return Supplier object
     */
    public Supplier find(int id) {

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + propertiesReader.db_url + "/"
                            + propertiesReader.database,
                    propertiesReader.user,
                    propertiesReader.psw);
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("SELECT * FROM Supplier WHERE id = ?;",
                            ResultSet.TYPE_FORWARD_ONLY,
                            ResultSet.CONCUR_READ_ONLY,
                            ResultSet.CLOSE_CURSORS_AT_COMMIT);
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                Supplier supplier = new Supplier(
                        result.getString("name"),
                        result.getString("description"));
                supplier.setId(result.getInt("id"));
                return supplier;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return null;

    }

    /**
     * Returns all rows in the supplier table.
     * @return List containing Supplier objects.
     */
    public List<Supplier> getAll() {
        Connection connection = null;
        List<Supplier> supplierList = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + propertiesReader.db_url + "/"
                            + propertiesReader.database,
                    propertiesReader.user,
                    propertiesReader.psw);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM supplier",
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                Supplier supplier = new Supplier(
                        result.getString("name"),
                        result.getString("description"));
                supplier.setId(result.getInt("id"));
                supplierList.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return supplierList;
    }

    /**
     * Removes a row from the supplier table. The row is specified by the id parameter which is a primary key in the table.
     * @param id id primary key field in the supplier table
     */
    public void remove(int id) {

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + propertiesReader.db_url + "/"
                            + propertiesReader.database,
                    propertiesReader.user,
                    propertiesReader.psw);
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM supplier WHERE id = ?;");
            preparedStatement.setInt(1, id);
            preparedStatement.executeQuery();
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /**
     * Deletes all rows from the supplier table. It only used for testing on the dummy database.
     * <p>Truncates the table and restarts it's primary key. Also it runs as cascaded,
     * so it will delete all products associated with the suppliers.
     * You should never-ever use it on the primary database! You have been warned!</p>
     */

    public void removeAll() {

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + propertiesReader.db_url + "/"
                            + propertiesReader.database,
                    propertiesReader.user,
                    propertiesReader.psw);
            Statement statement = connection.createStatement();
            statement.executeUpdate("TRUNCATE supplier RESTART IDENTITY CASCADE;");
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}
