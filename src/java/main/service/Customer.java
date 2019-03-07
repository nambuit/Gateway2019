/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.service;


import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Temitope
 */@Getter @Setter 
public class Customer {
    private String CustomerNo;
    private String CustomerName;
    private String AccountOfficer;
    private String Nationality;
    private String Residence;
    private String Sector;
    private String Industry;
    private String InstitutionCode;
    private Boolean IsSuccessful;
    private String Message;
    
}
