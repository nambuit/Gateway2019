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
 * @author dogor-Igbosuah
 */
@Getter @Setter
public class FtHistResponse {

    private String TransferReference;
    private String TransactionDetails;
    private String DebitAcctNo;
    private String CreditAcctNo;
    private String DebitValueDate;
    private String CreditValueDate;
    private String Currency;
    private String DebitAmount;
    private String CreditAmount;
    private String ResponseCode;
    private String ResponseText;
    private String Message;
    private String ProfitCenterDepartment;

    
    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append("ResponseCode:" + ResponseCode + " ");
        builder.append("ResponseText:" + ResponseText + " ");
        builder.append("Message:" + Message + " ");
        return builder.toString();

    }
}
