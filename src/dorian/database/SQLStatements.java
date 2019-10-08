package dorian.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLStatements {

    public static Connection connection;
    public static String question, answer;

    public SQLStatements(){
        connection = SQLConnection.connectDb();
        if(connection == null) System.exit(1);
    }

    // Querying database to find main login to system
    public static boolean loginDetails(String user, String pass){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM credentials WHERE username = ? AND password = ?";
        try{
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, pass);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return true;
            }
            else{
                return false;
            }
        }
        catch (SQLException e){
            return false;
        }

        finally{
            try {
                preparedStatement.close();
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean verifyUser(String user){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM credentials WHERE username = ?";
        try{
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                question = resultSet.getString("Question");
                answer = resultSet.getString("Answer");
                return true;
            }
            else{
                return false;
            }
        }
        catch (SQLException e){
            return false;
        }

        finally{
            try {
                preparedStatement.close();
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
