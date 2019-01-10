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
public class Day {
    private static Connection connection = DBConnection.getConnection();
 
    public static boolean addDate(java.sql.Date date) throws SQLException {
        
        final String sql = "INSERT INTO DAY(DATE) VALUES(?)";
        final PreparedStatement stmt = connection.prepareStatement(sql);
        
        try {
            stmt.setDate(1, date);
            stmt.executeUpdate();
            
            System.out.println("\n Date "+date+" added successfully");
            return true;
        }
        catch (SQLIntegrityConstraintViolationException d) {
            System.out.println("\nDate already entered! please enter another");
            return false;
        }
        
        catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }
    
    public static ArrayList<String> getDay() {
    
        try {
            String CONNECTION_QUERY = "SELECT DATE FROM DAY";
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
    
    /*
    public static boolean isDupe(java.sql.Date date) {
        
        final ArrayList<String> days = new ArrayList();
        
        
        
    }*/
    
}
