/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.service;

import com.service.*;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Temitope
 */
@Getter @Setter 
public class AccountBalance {
    private String AvailableBalance;
    private String LedgerBalance;
    private String AccountNumber;
    private String Currency;
    private String InstitutionCode;
    private Boolean IsSuccessful;
    private String Message;
    
}
