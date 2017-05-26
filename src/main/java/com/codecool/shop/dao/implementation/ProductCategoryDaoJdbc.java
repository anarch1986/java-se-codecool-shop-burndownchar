package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.model.ProductCategory;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database using implementation of the ProductCategoryDao interface.
 */
public class ProductCategoryDaoJdbc implements ProductCategoryDao {
    private static ProductCategoryDaoJdbc instance = null;
    private static PropertiesReader propertiesReader = new PropertiesReader("connection.properties");

    public static ProductCategoryDaoJdbc getInstance() {
        if (instance == null) {
            instance = new ProductCategoryDaoJdbc();
        }
        propertiesReader.readData();
        return instance;
    }

    public static void setPropertiesReader(String fileName) {
        ProductCategoryDaoJdbc.propertiesReader = new PropertiesReader(fileName);
    }

    /**
     * Adds a row into the database's productcategory table. The data for this row is coming from the parameter ProductCategory object.
     *
     * @param productCategory ProductCategory object
     */
    @Override
    public void add(ProductCategory productCategory) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + propertiesReader.db_url + "/"
                            + propertiesReader.database,
                    propertiesReader.user,
                    propertiesReader.psw);
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("INSERT INTO productcategory (name, description) VALUES (?,?);");
            preparedStatement.setString(1, productCategory.getName());
            preparedStatement.setString(2, productCategory.getDescription());
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
     * Finds a row by it's id (primary key) in the database's productcategory table, and returns it as a ProductCategory object.
     *
     * @param id primary key field in the productcategory table
     * @return ProductCategory object
     */

    public ProductCategory find(int id) {

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + propertiesReader.db_url + "/"
                            + propertiesReader.database,
                    propertiesReader.user,
                    propertiesReader.psw);
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("SELECT * FROM productcategory WHERE id = ?;",
                            ResultSet.TYPE_FORWARD_ONLY,
                            ResultSet.CONCUR_READ_ONLY,
                            ResultSet.CLOSE_CURSORS_AT_COMMIT);
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                ProductCategory productCategory = new ProductCategory(
                        result.getString("name"),
                        result.getString("department"),
                        result.getString("description"));
                productCategory.setId(result.getInt("id"));
                return productCategory;
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
     * Returns all rows in the productcategory table.
     *
     * @return List containing ProductCategory objects.
     */
    public List<ProductCategory> getAll() {
        Connection connection = null;
        List<ProductCategory> productCategories = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + propertiesReader.db_url + "/"
                            + propertiesReader.database,
                    propertiesReader.user,
                    propertiesReader.psw);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM productcategory",
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                ProductCategory productCategory = new ProductCategory(
                        result.getString("name"),
                        result.getString("department"),
                        result.getString("description"));
                productCategory.setId(result.getInt("id"));
                productCategories.add(productCategory);
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
        return productCategories;
    }

    /**
     * Removes a row from the productcategory table. The row is specified by the id parameter which is a primary key in the table.
     *
     * @param id id primary key field in the productcategory table
     */
    public void remove(int id) {

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + propertiesReader.db_url + "/"
                            + propertiesReader.database,
                    propertiesReader.user,
                    propertiesReader.psw);
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM productcategory WHERE id = ?;");
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
     * Deletes all rows from the productcategory table. It only used for testing on the dummy database.
     * <p>Truncates the table and restarts it's primary key. Also it runs as cascaded,
     * so it will delete all products associated with the productcategories.
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
            statement.executeUpdate("TRUNCATE productcategory RESTART IDENTITY CASCADE;");
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

