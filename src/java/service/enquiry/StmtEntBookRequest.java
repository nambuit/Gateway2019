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
public class StmtEntBookRequest {
    
    private String Account;
    private String BookingDate;
    private String StartDate;
    private String EndDate;
    private String ProcessingDate;
    private String ValueDate;
    private String Description;
    private String ReversalMarker;
    private String IncludeSubAcct;
    private String ApplDrilldown;
}
