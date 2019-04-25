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
public class CategEntBookRequest {
    
    private String Ccy;
    private String BookingDate;
    private String Category;
    private String Narration;
    private String RefNo;
    private String Debit;
    private String Credit;
    
}
