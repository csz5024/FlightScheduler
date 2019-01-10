/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flightapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author KC
 */
public class Flight {
    
    private static Connection connection = DBConnection.getConnection();
    private static int s = 0;
  
    public static boolean addFlight(String plane, int seats ) {
      
        try {
            final String sqlInsert = "INSERT INTO FLIGHT(FLIGHTNAME, NUMSEATS)  VALUES(?,?)";
            final PreparedStatement statement = connection.prepareStatement(sqlInsert);
            statement.setString(1,plane);
            statement.setInt(2,seats);
            statement.executeUpdate();
            
            System.out.println("\n"+plane+" added successfully");
            return true;
        }
        catch (SQLIntegrityConstraintViolationException d) {
            System.out.println("\nflight name already in use, please choose another");
            return false;
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return false;
        }
    }
  
    public static ArrayList<String> getFLIGHTnames() {
    
        try {
            String sqlRequest = "SELECT FLIGHTNAME FROM FLIGHT";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlRequest);
         
            ArrayList<String> customers = new ArrayList<>();
            
            while(resultSet.next()){
                customers.add(resultSet.getString(1));
            }
            return customers;
        }
          
      
        catch (SQLException sqlException){
            sqlException.printStackTrace();
            return new ArrayList<>();
        }
     
    }
  
    public static ArrayList<String> getFLIGHTseats() {
    
        try {
            String CONNECTION_QUERY = "SELECT NUMSEATS FROM FLIGHT";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(CONNECTION_QUERY);
         
            ArrayList<String> customers = new ArrayList<>();
            while(resultSet.next()){
                customers.add(resultSet.getString(1));
            }
            return customers;
        }
          
      
        catch (SQLException sqlException){
            sqlException.printStackTrace();
            return new ArrayList<>();
        }
     
    }
    
    //unused: setters and getters to get available seat numbers in BookingQueries
    public static void setSeatSub(int seats) {
        s = seats;
    }
    
    public static int getSeatSub() {
        return s;
    }
    
}
