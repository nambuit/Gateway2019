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
@Getter
@Setter
public class FtNarrateRequest {

   
    private String accountNumber;
    private String creditORdebit;
    private String hash;
    private String apikey;
    private String authenticationID;
    private String applicationID;
    
}

