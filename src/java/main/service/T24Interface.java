/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.service;


import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.naming.InitialContext;

/**
 *
 * @author Temitope
 */     
@WebService(serviceName = "T24Interface")
public class T24Interface {
private T24Link t24;
private String Ofsuser;
private String Ofspass;
private String ImageBase;
    
    public T24Interface(){
           try
        {
        
         
            javax.naming.Context ctx = (javax.naming.Context) new InitialContext().lookup("java:comp/env");
            String Host =  (String) ctx.lookup("HOST");
            int port = Integer.parseInt((String)ctx.lookup("PORT"));
            String OFSsource =   (String) ctx.lookup("OFSsource");
            Ofsuser = (String) ctx.lookup("OFSuser");
            Ofspass = (String) ctx.lookup("OFSpass");
              ImageBase = (String) ctx.lookup("ImageBase");
             t24 =  new T24Link(Host,port,OFSsource);
            
        } catch (Exception e)
        {
           System.out.println(e.getMessage());
        } 
    }

    @WebMethod(operationName = "GetAccounts")
    public List<AccountDetails> GetAccounts(@WebParam(name = "InstitutionCode") String InstitutionCode, @WebParam(name = "AccountNumber") String acctNo) {
        //ResponseObject response = new ResponseObject();
         List<AccountDetails> alldetails = new ArrayList<>();
        try{
        
        //Mnemonic Product Account Id CLASS-POSNEG Ccy Account Officer
           
        //   Gson gson = new Gson(); 
           ArrayList<List<String>> result = t24.getOfsData("ACCOUNTS$PRIMERA",Ofsuser,Ofspass,"@ID:EQ="+acctNo.trim());
           List<String> headers = result.get(0);
           
           if(headers.size()!=result.get(1).size()){
               
               throw new Exception(result.get(1).get(0));
           }
           
          String Customer = result.get(1).get(headers.indexOf("Customer")).replace("\"", "").trim();
           result = t24.getOfsData("ACCOUNTS$PRIMERA",Ofsuser,Ofspass,"CUSTOMER:EQ="+Customer.trim());
            
       for(int i=1;i<result.size();i++){
           AccountDetails details = new AccountDetails();
        details.setAccountName(result.get(i).get(headers.indexOf("AccountName")).replace("\"", ""));
        details.setAccountNumber(result.get(i).get(headers.indexOf("Account Id")).replace("\"", ""));
        details.setCurrency(result.get(i).get(headers.indexOf("Ccy")).replace("\"", "")); 
        details.setAccountType(result.get(i).get(headers.indexOf("Product")).replace("\"", "")); 
        details.setCustomerName(result.get(i).get(headers.indexOf("CustomerName")).replace("\"", "")); 
        details.setOnlineActualBalance(result.get(i).get(headers.indexOf("AvailableBalance")).replace("\"", "")); 
        details.setAccountOfficer(result.get(i).get(headers.indexOf("Account Officer")).replace("\"", "")); 
        details.setPhoneNo(result.get(i).get(headers.indexOf("PhoneNo")).replace("\"", ""));
        details.setInstitutionCode(InstitutionCode);
        details.setIsSuccessful(true);
        alldetails.add(details);
      
       }
        }
        catch(Exception d){
          AccountDetails details = new AccountDetails();
          details.setIsSuccessful(false);
          details.setMessage(d.getMessage());
          alldetails.add(details);
        }
         return alldetails;
    }
    
    
     @WebMethod(operationName = "GetAccountByAccountNo")
    public AccountDetails GetAccountByAccountNo(@WebParam(name = "InstitutionCode") String InstitutionCode, @WebParam(name = "AccountNumber") String acctNo) {
        //ResponseObject response = new ResponseObject();
        AccountDetails details = new AccountDetails(); 
       try{       
        //Mnemonic Product Account Id CLASS-POSNEG Ccy Account Officer
           
        //   Gson gson = new Gson(); 
           ArrayList<List<String>> result = t24.getOfsData("ACCOUNTS$PRIMERA",Ofsuser,Ofspass,"@ID:EQ="+acctNo.trim());
           List<String> headers = result.get(0);
           
              if(headers.size()!=result.get(1).size()){
               
               throw new Exception(result.get(1).get(0));
           }

        details.setAccountName(result.get(1).get(headers.indexOf("AccountName")).replace("\"", ""));
        details.setAccountNumber(result.get(1).get(headers.indexOf("Account Id")).replace("\"", ""));
        details.setCurrency(result.get(1).get(headers.indexOf("Ccy")).replace("\"", "")); 
        details.setAccountType(result.get(1).get(headers.indexOf("Product")).replace("\"", "")); 
        details.setCustomerName(result.get(1).get(headers.indexOf("CustomerName")).replace("\"", "")); 
          details.setOnlineActualBalance(result.get(1).get(headers.indexOf("AvailableBalance")).replace("\"", "")); 
          details.setAccountOfficer(result.get(1).get(headers.indexOf("Account Officer")).replace("\"", "")); 
          details.setPhoneNo(result.get(1).get(headers.indexOf("PhoneNo")).replace("\"", ""));
          details.setInstitutionCode(InstitutionCode);
          details.setIsSuccessful(true);
      
        }
        catch(Exception d){
           details.setIsSuccessful(false);
          details.setMessage(d.getMessage());
        }
         return details;
    }
    
