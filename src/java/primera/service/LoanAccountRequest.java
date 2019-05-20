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

public class LoanAccountRequest {
 
    
    private String CustomerNo;
     private String currency;
     private String category;
     
    //private String Category;
    //private String Currency;
    private String AccountName;
    private String TransRef;
    private String BusinessSegmentCode;
    private String AccountMnemonic;
    private String LimitReference;
    private String BlockedReasons;
    private String OpeningDate;
    private String ValueDate;
    private String Gender;
    private String IPPISnumber;
    private String BranchLocation;
    private String hash;
    private String applicationID;
    private String authenticationID;
    private String apikey;
    
    
    
            
}