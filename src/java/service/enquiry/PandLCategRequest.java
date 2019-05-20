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

public class PandLCategRequest {

    private String PLCategory;
    private String StartDate;
    private String EndDate;
    private String authenticationID;
    private String applicationID;
    private String hash;
    private String apikey;
    
    
}
