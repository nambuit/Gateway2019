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
public class ChequeCollectionRequest {
    
    
    private String CustomerLoanNo;
    private String T24AcctNo;
    private String CustomerCIF;
    private String CustomerName;
    private String SalesOfficer;
    private String CustomerBankBranch;
    private String ChequeSerialNumber;
    //private String ChequeStatus;
    private String ChequeAcctNo;
    private String DateCollected;
    private String DateChequePresented;
    private String ChequeSignedBy;
    private String Comment;
    private String hash;
    private String InterfaceName;
    
    
}
