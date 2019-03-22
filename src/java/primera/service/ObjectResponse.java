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
    
    
    
    
}
