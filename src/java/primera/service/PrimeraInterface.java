/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package primera.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.naming.InitialContext;
import prm.tools.AppParams;
import prm.tools.NIBBsResponseCodes;

/**
 *
 * @author dogor-Igbosuah
 */
@WebService(serviceName = "PrimeraInterface")
public class PrimeraInterface {

 T24Link t24;
private String Ofsuser;
private String Ofspass;
AppParams options;
String logfilename = "PrimeraInterface";
NIBBsResponseCodes nibbsresp;
String SFactor = "Transaction Successful";
    
    public PrimeraInterface(){
           try
        {
        
         
            javax.naming.Context ctx = (javax.naming.Context) new InitialContext().lookup("java:comp/env");
            String Host =  (String) ctx.lookup("HOST");
            int port = Integer.parseInt((String)ctx.lookup("PORT"));
            String OFSsource =   (String) ctx.lookup("OFSsource");
            Ofsuser = (String) ctx.lookup("OFSuser");
            Ofspass = (String) ctx.lookup("OFSpass");
            t24 =  new T24Link(Host,port,OFSsource);
            
        } catch (Exception e)
        {
           System.out.println(e.getMessage());
        } 
    }  
    
    @WebMethod(operationName = "CurrentAccount")
    public ObjectResponse CurrentAccount(@WebParam(name = "accountdetails") CurrentAccountRequest accountdetails ) {
        ObjectResponse accountdetailresp = new ObjectResponse();
    try {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
       
        Date trandate = sdf.parse(accountdetails.getValueDate());
        Date transdate = sdf.parse(accountdetails.getOpeningDate());
        String[] ofsoptions = new String[] { "", "I", "PROCESS", "", "0" };
       String[] credentials = new String[] {Ofsuser,Ofspass};
        List<DataItem> items = new LinkedList<>();
    //List<String> headers = result.get(0);
    
        
        accountdetails.setValueDate(ndf.format(trandate));
        accountdetails.setOpeningDate(ndf.format(transdate));
        
               ofsParam param = new ofsParam();
               param.setCredentials(credentials);
               param.setOperation("ACCOUNT");
               param.setVersion("ICL.CURR.ACCT");
               param.setOptions(ofsoptions);
               param.setTransaction_id("");
               
               DataItem item = new DataItem();
               item.setItemHeader("CUSTOMER");
               item.setItemValues(new String[] {accountdetails.getCustomerNo()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("ACCOUNT.TITLE.1");
               item.setItemValues(new String[] {accountdetails.getAccountName()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("ACCOUNT.OFFICER");
               item.setItemValues(new String[] {accountdetails.getBusinessSegmentCode()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("LIMIT.REF");
               item.setItemValues(new String[] {accountdetails.getLimitReference()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("POSTING.RESTRICT");
               item.setItemValues(new String[] {accountdetails.getBlockedReasons()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("OPENING.DATE");
               item.setItemValues(new String[] {accountdetails.getOpeningDate()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("VALUE.DATE");
               item.setItemValues(new String[] {accountdetails.getValueDate()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("ENT.DETAILS");
               item.setItemValues(new String[] {accountdetails.getIPPISnumber()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("MIS.CODE");
               item.setItemValues(new String[] {accountdetails.getBranchLocation()});
               items.add(item);
               
               param.setDataItems(items);
               
               String ofstring = t24.generateOFSTransactString(param);

               String result = t24.PostMsg(ofstring);
               
               if(t24.IsSuccessful(result)){
                     
                   accountdetailresp.setResponseCode("00");
                   accountdetailresp.setIsSuccessful(true);
                   accountdetailresp.setMessage(SFactor);
                   accountdetailresp.setTransactionDate(ndf.format(trandate));
                 //accountdetailresp.setTransactionID(result.getClass()
                           
                 //txn.setTransactionID(result.get(i).get(headers.indexOf("@ID")).replace("\"", "")); 
               }
               
               else{
               
               accountdetailresp.setIsSuccessful(false);
               accountdetailresp.setMessage(result.split("/") [3]);
             //details(result.split("/")[3]);
           }
               
        
        
    } catch (ParseException ex) {
        accountdetailresp.setIsSuccessful(false);
        accountdetailresp.setMessage(ex.getMessage());
        
        
    }
        //ogger.getLogger(PrimeraInterface.class.getName()).log(Level.SEVERE, null, e//;
        
        return accountdetailresp;
  
    }
    
    
   
@WebMethod(operationName = "FundTransfer")
public ObjectResponse FundTransfer(@WebParam(name = "fdetails") FundsTransferRequest fdetails ) throws ParseException{
    
    ObjectResponse fdetailresp = new ObjectResponse();
    try {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
       
        Date trandate = sdf.parse(fdetails.getDRValueDate());
        //Date transdate = sdf.parse(fdetails.getOpeningDate());
        String[] ofsoptions = new String[] { "", "I", "PROCESS", "", "0" };
        String[] credentials = new String[] {Ofsuser,Ofspass};
        List<DataItem> items = new LinkedList<>();
        fdetails.setDRValueDate(ndf.format(trandate));
        
        ofsParam param = new ofsParam();
               param.setCredentials(credentials);
               param.setOperation("FUNDS.TRANSFER");
               param.setVersion("ICL.FT");
               param.setOptions(ofsoptions);
               param.setTransaction_id("");
               
        DataItem item = new DataItem();
               item.setItemHeader("DR.FULL.NAME");
               item.setItemValues(new String[] {fdetails.getDebitCustomer()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("DR.WORK.BAL");
               item.setItemValues(new String[] {fdetails.getAccountBalance()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("CONS.DISCLOSE");
               item.setItemValues(new String[] {fdetails.getSignInstructions()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("DEBIT.ACCT.NO");
               item.setItemValues(new String[] {fdetails.getDebitAccountNo()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("DEBIT.AMOUNT");
               item.setItemValues(new String[] {fdetails.getDebitAmount()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("DEBIT.VALUE.DATE");
               item.setItemValues(new String[] {fdetails.getDRValueDate()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("DEBIT.THEIR.REF");
               item.setItemValues(new String[] {fdetails.getDebitRef()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("ORDERING.CUST");
               item.setItemValues(new String[] {fdetails.getOrderedBy()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("PROFIT.CENTRE.DEPT");
               item.setItemValues(new String[] {fdetails.getCostCenter()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("CR.FULL.NAME");
               item.setItemValues(new String[] {fdetails.getCreditCustomer()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("CREDIT.ACCT.NO");
               item.setItemValues(new String[] {fdetails.getCreditAccountNo()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("CREDIT.VALUE.DATE");
               item.setItemValues(new String[] {fdetails.getDRValueDate()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("CREDIT.THEIR.REF");
               item.setItemValues(new String[] {fdetails.getCreditRef()});
               items.add(item);
               
          
               
               param.setDataItems(items);
               
               String ofstring = t24.generateOFSTransactString(param);

               String result = t24.PostMsg(ofstring);
               
               if(t24.IsSuccessful(result)){
                   
                  fdetailresp.setResponseCode("00");
                   fdetailresp.setIsSuccessful(true);
                   fdetailresp.setMessage(SFactor);
                   fdetailresp.setTransactionDate(ndf.format(trandate));
               }
            else{
               
              fdetailresp.setIsSuccessful(false);
              fdetailresp.setMessage(result.split("/") [3]);
             //details(result.split("/")[3]);
           }
}
  catch (ParseException ex) {
       fdetailresp.setIsSuccessful(false);
       fdetailresp.setMessage(ex.getMessage());         
    
} 
    
      return fdetailresp;
}

    @WebMethod(operationName = "D2RSCustomer")
    public ObjectResponse D2RSCustomer(@WebParam(name = "drdetails") D2RSPersonalDetailRequest drdetails ){
       
        ObjectResponse drobjectresp = new ObjectResponse();
        try {
            
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
       
        Date trandate = sdf.parse(drdetails.getDateofEvaluation());
        //Date transdate = sdf.parse(fdetails.getOpeningDate());
        String[] ofsoptions = new String[] { "", "I", "PROCESS", "", "0" };
        String[] credentials = new String[] {Ofsuser,Ofspass};
        List<DataItem> items = new LinkedList<>();
        drdetails.setDateofEvaluation(ndf.format(trandate));
        
        ofsParam param = new ofsParam();
               param.setCredentials(credentials);
               param.setOperation("CUSTOMER");
               param.setVersion("ICL.CUST.D2RS");
               param.setOptions(ofsoptions);
               param.setTransaction_id("");
               
               DataItem item = new DataItem();
               item.setItemHeader("TITLE");
               item.setItemValues(new String[] {drdetails.getTitle()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("SHORT.NAME");
               item.setItemValues(new String[] {drdetails.getSurname()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("NAME.2");
               item.setItemValues(new String[] {drdetails.getMiddleName()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("NAME.1");
               item.setItemValues(new String[] {drdetails.getFirstName()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("MARITAL.STATUS");
               item.setItemValues(new String[] {drdetails.getMaritalStatus()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("FORMER.NAME");
               item.setItemValues(new String[] {drdetails.getMaidenName()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("BIRTH.INCORP.DATE");
               item.setItemValues(new String[] {drdetails.getDateofBirth()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("INITIALS");
               item.setItemValues(new String[] {drdetails.getInitials()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("INDUSTRY");
               item.setItemValues(new String[] {drdetails.getIndustry()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("ACCOUNT.OFFICER");
               item.setItemValues(new String[] {drdetails.getLoanOfficer()});
               items.add(item);
               
               
               item = new DataItem();
               item.setItemHeader("OPENING.DATE");
               item.setItemValues(new String[] {drdetails.getDateofEvaluation()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("GENDER");
               item.setItemValues(new String[] {drdetails.getGender()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("BVN.NUMBER");
               item.setItemValues(new String[] {drdetails.getCustomerBVNNo()});
               items.add(item);
               
               item = new DataItem();
               item.setItemHeader("GROUP");
               item.setItemValues(new String[] {drdetails.getGroupName()});
               items.add(item);
               
               
               param.setDataItems(items);
               
               String ofstring = t24.generateOFSTransactString(param);

               String result = t24.PostMsg(ofstring);
               
               if(t24.IsSuccessful(result)){
                   
                  drobjectresp.setResponseCode("00");
                   drobjectresp.setIsSuccessful(true);
                   drobjectresp.setMessage(SFactor);
                   drobjectresp.setTransactionDate(ndf.format(trandate));
               }
               else{
               
              drobjectresp.setIsSuccessful(false);
              drobjectresp.setMessage(result.split("/") [3]);
             //details(result.split("/")[3]);
           }
               
            
        } catch (ParseException ex) {
         Logger.getLogger(PrimeraInterface.class.getName()).log(Level.SEVERE, null, ex);
     }
        return drobjectresp;
}
  
    
    
    
    
}