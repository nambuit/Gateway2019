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
public class NonIndividualCustomerRequest {
    
    private String NameofEntity;
    private String AbbreviatedName;
    private String DateRegistered;
    private String RegistrationNo;
    private String CustomerOpeningDate;
    //private String EntityType;
    private String Industry;
    private String Sector;
    //private String CreditCheckDone;
    private String CreditIndicator;
    //private String ConsenttoDisclosure;
    private String DateofSignature;
    private String LoansWrittenOff;
    private String AccountOfficer;
    private String CustomerStatus;
    private String PrimeraRefer;
    private String RefereeName;
    private String RefereePhoneNo;
    private String hash;
    
}
