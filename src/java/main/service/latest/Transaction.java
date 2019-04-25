/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.service.latest;


import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Temitope
 */
@Getter @Setter
public class Transaction {
   private String ValueDate;
   private String TransactionID;
   private String Amount;
   private String Currency;
   private String Reference;
   private String InstitutionCode;
   private Boolean IsSuccessful;
   private String Message;
}
