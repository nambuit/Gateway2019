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
 * @author primeralive
 */
@Getter
@Setter
public class IclChequeResponse {
    private String T24ChequeID;
    private String CustomerLoanID;
    private String T24AcctNo;
    private String CustomerCIF;
    private String CustomerName;
    private String SalesOfficer;
    private String CustomerBank;
    private String CustomerChequeSerialNumber;
    private String CustomerChequeStatus;
    private String CustomerAccount;
    private String DateCollected;
    private String Comment;
    private String ResponseCode;
    private String ResponseText;
    private String Message;

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append("ResponseCode:" + ResponseCode + " ");
        builder.append("ResponseText:" + ResponseText + " ");
        builder.append("Message:" + Message + " ");
        return builder.toString();

    }
}
