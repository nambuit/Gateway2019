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
 * @author dogor-Igbosuah
 */
@Getter
@Setter
public class LoanDisbursedResponse {

    private String TransactionRef;
    private String Product;
    private String T24AccountNumber;
    private String CustomerName;
    private String ExtAcctNo;
    private String OracleNumber;
    private String EmployerName;
    private String CustomerEmail;
    private String BankBranch;
    private String MobileNo;
    private String CustomerRelationNumber;
    private String BookedAmount;
    private String DisbursedAmount;
    private String ValueDate;
    private String RepaymentStartDate;
    private String RepaymentType;
    private String MaturityDate;
    private String MeansOfPayment;
    private String LoanTenor;
    private String InterestRate;
    private String PrincipalRepayment;
    private String IntRepay;
    private String TotalMonthlyRepay;
    private String AcctOfficer;
    private String LoanType;
    private String Gender;
    private String Industry;
    private String Sector;
    private String GuarantorName;
    private String Age;
    private String OldTransactionRef;
    private String OriginalLoanAmount;
    private String OriginalDisbursedAmount;
    private String Introducer;
    private String LoanDisbursedAmount;
    private String ResponseCode;
    private String ResponseText;
    private String Message;

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append("ResponseCode:" + ResponseCode + " ");
        builder.append("ResponseText:" + ResponseText + " ");
        builder.append("Message:" + Message + " ");
        return builder.toString();

    }
}
