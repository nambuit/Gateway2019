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
 * @author emusa
 */
@Getter @Setter
public class GICListAccountRequest {
    
    private String AccountNumber;
    private String CustomerName;
    private String Category;
    private String Currency;
    private String OnlineBalance;
    private String AccountMnemonic;
    private String CurDvndPnts;
    private String hash;
    private String apikey;
    private String authenticationID;
    private String applicationID;
    
}
