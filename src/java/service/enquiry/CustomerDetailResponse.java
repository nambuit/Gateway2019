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
 * @author primeralive
 */
@Getter
@Setter
public class CustomerDetailResponse {

    private String CustomerNumber;
    private String Mnemonic;
    private String Surname;
    private String FirstName;
    private String PreferredName;
    private String Address;
    private String Status;
    private String Officer;
    private String AccountOfficer;
    private String AccountMnemonic;

    private String Age;
    private String Email;
    private String TelephoneNo;
    private String EmployerName;
    private String EmployerAddress;
    private String NextofKinName;
    private String NextofKinTelephonenumber;
    private String CustomerBVN;
    private String BVNNumber;
    private String HomeAddress;
    private String Guarantor;
    private String Gender;
    private String LoanType;
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
