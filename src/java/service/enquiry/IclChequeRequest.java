/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.enquiry;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author emusa
 */
@Getter
@Setter
public class IclChequeRequest {

    private String T24ChequeID;
    private String CustLoanID;
    private String T24AcctNo;
    private String CustCIF;
    private String hash;
    private String CustomerName;
    private String SalesOfficer;
    private String CustomerBank;
    private String CustomerChequeSerialNumber;
    private String CustomerChequeStatus;
    private String CustomerAccount;
    private String DataCollected;
    private String Comment;
    private String apikey;
    private String applicationID;
    private String authenticationID;

}