     @WebMethod(operationName = "GetBalance")
    public AccountBalance GetBalance(@WebParam(name = "InstitutionCode") String InstitutionCode, @WebParam(name = "AccountNumber") String acctNo) {
        //ResponseObject response = new ResponseObject();
        AccountBalance balance = new AccountBalance(); 
       try{       
        //Mnemonic Product Account Id CLASS-POSNEG Ccy Account Officer
           
         //  Gson gson = new Gson(); 
           ArrayList<List<String>> result = t24.getOfsData("ACCOUNTS$PRIMERA",Ofsuser,Ofspass,"@ID:EQ="+acctNo.trim());
           List<String> headers = result.get(0);
           
              if(headers.size()!=result.get(1).size()){
               
               throw new Exception(result.get(1).get(0));
           }

        balance.setLedgerBalance(result.get(1).get(headers.indexOf("LedgerBalance")).replace("\"", ""));
        balance.setAccountNumber(result.get(1).get(headers.indexOf("Account Id")).replace("\"", ""));
        balance.setAvailableBalance(result.get(1).get(headers.indexOf("AvailableBalance")).replace("\"", ""));
        balance.setCurrency(result.get(1).get(headers.indexOf("Ccy")).replace("\"", ""));
        balance.setInstitutionCode(InstitutionCode);
        balance.setIsSuccessful(true);
        }
        catch(Exception d){
          balance.setIsSuccessful(false);
          balance.setMessage(d.getMessage());
        }
         return balance;
    }
    
    @WebMethod(operationName = "GetCustomer")
    public Customer GetCustomer(@WebParam(name = "InstitutionCode") String InstitutionCode, @WebParam(name = "CustomerID") String CustomerID) {
        //ResponseObject response = new ResponseObject();
        Customer cust = new Customer(); 
       try{       
        //Mnemonic Product Account Id CLASS-POSNEG Ccy Account Officer
           
          // Gson gson = new Gson(); 
           ArrayList<List<String>> result = t24.getOfsData("%CUSTOMER",Ofsuser,Ofspass,"@ID:EQ="+CustomerID.trim());
           List<String> headers = result.get(0);
           
              if(headers.size()!=result.get(1).size()){
               
               throw new Exception(result.get(1).get(0));
           }

        cust.setCustomerName(result.get(1).get(headers.indexOf("Name")).replace("\"", ""));
        cust.setCustomerNo(result.get(1).get(headers.indexOf("Customer No")).replace("\"", "")); 
        cust.setAccountOfficer(result.get(1).get(headers.indexOf("Account Officer")).replace("\"", "")); 
        cust.setNationality(result.get(1).get(headers.indexOf("Nationality")).replace("\"", "")); 
        cust.setResidence(result.get(1).get(headers.indexOf("Residence")).replace("\"", "")); 
        cust.setSector(result.get(1).get(headers.indexOf("Sector")).replace("\"", "")); 
        cust.setIndustry(result.get(1).get(headers.indexOf("Industry")).replace("\"", ""));
        cust.setInstitutionCode(InstitutionCode);
        cust.setIsSuccessful(true);
        }
        catch(Exception d){
           cust.setIsSuccessful(false);
          cust.setMessage(d.getMessage());
        }
         return cust;
    }
    
