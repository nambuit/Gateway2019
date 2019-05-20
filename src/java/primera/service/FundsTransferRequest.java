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
@Getter  @Setter
public class FundsTransferRequest {
    
    private String DebitCustomer;
    private String Currency;
    private String narration;
    private String AccountBalance;
    private String SignInstructions;
    private String DebitAccountNo;
    private String DebitAmount;
    private String DRValueDate;
    private String DebitRef;
    private String OrderedBy;
    private String CostCenter;
    private String CreditCustomer;
    private String CreditAccountNo;
    private String CreditRef;
    private String applicationID;
    private String authenticationID;
    private String apikey;
   // private String CustomerDetails;
    private String hash;
    private String InterfaceName;
    private String TransRef;
    
}
