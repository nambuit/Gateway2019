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
public class FtNarrateRequest {

    private String ID;
    private String DebitAcctNo;
    private String DrAcctName;
    private String DrAmount;
    private String DrValueDate;
    private String CrAcctNo;
    private String CrAcctName;
    private String PaymentDetails;

}
