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
@Getter
@Setter

public class AccountDetailsResponse {

    private String Customer;
    private String Name;
    private String Product;
    private String AccountNumber;
    private String CustomerNumber;
    private String CustomerName;
    private String PostRestriction;
    private String Ccy;
    private String AccountOfficer;
    private String OnlineBalance;
    private String AcctOpeningDate;
    private String LoanType;
    private String SnapInputter;
    private String SnapAuthoriser;
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