     @WebMethod(operationName = "GetPassport")
    public CustomerPassport GetPassport(@WebParam(name = "InstitutionCode") String InstitutionCode, @WebParam(name = "CustomerID") String CustomerID) {
        //ResponseObject response = new ResponseObject();
        CustomerPassport passport = new CustomerPassport(); 
       try{       
        //  result = t24.getOfsData("%IM.IMAGE.TYPE","INPUTT","DEBENSON","@ID:EQ="+CustomerID.trim());
       ArrayList<List<String>> result  = t24.getOfsData("PRIMERA$IMAGE",Ofsuser,Ofspass,"IMAGE.REFERENCE:EQ="+CustomerID.trim()+",IMAGE.TYPE:EQ=PHOTOS,IMAGE.APPLICATION:EQ=CUSTOMER");
       List<String>  headers = result.get(0);  
       
          if(headers.size()!=result.get(1).size()){
               
               throw new Exception(result.get(1).get(0));
           }
       
     String path =  result.get(1).get(headers.indexOf("Path")).replace("\"", "").trim();
     String file =  result.get(1).get(headers.indexOf("IMAGE")).replace("\"", "").trim();
     
     if(path.startsWith(".")){
         path = path.substring(1, path.length());
     }
     path = path.replace("/", "\\");
     String filepath = ImageBase+path+file;
           
        File fi = new File(filepath);
        byte[] fileContent = Files.readAllBytes(fi.toPath());
        passport.setPhoto(fileContent);
        passport.setCustomerNo(result.get(1).get(headers.indexOf("Reference")).replace("\"", ""));
        passport.setInstitutionCode(InstitutionCode);
        passport.setIsSuccessful(true);
        }
        catch(Exception d){
           passport.setIsSuccessful(false);
          passport.setMessage(d.getMessage());
           
        }
         return passport;
    }
    
