package com.codecool.shop.dao.implementation;

import java.io.*;
import java.util.Properties;


/**
 * Reads and holds all data from a *.properties file. Used for making the database connections.
 */

public class PropertiesReader  {
    public  String fileName;
    public static String user;
    public static String psw;
    public static String database;
    public static String db_url;

    /**
     *
     * @param fileName a *.properties file. It contains all the necessary data for making a database connection.
     */
    public PropertiesReader(String fileName) {
        this.fileName = fileName;
    }


    /**
     * Reads the file defined in the fileName field, and sets the instance's other fields from the file.
     */
    public void readData() {
        try {
            Properties properties = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
            properties.load(inputStream);
            database = properties.getProperty("database");
            user = properties.getProperty("user");
            db_url = properties.getProperty("url");
            psw = properties.getProperty("password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
