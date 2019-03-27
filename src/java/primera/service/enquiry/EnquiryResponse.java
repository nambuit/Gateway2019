/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package primera.service.enquiry;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author dogor-Igbosuah
 */
@Getter @Setter
public class EnquiryResponse {
    private String TransactionDate;
    private String ResponseCode;
    private String ResponseText;
    private Boolean IsSuccessful;
    private String Message;
    
    
    
    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
    
        builder.append("ResponseCode:" + ResponseCode + " ");
        builder.append("ResponseText:"  + ResponseText + " ");
        builder.append("TransactionDate:"  + TransactionDate + " ");
        builder.append("IsSuccessful:"  + IsSuccessful + " ");
        builder.append("TransactionDate:"  + Message + " ");
        
        return builder.toString();
        
    
}
}