        @WebMethod(operationName = "Retreivesignature")
    public CustomerSignature Retreivesignature(@WebParam(name = "InstitutionCode") String InstitutionCode, @WebParam(name = "CustomerID") String CustomerID) {
        //ResponseObject response = new ResponseObject();
        CustomerSignature signature = new CustomerSignature(); 
       try{  
           
           ArrayList<List<String>> result  = t24.getOfsData("PRIMERA$IMAGE",Ofsuser,Ofspass,"IMAGE.REFERENCE:EQ="+CustomerID.trim()+",IMAGE.TYPE:EQ=SIGNATURES,IMAGE.APPLICATION:EQ=CUSTOMER");
       List<String>  headers = result.get(0);  
       
          if(headers.size()!=result.get(1).size()){
               
               throw new Exception(result.get(1).get(0));
           }
       
     String path =  result.get(1).get(headers.indexOf("Path")).replace("\"", "").trim();
     String file =  result.get(1).get(headers.indexOf("IMAGE")).replace("\"", "").trim();
     
     if(path.startsWith(".")){
         path = path.substring(1, path.length());
     }
     path = path.replace("/", "\\");
     String filepath = ImageBase+path+file;
           
        File fi = new File(filepath);
        byte[] fileContent = Files.readAllBytes(fi.toPath()); 
           
        signature.setSignature(fileContent);
        signature.setCustomerNo(CustomerID);
        signature.setInstitutionCode(InstitutionCode);
        signature.setIsSuccessful(true);
        }
        catch(Exception d){
          signature.setIsSuccessful(false);
          signature.setMessage(d.getMessage());
        }
         return signature;
    } 
    
        
     @WebMethod(operationName = "GetAccountsByPhoneNo")
    public List<AccountDetails> GetAccountsByPhoneNo(@WebParam(name = "InstitutionCode") String InstitutionCode, @WebParam(name = "PhoneNo") String PhoneNo) {
        //ResponseObject response = new ResponseObject();
         List<AccountDetails> alldetails = new ArrayList<>();
        try{
        
        //Mnemonic Product Account Id CLASS-POSNEG Ccy Account Officer
           
         //  Gson gson = new Gson(); 
           ArrayList<List<String>> result = t24.getOfsData("%CUSTOMER",Ofsuser,Ofspass,"PHONE.1:EQ="+PhoneNo.trim());
           List<String> headers = result.get(0);
           String Customer = result.get(1).get(headers.indexOf("Customer No")).replace("\"", "").trim();
           result = t24.getOfsData("ACCOUNTS$PRIMERA","INPUTT","DEBENSON","CUSTOMER:EQ="+Customer.trim());
           headers = result.get(0);  
           
              if(headers.size()!=result.get(1).size()){
               
               throw new Exception(result.get(1).get(0));
           }
           
       for(int i=1;i<result.size();i++){
           AccountDetails details = new AccountDetails();
        details.setAccountName(result.get(i).get(headers.indexOf("AccountName")).replace("\"", ""));
        details.setCurrency(result.get(i).get(headers.indexOf("Ccy")).replace("\"", "")); 
        details.setAccountNumber(result.get(i).get(headers.indexOf("Account Id")).replace("\"", ""));
        details.setAccountType(result.get(i).get(headers.indexOf("Product")).replace("\"", "")); 
        details.setCustomerName(result.get(i).get(headers.indexOf("CustomerName")).replace("\"", "")); 
          details.setOnlineActualBalance(result.get(i).get(headers.indexOf("AvailableBalance")).replace("\"", "")); 
          details.setAccountOfficer(result.get(i).get(headers.indexOf("Account Officer")).replace("\"", ""));
          details.setPhoneNo(result.get(i).get(headers.indexOf("PhoneNo")).replace("\"", ""));
          details.setInstitutionCode(InstitutionCode);
          details.setIsSuccessful(true);
       alldetails.add(details);
       
       }
        }
        catch(Exception d){
            AccountDetails details = new AccountDetails();
           details.setIsSuccessful(false);
          details.setMessage(d.getMessage());
          alldetails.add(details);
        }
         return alldetails;
    }
    
    
    @WebMethod(operationName = "GetCustomerTransactions")
    public List<Transaction> GetCustomerTransactions(@WebParam(name = "InstitutionCode") String InstitutionCode,@WebParam(name = "AccountNumber") String AccountNo,@WebParam(name = "Startdate") String Startdate,@WebParam(name = "Enddate") String Enddate) {
        //ResponseObject response = new ResponseObject();
        
         List<Transaction> txns = new ArrayList<>();
        try{  
            
           SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
           SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
            Date Start = sdf.parse(Startdate);
            Date End = sdf.parse(Enddate);
            
            Startdate = ndf.format(Start);
            Enddate = ndf.format(End);   
            
        //Mnemonic Product Account Id CLASS-POSNEG Ccy Account Office         
           //Gson gson = new Gson(); 
           ArrayList<List<String>> result = t24.getOfsData("%STMT.ENTRY",Ofsuser,Ofspass,"ACCOUNT.NUMBER:EQ="+AccountNo.trim()+",VALUE.DATE:RG="+Startdate+" "+Enddate);
           List<String> headers = result.get(0);
           
              if(headers.size()!=result.get(1).size()){
               
               throw new Exception(result.get(1).get(0));
           }
           
       for(int i=1;i<result.size();i++){
           Transaction txn = new Transaction();
        txn.setAmount(result.get(i).get(headers.indexOf("Amount Lccy")).replace("\"", ""));
        txn.setCurrency(result.get(i).get(headers.indexOf("CCY")).replace("\"", "")); 
        txn.setReference(result.get(i).get(headers.indexOf("Reference")).replace("\"", "")); 
        txn.setTransactionID(result.get(i).get(headers.indexOf("@ID")).replace("\"", "")); 
        txn.setValueDate(result.get(i).get(headers.indexOf("Value date")).replace("\"", "")); 
        txn.setInstitutionCode(InstitutionCode);
        txn.setIsSuccessful(true);
       txns.add(txn);
       }
        }
        catch(Exception d){
         Transaction txn = new Transaction();
         txn.setIsSuccessful(false);
          txn.setMessage(d.getMessage());
          txns.add(txn);
        }
         return txns;
    }
    
    
 @WebMethod(operationName = "PlaceLien")
    public  LienDetails  PlaceLien (@WebParam(name = "LienDetails") LienDetails details) {
  
       try{  
           
           
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            SimpleDateFormat ndf = new SimpleDateFormat("yyyyMMdd");
            
            Date Start = sdf.parse(details.getFromDate());
            Date End = sdf.parse(details.getToDate());
            
            details.setFromDate(ndf.format(Start));
            details.setToDate(ndf.format(End));  
           
           
           
           ofsParam param = new ofsParam();
             String [] credentials = new String [] {Ofsuser, Ofspass};
           param.setCredentials(credentials);
           param.setOperation("AC.LOCKED.EVENTS");
           param.setTransaction_id("");
           String [] options = new String [] {"", "I", "PROCESS", "", "0" };
           param.setOptions(options);
           
           List<DataItem> items = new LinkedList<>();
           DataItem item = new DataItem();
           item.setItemHeader("ACCOUNT.NUMBER.1");
           item.setItemValues(new String [] {details.getAccountNumber()});
           items.add(item);
           
           item = new DataItem();
           item.setItemHeader("FROM.DATE");
           item.setItemValues(new String [] {details.getFromDate()});
           items.add(item);
           
           item = new DataItem();
           item.setItemHeader("TO.DATE");
           item.setItemValues(new String [] {details.getToDate()});
           items.add(item);
           
           item = new DataItem();
           item.setItemHeader("LOCKED.AMOUNT");
           item.setItemValues(new String [] {details.getAmount()});
           items.add(item);
           
           item = new DataItem();
           item.setItemHeader("TRANSACTION.REF");
           item.setItemValues(new String [] {details.getReferenceNo()});
           items.add(item);
           
           item = new DataItem();
           item.setItemHeader("DESCRIPTION");
           item.setItemValues(new String [] {details.getDescription()});
           items.add(item);
           
           param.setDataItems(items);
              //ACLK1308680628
           String ofstr = t24.generateOFSTransactString(param);
           
           String result = t24.PostMsg(ofstr);
           
           if(t24.IsSuccessful(result)){
           
               details.setReferenceNo(result.split("/")[0]);
               details.setIsSuccessful(true);
               details.setMessage("Lien placed Successfully");
       }
           else{
               details.setReferenceNo("");
               details.setIsSuccessful(false);
                details.setMessage(result.split("/")[3]);
           }
           

          
        }
        catch(Exception d){
         details.setReferenceNo("");
         details.setIsSuccessful(false);
         details.setMessage(d.getMessage());
        }
       return details;  
    } 
    
