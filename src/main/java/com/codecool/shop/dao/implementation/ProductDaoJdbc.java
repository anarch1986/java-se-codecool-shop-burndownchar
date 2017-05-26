package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database using implementation of the ProductDao interface.
 */
public class ProductDaoJdbc implements ProductDao {
    private static ProductDaoJdbc instance = null;
    private SupplierDaoJdbc supplierDaoJdbc = SupplierDaoJdbc.getInstance();
    private ProductCategoryDaoJdbc productCategoryDaoJdbc = ProductCategoryDaoJdbc.getInstance();
    private static PropertiesReader propertiesReader = new PropertiesReader("connection.properties");

    private ProductDaoJdbc() {
    }

    public static void setPropertiesReader(String fileName) {
        ProductDaoJdbc.propertiesReader = new PropertiesReader(fileName);
    }

    public static ProductDaoJdbc getInstance() {
        if (instance == null) {
            instance = new ProductDaoJdbc();
        }
        return instance;
    }

    /**
     * * Adds a row into the database's product table. The data for this row is coming from the parameter Product object.
     *
     * @param product Product object
     */
    public void add(Product product) {
        Connection connection = null;


        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + propertiesReader.db_url + "/"
                            + propertiesReader.database,
                    propertiesReader.user,
                    propertiesReader.psw);
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("INSERT INTO product " +
                            "(name, description, currency, default_price, supplier_id, " +
                            "productcategory_id) VALUES (?,?,?,?,?,?);");
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setString(3, product.getDefaultCurrency().toString());
            preparedStatement.setFloat(4, product.getDefaultPrice());
            preparedStatement.setInt(5, product.getSupplier().getId());
            preparedStatement.setInt(6, product.getProductCategory().getId());
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
     * /**
     * Finds a row by it's id (primary key) in the database's product table, and returns it as a Product object.
     *
     * @param id primary key field in the product table
     * @return Product object
     */
    public Product find(int id) {
        Connection connection = null;
        String query = "SELECT * FROM Product WHERE id = ?;";
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + propertiesReader.db_url + "/"
                            + propertiesReader.database,
                    propertiesReader.user,
                    propertiesReader.psw);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Product WHERE id = ?;",
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                Product product = new Product(
                        result.getString("name"),
                        result.getFloat("default_price"),
                        result.getString("currency"),
                        result.getString("description"),
                        productCategoryDaoJdbc.find(result.getInt("supplier_id")),
                        supplierDaoJdbc.find(result.getInt("productcategory_id"))
                );
                product.setId(result.getInt("id"));
                return product;
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
        return null;
    }

    /**
     * * Returns all rows in the product table.
     *
     * @return List containing Product objects.
     */
    public List<Product> getAll() {
        Connection connection = null;
        List<Product> products = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + propertiesReader.db_url + "/"
                            + propertiesReader.database,
                    propertiesReader.user,
                    propertiesReader.psw);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM product",
                    ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                Product product = new Product(
                        result.getString("name"),
                        result.getFloat("default_price"),
                        result.getString("currency"),
                        result.getString("description"),
                        productCategoryDaoJdbc.find(result.getInt("productcategory_id")),
                        supplierDaoJdbc.find(result.getInt("supplier_id"))
                );
                product.setId(result.getInt("id"));
                products.add(product);
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
        return products;
    }

    /**
     * Removes a row from the product table. The row is specified by the id parameter which is a primary key in the table.
     *
     * @param id id primary key field in the product table
     */
    public void remove(int id) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + propertiesReader.db_url + "/"
                            + propertiesReader.database,
                    propertiesReader.user,
                    propertiesReader.psw);
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM product WHERE id = ?;");
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
     * Finds all products associated with the parameter Supplier object in the database.
     *
     * @param supplier Supplier object
     * @return List of Product objects
     */

    public List<Product> getBy(Supplier supplier) {
        Connection connection = null;
        List<Product> products = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + propertiesReader.db_url + "/"
                            + propertiesReader.database,
                    propertiesReader.user,
                    propertiesReader.psw);
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("SELECT * FROM Product WHERE supplier_id = ?;",
                            ResultSet.TYPE_FORWARD_ONLY,
                            ResultSet.CONCUR_READ_ONLY,
                            ResultSet.CLOSE_CURSORS_AT_COMMIT);
            preparedStatement.setInt(1, supplier.getId());
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                Product product = new Product(
                        result.getString("name"),
                        result.getFloat("default_price"),
                        result.getString("currency"),
                        result.getString("description"),
                        productCategoryDaoJdbc.find(result.getInt("productcategory_id")),
                        supplierDaoJdbc.find(result.getInt("supplier_id"))
                );
                product.setId(result.getInt("id"));
                products.add(product);
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
        return products;
    }

    /**
     * Finds all products associated with the parameter ProducCategory object in the database.
     *
     * @param productCategory ProductCategory object
     * @return List of Product objects
     */
    public List<Product> getBy(ProductCategory productCategory) {
        Connection connection = null;
        List<Product> products = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + propertiesReader.db_url + "/"
                            + propertiesReader.database,
                    propertiesReader.user,
                    propertiesReader.psw);
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("SELECT * FROM Product WHERE productcategory_id = ?;",
                            ResultSet.TYPE_FORWARD_ONLY,
                            ResultSet.CONCUR_READ_ONLY,
                            ResultSet.CLOSE_CURSORS_AT_COMMIT);
            preparedStatement.setInt(1, productCategory.getId());
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                Product product = new Product(
                        result.getString("name"),
                        result.getFloat("default_price"),
                        result.getString("currency"),
                        result.getString("description"),
                        productCategoryDaoJdbc.find(result.getInt("productcategory_id")),
                        supplierDaoJdbc.find(result.getInt("supplier_id"))
                );
                product.setId(result.getInt("id"));
                products.add(product);
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
        return products;
    }

    /**
     * Deletes all rows from the product table. It only used for testing on the dummy database.
     * <p>Truncates the table and restarts it's primary key. Also it runs as cascaded.
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
            statement.executeUpdate("TRUNCATE product RESTART IDENTITY CASCADE;");
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



