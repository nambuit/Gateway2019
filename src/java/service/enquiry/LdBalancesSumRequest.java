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
public class LdBalancesSumRequest {
    
   private String Date;
   private String TotalPayment;
   private String Principal;
   private String Charges;
   private String Interest;
   private String Outstanding;
   
    
}