    @WebMethod(operationName = "UnPlaceLien")
    public  LienDetails  UnPlaceLien (@WebParam(name = "LienDetails") LienDetails details) {
  
       try{  
           
           ofsParam param = new ofsParam();
           String [] credentials = new String [] {Ofsuser, Ofspass};
           param.setCredentials(credentials);
           param.setOperation("AC.LOCKED.EVENTS");
           param.setTransaction_id(details.getReferenceNo());
           String [] options = new String [] {"", "R", "PROCESS", "", "0" };
           param.setOptions(options);
           
           List<DataItem> items = new LinkedList<>();
           DataItem item = new DataItem();
           item.setItemHeader("ACCOUNT.NUMBER");
           item.setItemValues(new String [] {details.getAccountNumber()});
           items.add(item);     

           
           param.setDataItems(items);
              //ACLK1308680628
           String ofstr = t24.generateOFSTransactString(param);
           
           String result = t24.PostMsg(ofstr);
           
           if(t24.IsSuccessful(result)){
           
               details.setReferenceNo(result.split("/")[0]);
               details.setIsSuccessful(true);
               details.setMessage("Lien Unplaced Successfully");
       }
           else{
               details.setReferenceNo("");
               details.setIsSuccessful(false);
               details.setMessage(result.split("/")[3]);
           }
           

         
        }
        catch(Exception d){
                details.setReferenceNo("");
         details.setIsSuccessful(false);
         details.setMessage(d.getMessage()); 
        }
          return details;
    } 
    
     @WebMethod(operationName = "UpdatePhoneNo")
    public  ResponseObject  UpdatePhoneNo (@WebParam(name = "InstitutionCode") String InstitutionCode, @WebParam(name = "CustomerID") String CustomerID, @WebParam(name = "PhoneNo") String PhoneNo) {
             
            ResponseObject details = new ResponseObject();
       try{  
           
           details.setInstitutionCode(InstitutionCode);
           ofsParam param = new ofsParam();
           String [] credentials = new String [] {Ofsuser, Ofspass};
           param.setCredentials(credentials);
           param.setOperation("CUSTOMER");
           param.setTransaction_id(CustomerID);
           String [] options = new String [] {"", "I", "PROCESS", "", "0"};
           param.setOptions(options);
           
           List<DataItem> items = new LinkedList<>();
           DataItem item = new DataItem();
           item.setItemHeader("PHONE.1");
           item.setItemValues(new String [] {PhoneNo});
           items.add(item);     

           
           param.setDataItems(items);
      
           String ofstr = t24.generateOFSTransactString(param);
           
           String result = t24.PostMsg(ofstr);
           
           if(t24.IsSuccessful(result)){
           
              
               details.setIsSuccessful(true);
               details.setMessgae("PhoneNo Updated Successfully");
       }
           else{
               
               details.setIsSuccessful(false);
               details.setMessgae(result.split("/")[3]);
           }
           

         
        }
        catch(Exception d){
         details.setIsSuccessful(false);
         details.setMessgae(d.getMessage()); 
        }
        return details;  
    } 
    
    
    
    
}

