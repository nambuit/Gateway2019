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
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm.ss");
        SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
       
        Date trandate = sdf.parse(accountdetails.getValueDate());
        
        String[] ofsoptions = new String[] { "", "I", "PROCESS", "", "0" };
       String[] credentials = new String[] {options.getOfsuser(), options.getOfspass() };
        List<DataItem> items = new LinkedList<>();
    //List<String> headers = result.get(0);
        
        accountdetails.setValueDate(ndf.format(trandate));
        
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
               item.setItemHeader("ACCOUNT.TITLE");
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
}
