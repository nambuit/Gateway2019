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
 * @author emusa
 */
@Getter
@Setter
public class FtHistRequest {

    private String TransRef;
    private String DrAcctNo;
    private String CrAcctNo;
    private String DrValDate;
    private String CrValDate;
    private String DrCcy;
    private String DrAmount;
    private String CrCcy;
    private String CrAmount;
    
}
