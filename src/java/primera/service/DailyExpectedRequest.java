/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package primera.service;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author emusa
 */
@Getter @Setter
public class DailyExpectedRequest {
    
    private String ID;
    private String Type;
    private String RepaymentDate;
    private String PrinAmount;
    private String InterestRepay;
    private String TotalRepayment;
}
