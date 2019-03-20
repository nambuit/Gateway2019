/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package primera.service;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author dogor-Igbosuah
 */
@Getter @Setter
public class MicroCreditRequest {
    private String Title;
    private String Surname;
    private String MiddleName;
    private String FirstName;  
    private String DateofBirth;
    private String Initials;
    private String CustomerOpeningDate;
    private String Industry;
    private String Introducer;
    private String RepaymentType;
    private String BranchMISCode;
    private String GuarantorName;
    private String AccountOfficer;
    private String PrimeraRefer;
    private String RefereeName;
    private String RefereePhoneNo;
    //private String EntityType;
    
}
