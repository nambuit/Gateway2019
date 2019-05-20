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

public class GICListAccountResponse {

    private String AccountNumber;
    private String CustomerName;
    private String Category;
    private String Currency;
    private String OpeningDate;
    private String MaturityDate;
    private String CustomerNo;
    private String OnlineBalance;
    private String AccountMnemonic;
    private String CurDvndPnts;
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
