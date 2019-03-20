package prm.tools;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Temitope
 */



 public enum NIBBsResponseCodes {
  SUCCESS("00","Approved or completed successfully"),
  Status_unknown("01","Status unknown, please wait for settlement report"),
  Invalid_Sender("03","Invalid Sender"),
  Do_not_honor("05","Do not honor"),
  Dormant_Account("06","Dormant Account"),
  Invalid_Account("07","Invalid Account"),
  Account_Name_Mismatch("08","Account Name Mismatch"),
  Request_processing_in_progress("09","Request processing in progress"),
  Invalid_transaction("12","Invalid transaction"),
  Invalid_Amount("13","Invalid Amount"),
  Invalid_Batch_Number("14","Invalid Batch Number"),
  Invalid_Session_or_Record_ID("15","Invalid Session or Record ID"),
  Unknown_Bank_Code("16","Unknown Bank Code"),
  Invalid_Channel("17","Invalid Channel"),
  Wrong_Method_Call("18","Wrong Method Call"),
  No_action_taken("21","No action taken"),
  Unable_to_locate_record("25","Unable to locate record"),
  Duplicate_record("26","Duplicate record"),
  Format_error("30","Format error"),
  Contact_sending_bank("35","Contact sending bank"),
  No_sufficient_funds("51","No sufficient funds"),
  Transaction_not_permitted_to_sender("57","Transaction not permitted to sender"),
  Transaction_not_permitted_on_channel("58","Transaction not permitted on channel"),
  Transfer_limit_Exceeded("61","Transfer limit Exceeded"),
  Security_violation("63","Security violation"),
  Exceeds_withdrawal_frequency("65","Exceeds withdrawal frequency"),
  Response_received_too_late("68","Response received too late"),
  Unsuccessful_Account_Amount_block("69","Unsuccessful Account/Amount block"),
  Unsuccessful_Account_Amount_unblock("70","Unsuccessful Account/Amount unblock"),
  Empty_Mandate_Reference_Number("71","Empty Mandate Reference Number"),
  Beneficiary_Bank_not_available("91","Beneficiary Bank not available"),
  Routing_error("92","Routing error"),
  Duplicate_transaction("94","Duplicate transaction"),
  System_malfunction("96","System malfunction"),
  Timeout("97","Timeout waiting for response from destination"),
  Maximun_transaction("0A","Maximum transaction amount set by NIBSS exceeded"),
  Unknown_requestor("0B","Unknown requestor"),
  Payer_blacklisted("0C","Payer blacklisted"),
  Unknown_merchant_code("0D","Unknown merchanct code"),
  Transaction_not_permitted_to_merchant("0E","Transaction not permitted to merchant"),
  Maximum_transaction_amount("0F","Maximum transaction amount permitted to mercahnt exceeded"),
  Maximum_daily_transaction_limit("0G","Maximum daily transaction limit to payer exceeded"),
  Maximum_transaction_amount_permitted("0H","Maximum transaction amount permitted to payer exceeded"),
  Invalid_BVN("0J","Invalid BVN"),
  More_than_one_BVN("0K","More than one BVN tied to phone number"),
  No_BNV_tied_to_account("0L","No BVN tied to account"),
  N0_account_returned("0M","No account returned"),
  Name_enquiry_failed("0N","Name enquired failed"),
  Wrong_date_of_birth("0P","Wrong date of birth"),
  Phone_number_swapped("0Q","Phone number swapped"),
  Multiple_merchant_code_request("0R","Multiple merchant code request")
  ;
  
  
  private String code, message;
 
    private NIBBsResponseCodes(String value, String message) {
    this.code = value;
    this.message = message;
    }
    
    public String getCode(){
        return code;
    }
    
    public String getMessage(){
        
        return message;
    }
    
       public String getInlaksCode(){
        
        return "INK_NIP_"+code;
    }
    
 
    
  }

