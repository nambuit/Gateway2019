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
public class IclPublicStateRequest {
    
    private String LoanApplicationID;
    private String Customer;
    private String LoanApprovedDate;
    private String RepaymentStartDate;
    private String PrincipalAmount;
    private String LoanPurpose;
    private String ValueDate;
    private String MaturityDate;
    private String AccountOfficer;
    private String Introducer;
    private String ProvisionMethod;
    private String RepaymentType;
    private String MeansOfPayment;
    private String LoanDisbursedAmount;
    private String LoanType;
    private String AmountApproved;
    private String hash;
    private String InterfaceName;
      
}
