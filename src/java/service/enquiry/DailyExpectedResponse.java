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

public class DailyExpectedResponse {

    private String ID;
    private String Type;
    private String RepaymentDate;
    private String PrinAmount;
    private String InterestRepay;
    private String TotalRepayment;
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
