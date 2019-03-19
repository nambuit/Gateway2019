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
import main.service.T24Link;
import prm.tools.AppParams;

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
    public CurrentAccountResponse CurrentAccount(@WebParam(name = "accountdetails") CurrentAccountRequest accountdetails ) {
        CurrentAccountResponse accountdetailresp = new CurrentAccountResponse();
    try {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm.ss");
        SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
       
        Date trandate = sdf.parse(accountdetails.getValueDate());
        
        String[] ofsoptions = new String[] { "", "I", "PROCESS", "", "0" };
       String[] credentials = new String[] {options.getOfsuser(), options.getOfspass() };
        List<DataItem> items = new LinkedList<>();
        
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
        
        
    } catch (ParseException ex) {
         Logger.getLogger(PrimeraInterface.class.getName()).log(Level.SEVERE, null, ex);
     }
}
