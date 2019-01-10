/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flightapp;

import java.sql.Timestamp;

/**
 *
 * @author KC
 */
public class BookingEntry {
    
    private String customer;
    private String flight;
    private String date;
    private Timestamp time;

    public BookingEntry(String customer, String flight, String date, Timestamp time) {
        this.customer = customer;
        this.flight = flight;
        this.date = date;
        this.time = time;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getFlight() {
        return flight;
    }

    public void setFlight(String flight) {
        this.flight = flight;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
