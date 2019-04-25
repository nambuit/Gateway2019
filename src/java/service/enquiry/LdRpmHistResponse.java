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

public class LdRpmHistResponse {

    private String Date;
    private String SchType;
    private String AmountDue;
    private String AmountPaid;
    private String Balance;
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
