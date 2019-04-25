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
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import javax.naming.InitialContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Level;

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

    public String escape(String text) {

        return text.replace(",", "?").replace("\"", "&quot;").replace("'", "&apos;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public double Amount1(int loanamt, int tenor) {
        return (loanamt / tenor);
    }

    public double Repayment(int loanamt, double interestrate) {
        double TotalRepayment = (interestrate / 100 * loanamt);
        return loanamt + TotalRepayment;
    }
    
    
    public double TotalInstallments(int loanamt, double interestrate) {
        double TotalRepayment = (interestrate / 100 * loanamt);
        return TotalRepayment;
    }

    public double Amount2(double intrate, int tenor, int principal) {
        double InterestRate = (intrate / tenor);
        return (principal * InterestRate / 100);
    }

    public int monthsBetween(Date a, Date b) {
        Calendar cal = Calendar.getInstance();
        if (a.before(b)) {
            cal.setTime(a);
        } else {
            cal.setTime(b);
            b = a;
        }
        int c = 0;
        while (cal.getTime().before(b)) {
            cal.add(Calendar.MONTH, 1);
            c++;
        }
        return c;
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

    public ResponseCodes getNIBBsCode(String message) {

        ResponseCodes respcode = ResponseCodes.System_malfunction;

        message = message.toLowerCase();

        if (message.contains("ACCOUNT RECORD MISSING".toLowerCase()) || message.contains("found that matched the selection criteria")) {
            respcode = ResponseCodes.Invalid_Account;
        }

        if (message.contains("is inactive")) {
            respcode = ResponseCodes.Dormant_Account;
        }

        if (message.contains("IS FLAGGED FOR ONLINE CLOSURE".toLowerCase())) {
            respcode = ResponseCodes.Invalid_Account;
        }

        if (message.contains("Insolvent".toLowerCase())) {
            respcode = ResponseCodes.Do_not_honor;
        }

        if (message.contains("Unauthorised overdraft".toLowerCase())) {
            respcode = ResponseCodes.No_sufficient_funds;
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
    public ResponseCodes getResponseObject(String code) {
        ResponseCodes respcode;
        switch (code.trim()) {

            default:
                respcode = ResponseCodes.System_malfunction;
                break;

            case "00":

                respcode = ResponseCodes.SUCCESS;

                break;

            case "01":

                respcode = ResponseCodes.Status_unknown;

                break;

            case "03":

                respcode = ResponseCodes.Invalid_Sender;

                break;

            case "05":

                respcode = ResponseCodes.Do_not_honor;

                break;

            case "07":

                respcode = ResponseCodes.Invalid_Account;

                break;

            case "08":

                respcode = ResponseCodes.Account_Name_Mismatch;

                break;

            case "09":

                respcode = ResponseCodes.Request_processing_in_progress;

                break;

            case "12":

                respcode = ResponseCodes.Invalid_transaction;

                break;

            case "13":

                respcode = ResponseCodes.Invalid_Amount;

                break;

            case "14":

                respcode = ResponseCodes.Invalid_Batch_Number;

                break;

            case "15":

                respcode = ResponseCodes.Invalid_Session_or_Record_ID;

                break;

            case "16":

                respcode = ResponseCodes.Unknown_Bank_Code;

                break;

            case "17":

                respcode = ResponseCodes.Invalid_Channel;

                break;

            case "18":

                respcode = ResponseCodes.Wrong_Method_Call;

                break;

            case "21":

                respcode = ResponseCodes.No_action_taken;

                break;

            case "25":

                respcode = ResponseCodes.Unable_to_locate_record;

                break;

            case "26":

                respcode = ResponseCodes.Duplicate_record;

                break;

            case "30":

                respcode = ResponseCodes.Format_error;

                break;

            case "35":

                respcode = ResponseCodes.Contact_sending_bank;

                break;

            case "51":

                respcode = ResponseCodes.No_sufficient_funds;

                break;

            case "57":

                respcode = ResponseCodes.Transaction_not_permitted_to_sender;

                break;

            case "58":

                respcode = ResponseCodes.Transaction_not_permitted_on_channel;

                break;

            case "61":

                respcode = ResponseCodes.Transfer_limit_Exceeded;

                break;

            case "63":

                respcode = ResponseCodes.Security_violation;

                break;

            case "65":

                respcode = ResponseCodes.Exceeds_withdrawal_frequency;

                break;

            case "69":

                respcode = ResponseCodes.Unsuccessful_Account_Amount_block;

                break;

            case "70":

                respcode = ResponseCodes.Unsuccessful_Account_Amount_unblock;

                break;

            case "71":

                respcode = ResponseCodes.Empty_Mandate_Reference_Number;

                break;

            case "91":

                respcode = ResponseCodes.Beneficiary_Bank_not_available;

                break;

            case "92":

                respcode = ResponseCodes.Routing_error;

                break;

            case "94":

                respcode = ResponseCodes.Duplicate_transaction;

                break;

            case "96":

                respcode = ResponseCodes.System_malfunction;

                break;

            case "97":

                respcode = ResponseCodes.Timeout;

                break;

        }

        return respcode;
    }

    public Object XMLToObject(String xml, Object object) throws Exception {
        try {
            JAXBContext jcontext = JAXBContext.newInstance(object.getClass());
            Unmarshaller um = jcontext.createUnmarshaller();

            //InputSource source = new InputSource(new StringReader(xml));
            return um.unmarshal(new StringInputStream(xml));

        } catch (Exception y) {
            throw (y);
        }
    }

    public String ObjectToXML(Object object) {
        try {
            JAXBContext jcontext = JAXBContext.newInstance(object.getClass());
            Marshaller m = jcontext.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter sw = new StringWriter();

            m.marshal(object, sw);

            return sw.toString();
        } catch (Exception y) {

            return "";
        }

    }

}
