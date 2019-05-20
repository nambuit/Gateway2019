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
 * 
 * 
 */

@Getter @Setter
public class StatementResp {
   

    private String AccountNumber;
    private String DebitAmount;
    private String CIF;
    private String CustomerName;
    private String CreditAmount;
    private String ClosingBalance;
    private String BookingDate;
    private String ProcessingDate;
    private String ValueDate;
    private String Description;
    private String ReversalMarker;
    private String IncludeSubAcct;
    private String ApplDrilldown;
    private String ResponseCode;
    private String ResponseText;
    private String Reference;
    private String Narrative;
    private String CreditAcctNumber;
    private String Message;
    private String TransactionDate;

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append("ResponseCode:" + ResponseCode + " ");
        builder.append("ResponseText:" + ResponseText + " ");
        builder.append("Message:" + Message + " ");
        return builder.toString();

    }

}
