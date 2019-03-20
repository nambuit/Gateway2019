package prm.tools;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.google.gson.Gson;
import com.sun.xml.bind.StringInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import javax.naming.InitialContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import lombok.Getter;
import lombok.Setter;


/**
 *
 * @author Temitope
 */
@Getter
@Setter
public class AppParams {

    private String Ofsuser;
    private String Ofspass;
    private String ImageBase;
    private String ISOofsSource;
    private String LogDir;
    private String listeningDir;
    private String Host;
    private int port;
    private String OFSsource;
    private String T24Framework;
    InputStream propertiesfile;
    private String encryptionserver;
    private int encryptionport;
    private String encryptionkey;
    private String DBuser;
    private String DBpass;
    private String DBserver;
    private String bvnEndpoint;
    private String bvnOrgcode;

    public AppParams() {
        try {

            javax.naming.Context ctx = (javax.naming.Context) new InitialContext().lookup("java:comp/env");
            Host = (String) ctx.lookup("HOST");
            port = Integer.parseInt((String) ctx.lookup("PORT"));
            
            OFSsource = (String) ctx.lookup("OFSsource");
            Ofsuser = (String) ctx.lookup("OFSuser");
            Ofspass = (String) ctx.lookup("OFSpass");
//            ImageBase = (String) ctx.lookup("ImageBase");
//            DBuser = (String) ctx.lookup("DBuser");
//            DBpass = (String) ctx.lookup("DBpass");
//            DBserver = (String) ctx.lookup("DBserver");
//            
//                
//            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//            propertiesfile = classLoader.getResourceAsStream("nip/tools/interfacelogger.properties");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
 

    public static String escape(String text) {

        return text.replace("&", "&amp;").replace("\"", "&quot;").replace("'", "&apos;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public String generateSessionID(String instcode) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");

        Date now = new Date();

        String date = sdf.format(now);
        String uniquenumber = GenerateRandomNumber(12);

        String sessionid = instcode + date + uniquenumber;

        return sessionid;

    }

// public  String GenerateRandomNumber(int charLength) {
//        return String.valueOf(charLength < 1 ? 0 : new Random()
//                .nextInt((9 * (int) Math.pow(10, charLength - 1)) - 1)
//                + (int) Math.pow(10, charLength - 1));
//    }
    public String GenerateRandomNumber(int charLength) {

        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < charLength; i++) {
            sb.append(String.valueOf(rand.nextInt(9)));
        }

        return sb.toString();
    }

    public NIBBsResponseCodes getNIBBsCode(String message) {

        NIBBsResponseCodes respcode = NIBBsResponseCodes.System_malfunction;

        message = message.toLowerCase();

        if (message.contains("ACCOUNT RECORD MISSING".toLowerCase()) || message.contains("found that matched the selection criteria")) {
            respcode = NIBBsResponseCodes.Invalid_Account;
        }

        if (message.contains("is inactive")) {
            respcode = NIBBsResponseCodes.Dormant_Account;
        }

        if (message.contains("IS FLAGGED FOR ONLINE CLOSURE".toLowerCase())) {
            respcode = NIBBsResponseCodes.Invalid_Account;
        }

        if (message.contains("Insolvent".toLowerCase())) {
            respcode = NIBBsResponseCodes.Do_not_honor;
        }

        if (message.contains("Unauthorised overdraft".toLowerCase())) {
            respcode = NIBBsResponseCodes.No_sufficient_funds;
        }

        return respcode;
    }

   

    public String get_SHA_512_Hash(String StringToHash, String salt) throws Exception {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(StringToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw (e);
        }
        return generatedPassword;
    }

//    public static void main(String [] args){
//        
//   //   ISOResponse sd = new ISOResponse();
//     AppParams param = new AppParams();
////        sd.setErrorMessgae("");
////        sd.setISOMessage("xfsss");
////        sd.setIsSuccessful(Boolean.TRUE);
////        
////        String xml = param.ObjectToXML(sd);
//        
//        try{
//            
//            String sed = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
//"<AccountUnblockRequest>\n" +
//"<SessionID>000001100913103301000000000001</SessionID>\n" +
//"<DestinationInstitutionCode>000002</DestinationInstitutionCode>\n" +
//"<ChannelCode>7</ChannelCode>\n" +
//"<ReferenceCode>xxxxxxxxxxxxxxx</ReferenceCode>\n" +
//"<TargetAccountName>Ajibade Oluwasegun</TargetAccountName>\n" +
//"<TargetBankVerificationNumber>1033000442</TargetBankVerificationNumber>\n" +
//"<TargetAccountNumber>2222002345</TargetAccountNumber>\n" +
//"<ReasonCode>0001</ReasonCode>\n" +
//"<Narration>Transfer from 000002 to 0YY</Narration>\n" +
//"</AccountUnblockRequest>";
//            
//        AccountUnblockRequest ds = (AccountUnblockRequest) param.XMLToObject(sed, new AccountUnblockRequest());
//        String sss = param.ObjectToXML(ds);
//       ds =  (AccountUnblockRequest) param.XMLToObject(sss, new AccountUnblockRequest());
//        ds.getChannelCode();
//        }
//        catch(Exception d){
//           System.out.println(d.getMessage());
//        }
//        
//    }
//    
    public NIBBsResponseCodes getResponseObject(String code) {
        NIBBsResponseCodes respcode;
        switch (code.trim()) {

            default:
                respcode = NIBBsResponseCodes.System_malfunction;
                break;

            case "00":

                respcode = NIBBsResponseCodes.SUCCESS;

                break;

            case "01":

                respcode = NIBBsResponseCodes.Status_unknown;

                break;

            case "03":

                respcode = NIBBsResponseCodes.Invalid_Sender;

                break;

            case "05":

                respcode = NIBBsResponseCodes.Do_not_honor;

                break;

            case "07":

                respcode = NIBBsResponseCodes.Invalid_Account;

                break;

            case "08":

                respcode = NIBBsResponseCodes.Account_Name_Mismatch;

                break;

            case "09":

                respcode = NIBBsResponseCodes.Request_processing_in_progress;

                break;

            case "12":

                respcode = NIBBsResponseCodes.Invalid_transaction;

                break;

            case "13":

                respcode = NIBBsResponseCodes.Invalid_Amount;

                break;

            case "14":

                respcode = NIBBsResponseCodes.Invalid_Batch_Number;

                break;

            case "15":

                respcode = NIBBsResponseCodes.Invalid_Session_or_Record_ID;

                break;

            case "16":

                respcode = NIBBsResponseCodes.Unknown_Bank_Code;

                break;

            case "17":

                respcode = NIBBsResponseCodes.Invalid_Channel;

                break;

            case "18":

                respcode = NIBBsResponseCodes.Wrong_Method_Call;

                break;

            case "21":

                respcode = NIBBsResponseCodes.No_action_taken;

                break;

            case "25":

                respcode = NIBBsResponseCodes.Unable_to_locate_record;

                break;

            case "26":

                respcode = NIBBsResponseCodes.Duplicate_record;

                break;

            case "30":

                respcode = NIBBsResponseCodes.Format_error;

                break;

            case "35":

                respcode = NIBBsResponseCodes.Contact_sending_bank;

                break;

            case "51":

                respcode = NIBBsResponseCodes.No_sufficient_funds;

                break;

            case "57":

                respcode = NIBBsResponseCodes.Transaction_not_permitted_to_sender;

                break;

            case "58":

                respcode = NIBBsResponseCodes.Transaction_not_permitted_on_channel;

                break;

            case "61":

                respcode = NIBBsResponseCodes.Transfer_limit_Exceeded;

                break;

            case "63":

                respcode = NIBBsResponseCodes.Security_violation;

                break;

            case "65":

                respcode = NIBBsResponseCodes.Exceeds_withdrawal_frequency;

                break;

            case "69":

                respcode = NIBBsResponseCodes.Unsuccessful_Account_Amount_block;

                break;

            case "70":

                respcode = NIBBsResponseCodes.Unsuccessful_Account_Amount_unblock;

                break;

            case "71":

                respcode = NIBBsResponseCodes.Empty_Mandate_Reference_Number;

                break;

            case "91":

                respcode = NIBBsResponseCodes.Beneficiary_Bank_not_available;

                break;

            case "92":

                respcode = NIBBsResponseCodes.Routing_error;

                break;

            case "94":

                respcode = NIBBsResponseCodes.Duplicate_transaction;

                break;

            case "96":

                respcode = NIBBsResponseCodes.System_malfunction;

                break;

            case "97":

                respcode = NIBBsResponseCodes.Timeout;

                break;

        }

        return respcode;
    }


    

    
}
