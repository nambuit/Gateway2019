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
public class ObjectResponse {
   
    private String TransRef;
    private String TransactionDate;
    private String ResponseCode;
    private String ResponseText;
    private String TransactionID;
    private String Amount;
    private String Currency;
    private String Reference;
    private String InstitutionCode;
    private Boolean IsSuccessful;
    private String Message;
    
    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
    
        builder.append("ResponseCode:" + ResponseCode + " ");
        builder.append("ResponseText:"  + ResponseText + " ");
        builder.append("TransactionDate:"  + TransactionDate + " ");
        builder.append("TransactionID:"  + TransactionID + " ");
        builder.append("Message:"  + Message + " ");
        return builder.toString();
        
    
}
}