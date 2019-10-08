package dorian.database;

import java.sql.*;

/*
    Connection to database is made in this file
 */
public class SQLConnection {

    static Connection connection;

    public static Connection connectDb(){
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Manager.sqlite");
            return connection;
        }
        catch (Exception e){
            return null;
        }
    }
}
