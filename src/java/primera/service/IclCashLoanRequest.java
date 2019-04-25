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
@Getter
@Setter
public class IclCashLoanRequest {

    private String LoanApplicationID;
    private String Customer;
    private String Category;
    private String Currency;
    private String LoanPurpose;
    private int LoanAmount;
    private String RepaymentFrequency;
    private String RepaymentStartDate;
    private String LoanApprovedDate;
    private String DrawdownAccount;
    private String AutoSchedule;
    private String DefineSchedule;
    private String LiquidationMode;
    private double InterestRate;
    private String ValueDate;
    private String MaturityDate;
    private String ProvisionMethod;
    private String Comment;
    private String LoanDisbursedAmount;
    private String LoanType;
    private String RepaymentType;
    private String AccountOfficer;
    private String hash;
    private String InterfaceName;
    
}
