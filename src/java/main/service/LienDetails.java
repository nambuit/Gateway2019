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
 */
@Getter @Setter 
public class LienDetails {
    private String AccountNumber;
    private String Amount;
    private String ReferenceNo;
    private String Description;
    private String PermissionLevel;
    private String FromDate;
    private String ToDate;
    private Boolean IsSuccessful;
    private String Message;
    private String InstitutionCode;
    
}
