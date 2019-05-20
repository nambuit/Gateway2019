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
public class AcctBalResponse {
    
    private String Customer;
    private String Name;
    private String LimitReference;
    private String AccountNumber;
    private String Product;
    private String Currency;
    private String WorkingBalance;
    private String LedgerBalance;
    private String ClearedBalance;
    private String LockedAmount;
    private String ResponseCode;
    private String ResponseText;
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
